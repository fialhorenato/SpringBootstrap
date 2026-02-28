package com.renato.springbootstrap.security.service

import com.renato.springbootstrap.exception.NotFoundException
import com.renato.springbootstrap.factory.RoleFactory
import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity
import com.renato.springbootstrap.security.exception.UserAlreadyExistsException
import com.renato.springbootstrap.security.repository.RoleRepository
import com.renato.springbootstrap.security.repository.UserRepository
import com.renato.springbootstrap.security.utils.JwtUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SecurityServiceTest {

    @InjectMocks
    lateinit var service: UserServiceImpl

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var encoder: PasswordEncoder

    @Mock
    lateinit var roleRepository: RoleRepository

    @Mock
    lateinit var authenticationManager: AuthenticationManager

    @Mock
    lateinit var jwtUtils: JwtUtils

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `given_existing_user_when_add_role_is_called_then_role_is_persisted`() {
        val user = UserFactory.createUser()
        `when`(userRepository.findByUsername("username")).thenReturn(user)

        service.addRole("username", "ADMIN")

        verify(userRepository).findByUsername("username")
        verify(roleRepository).save(any(RoleEntity::class.java))
        verifyNoMoreInteractions(userRepository, roleRepository)
    }

    @Test
    fun `given_existing_user_and_role_when_remove_role_is_called_then_role_is_deleted`() {
        val user = UserFactory.createUser()
        `when`(userRepository.findByUsername("username")).thenReturn(user)

        service.removeRole("username", "ADMIN")

        verify(userRepository).findByUsername("username")
        verify(roleRepository).deleteByUserAndRole(user = user, role = "ADMIN")
        verifyNoMoreInteractions(userRepository, roleRepository)
    }

    @Test
    fun `given_existing_user_id_when_get_user_by_id_is_called_then_user_is_returned`() {
        val user = UserFactory.createUser()
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))

        val result = service.getUserByUserId(1L)

        assertThat(result).isEqualTo(user)
        verify(userRepository).findById(1L)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `given_missing_user_id_when_get_user_by_id_is_called_then_not_found_is_thrown`() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            service.getUserByUserId(1L)
        }

        verify(userRepository).findById(1L)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `given_existing_username_when_get_user_by_username_is_called_then_user_is_returned`() {
        val user = UserFactory.createUser(username = "username")
        `when`(userRepository.findByUsername("username")).thenReturn(user)

        val result = service.getUserByUsername("username")

        assertThat(result).isEqualTo(user)
        verify(userRepository).findByUsername("username")
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `given_missing_username_when_get_user_by_username_is_called_then_not_found_is_thrown`() {
        `when`(userRepository.findByUsername("username")).thenReturn(null)

        assertThrows<NotFoundException> {
            service.getUserByUsername("username")
        }

        verify(userRepository).findByUsername("username")
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `given_pageable_when_get_users_is_called_then_repository_page_is_returned`() {
        val page = PageImpl(listOf(UserFactory.createUser()))
        `when`(userRepository.findAll(Pageable.unpaged())).thenReturn(page)

        val result = service.getUsers(Pageable.unpaged())

        assertThat(result.content).hasSize(1)
        verify(userRepository).findAll(Pageable.unpaged())
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `given_valid_credentials_when_authenticate_is_called_then_token_is_returned_and_context_is_updated`() {
        val principal = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "password",
            email = "email",
            authorities = emptyList(),
            roles = emptyList(),
        )
        val authentication = UsernamePasswordAuthenticationToken(principal, "password", emptyList())

        `when`(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .thenReturn(authentication)
        `when`(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token")

        val token = service.authenticate("username", "password")

        assertThat(token).isEqualTo("jwt-token")
        assertThat(SecurityContextHolder.getContext().authentication).isEqualTo(authentication)
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken::class.java))
        verify(jwtUtils).generateJwtToken(authentication)
        verifyNoMoreInteractions(authenticationManager, jwtUtils)
    }

    @Test
    fun `given_authenticated_principal_when_me_is_called_then_user_security_is_returned`() {
        val principal = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "password",
            email = "email",
            authorities = emptyList(),
            roles = listOf("USER"),
        )
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(principal, null, principal.authorities)

        val result = service.me()

        assertThat(result.username).isEqualTo("username")
        assertThat(result.roles).containsExactly("USER")
    }

    @Test
    fun `given_missing_user_security_principal_when_me_is_called_then_access_denied_is_thrown`() {
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(null, null)

        assertThrows<AccessDeniedException> {
            service.me()
        }
    }

    @Test
    fun `given_null_authentication_when_me_is_called_then_access_denied_is_thrown`() {
        SecurityContextHolder.getContext().authentication = null

        assertThrows<AccessDeniedException> {
            service.me()
        }
    }

    @Test
    fun `given_authenticated_user_when_update_user_is_called_then_email_and_encoded_password_are_persisted`() {
        val existingUser = UserFactory.createUser(username = "username", email = "old@email.com", password = "old")
        val principal = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "old",
            email = "old@email.com",
            authorities = emptyList(),
            roles = listOf("USER"),
        )
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(principal, null, principal.authorities)

        `when`(userRepository.findByUsername("username")).thenReturn(existingUser)
        `when`(encoder.encode("new-password")).thenReturn("encoded-password")
        `when`(userRepository.save(any(UserEntity::class.java))).thenAnswer { it.arguments[0] as UserEntity }

        val updated = service.updateUser("new@email.com", "new-password")

        assertThat(updated.email).isEqualTo("new@email.com")
        assertThat(updated.password).isEqualTo("encoded-password")
        verify(userRepository).findByUsername("username")
        verify(encoder).encode("new-password")
        verify(userRepository).save(any(UserEntity::class.java))
        verifyNoMoreInteractions(userRepository, encoder)
    }

    @Test
    fun `given_non_user_security_principal_when_update_user_is_called_then_illegal_argument_is_thrown`() {
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(null, null)

        assertThrows<IllegalArgumentException> {
            service.updateUser("email", "password")
        }

        verifyNoInteractions(userRepository, encoder)
    }

    @Test
    fun `given_null_authentication_when_update_user_is_called_then_illegal_argument_is_thrown`() {
        SecurityContextHolder.getContext().authentication = null

        assertThrows<IllegalArgumentException> {
            service.updateUser("email", "password")
        }

        verifyNoInteractions(userRepository, encoder)
    }

    @Test
    fun `given_new_user_data_when_create_user_is_called_then_user_and_default_role_are_persisted`() {
        val savedUser = UserFactory.createUser(username = "username", email = "email")
        val savedRole = RoleFactory.createRole(savedUser, "USER")

        `when`(userRepository.existsByUsernameOrEmail("username", "email")).thenReturn(false)
        `when`(encoder.encode("password")).thenReturn("encoded-password")
        `when`(userRepository.save(any(UserEntity::class.java))).thenReturn(savedUser)
        `when`(roleRepository.save(any(RoleEntity::class.java))).thenReturn(savedRole)

        val created = service.createUser("username", "password", "email")

        assertThat(created.username).isEqualTo("username")
        assertThat(created.roles.map { it.role }).containsExactly("USER")
        verify(userRepository).existsByUsernameOrEmail("username", "email")
        verify(encoder).encode("password")
        verify(userRepository).save(any(UserEntity::class.java))
        verify(roleRepository).save(any(RoleEntity::class.java))
        verifyNoMoreInteractions(userRepository, encoder, roleRepository)
    }

    @Test
    fun `given_existing_username_or_email_when_create_user_is_called_then_user_already_exists_is_thrown`() {
        `when`(userRepository.existsByUsernameOrEmail("username", "email")).thenReturn(true)

        assertThrows<UserAlreadyExistsException> {
            service.createUser("username", "password", "email")
        }

        verify(userRepository).existsByUsernameOrEmail("username", "email")
        verifyNoMoreInteractions(userRepository)
        verifyNoInteractions(encoder, roleRepository)
    }

    @Test
    fun `given_username_when_find_all_roles_is_called_then_roles_are_returned`() {
        val user = UserFactory.createUser(username = "username")
        val roles = listOf(RoleFactory.createRole(user, "USER"), RoleFactory.createRole(user, "ADMIN"))
        `when`(roleRepository.findAllByUser_Username("username")).thenReturn(roles)

        val result = service.findAllRolesByUsername("username")

        assertThat(result).hasSize(2)
        assertThat(result.map { it.role }).containsExactlyInAnyOrder("USER", "ADMIN")
        verify(roleRepository).findAllByUser_Username("username")
        verifyNoMoreInteractions(roleRepository)
    }
}
