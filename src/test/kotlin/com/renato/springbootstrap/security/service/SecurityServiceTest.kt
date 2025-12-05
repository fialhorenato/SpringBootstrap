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
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
class SecurityServiceTest {
    @InjectMocks
    lateinit var securityService: UserServiceImpl

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

    @Test
    @DisplayName("Add role must call the repository once")
    fun addRoleSanity() {
        val user = UserFactory.generateUser()
        Mockito.`when`(userRepository.findByUsername("username")).thenReturn(user)
        securityService.addRole("username", "ADMIN")
        Mockito.verify(roleRepository).save(Mockito.any(RoleEntity::class.java))
    }

    @Test
    @DisplayName("Remove role must call the repository once")
    fun removeRoleSanity() {
        val user = UserFactory.generateUser()
        Mockito.`when`(userRepository.findByUsername("username")).thenReturn(user)
        securityService.removeRole("username", "ADMIN")
        Mockito.verify(roleRepository).deleteByUserAndRole(user = user, role = "ADMIN")
    }

    @Test
    @DisplayName("Get user by id must not throw error")
    fun getUserByUserIdSanity() {
        val user = UserFactory.generateUser()
        Mockito.`when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        assertDoesNotThrow {securityService.getUserByUserId(1L) }
    }

    @Test
    @DisplayName("Get non existing user by id must throw error")
    fun getUserByUserIdNotExistentSanity() {
        Mockito.`when`(userRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<NotFoundException> {securityService.getUserByUserId(1L) }
    }

    @Test
    @DisplayName("Get user by username must not throw error")
    fun getUserByUsernameSanity() {
        val user = UserFactory.generateUser()
        Mockito.`when`(userRepository.findByUsername("username")).thenReturn(user)
        assertDoesNotThrow { securityService.getUserByUsername("username") }
    }

    @Test
    @DisplayName("Get non existing user by username must throw error")
    fun getUserByUsernameNotExistentSanity() {
        Mockito.`when`(userRepository.findByUsername("username")).thenReturn(null)
        assertThrows<NotFoundException> { securityService.getUserByUsername("username") }
    }

    @Test
    @DisplayName("Get users paged sanity")
    fun getUsersSanity() {
        val user = UserFactory.generateUser()
        val page = PageImpl(listOf(user))
        Mockito.`when`(userRepository.findAll(Pageable.unpaged())).thenReturn(page)
        val returnedPage = securityService.getUsers(Pageable.unpaged())
        Assertions.assertThat(returnedPage.size).isEqualTo(1)
    }

    @Test
    @DisplayName("Authentication sanity")
    fun authenticateSanity() {
        val principal = UserSecurity(1L, UUID.randomUUID(), "username", "password", "email", emptyList(), emptyList())
        val authentication = UsernamePasswordAuthenticationToken(principal, "password", emptyList())
        Mockito.`when`(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken::class.java))).thenReturn(authentication)
        securityService.authenticate("username", "password")
        Mockito.verify(jwtUtils).generateJwtToken(authentication)
    }

    @Test
    @DisplayName("Me sanity")
    fun meSanity() {
        val principal = UserSecurity(1L, UUID.randomUUID(), "username", "password", "email", emptyList(), emptyList())
        val authentication = UsernamePasswordAuthenticationToken(principal, "password", emptyList())
        SecurityContextHolder.getContext().authentication = authentication
        assertDoesNotThrow { securityService.me() }
    }

    @Test
    @DisplayName("Update sanity")
    fun updateSanity() {
        // Given
        val user = UserFactory.generateUser()
        val principal = UserSecurity(1L, UUID.randomUUID(),"username", "password", "email", emptyList(), emptyList())
        val authentication = UsernamePasswordAuthenticationToken(principal, "password", emptyList())

        // When
        Mockito.`when`(userRepository.findByUsername("username")).thenReturn(user)
        Mockito.`when`(encoder.encode(user.password)).thenReturn("passwordEncoded")
        Mockito.`when`(userRepository.save(Mockito.any(UserEntity::class.java))).thenReturn(user)
        SecurityContextHolder.getContext().authentication = authentication

        // Then
        assertDoesNotThrow { securityService.updateUser("email", "password") }
    }

    @Test
    @DisplayName("Update throw exception")
    fun updateThrowExceptionSanity() {
        // Given
        val authentication = UsernamePasswordAuthenticationToken(null, null)

        SecurityContextHolder.getContext().authentication = authentication

        // Then
        assertThrows<IllegalArgumentException> { securityService.updateUser("email", "password") }
    }

    @Test
    @DisplayName("Me not existing sanity")
    fun meNotExistingSanity() {
        val authentication = UsernamePasswordAuthenticationToken(null, null)
        SecurityContextHolder.getContext().authentication = authentication
        assertThrows<AccessDeniedException> { securityService.me() }
    }

    @Test
    @DisplayName("Create user sanity")
    fun createUserSanity() {
        val user = UserFactory.generateUser()
        val role = RoleFactory.generateRole(user)
        Mockito.`when`(encoder.encode(user.password)).thenReturn("passwordEncoded")
        Mockito.`when`(userRepository.existsByUsernameOrEmail(user.username, user.email)).thenReturn(false)
        Mockito.`when`(userRepository.save(Mockito.any(UserEntity::class.java))).thenReturn(user)
        Mockito.`when`(roleRepository.save(Mockito.any(RoleEntity::class.java))).thenReturn(role)
        assertDoesNotThrow { securityService.createUser(user.username, user.password, user.email) }
    }

    @Test
    @DisplayName("Create user alerady exists sanity")
    fun createUserAlreadyExistsSanity() {
        val user = UserFactory.generateUser()
        Mockito.`when`(userRepository.existsByUsernameOrEmail(user.username, user.email)).thenReturn(true)
        assertThrows<UserAlreadyExistsException> { securityService.createUser(user.username, user.password, user.email) }
    }
}