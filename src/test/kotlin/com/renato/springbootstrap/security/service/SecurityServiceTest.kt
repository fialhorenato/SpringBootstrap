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
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockKExtension::class)
class SecurityServiceTest {
    @InjectMockKs
    lateinit var securityService: UserServiceImpl

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var encoder: PasswordEncoder

    @MockK
    lateinit var roleRepository: RoleRepository

    @MockK
    lateinit var authenticationManager: AuthenticationManager

    @MockK
    lateinit var jwtUtils: JwtUtils

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
        clearAllMocks()
    }

    @Nested
    @DisplayName("Add Role Tests")
    inner class AddRoleTests {
        @Test
        @DisplayName("Should successfully add role to existing user")
        fun addRoleSanity() {
            // Given
            val user = UserFactory.generateUser()
            val expectedRole = RoleFactory.generateRole(user, "ADMIN")

            every { userRepository.findByUsername("username") } returns user
            every { roleRepository.save(any<RoleEntity>()) } returns expectedRole

            // When
            securityService.addRole("username", "ADMIN")

            // Then
            verify(exactly = 1) { 
                roleRepository.save(match { 
                    it.role == "ADMIN" && it.user == user 
                }) 
            }
        }
    }

    @Nested
    @DisplayName("Remove Role Tests")
    inner class RemoveRoleTests {
        @Test
        @DisplayName("Should successfully remove role from user")
        fun removeRoleSanity() {
            // Given
            val user = UserFactory.generateUser()

            every { userRepository.findByUsername("username") } returns user
            every { roleRepository.deleteByUserAndRole(user, "ADMIN") } just Runs

            // When
            securityService.removeRole("username", "ADMIN")

            // Then
            verify(exactly = 1) { roleRepository.deleteByUserAndRole(user = user, role = "ADMIN") }
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    inner class GetUserTests {
        @Test
        @DisplayName("Should successfully get user by ID")
        fun getUserByUserIdSanity() {
            // Given
            val user = UserFactory.generateUser()
            every { userRepository.findById(1L) } returns Optional.of(user)

            // When
            val result = securityService.getUserByUserId(1L)

            // Then
            assertThat(result).isNotNull
            assertThat(result.userId).isEqualTo(user.userId)
        }

        @Test
        @DisplayName("Should throw NotFoundException when user ID does not exist")
        fun getUserByUserIdNotExistentSanity() {
            // Given
            every { userRepository.findById(1L) } returns Optional.empty()

            // When / Then
            assertThrows<NotFoundException> { securityService.getUserByUserId(1L) }
        }

        @Test
        @DisplayName("Should successfully get user by username")
        fun getUserByUsernameSanity() {
            // Given
            val user = UserFactory.generateUser()
            every { userRepository.findByUsername("username") } returns user

            // When
            val result = securityService.getUserByUsername("username")

            // Then
            assertThat(result).isNotNull
            assertThat(result.username).isEqualTo("username")
        }

        @Test
        @DisplayName("Should throw NotFoundException when username does not exist")
        fun getUserByUsernameNotExistentSanity() {
            // Given
            every { userRepository.findByUsername("username") } returns null

            // When / Then
            assertThrows<NotFoundException> { securityService.getUserByUsername("username") }
        }

        @Test
        @DisplayName("Should return paginated users")
        fun getUsersSanity() {
            // Given
            val user = UserFactory.generateUser()
            val page = PageImpl(listOf(user))
            every { userRepository.findAll(Pageable.unpaged()) } returns page

            // When
            val returnedPage = securityService.getUsers(Pageable.unpaged())

            // Then
            assertThat(returnedPage.content).hasSize(1)
            assertThat(returnedPage.content.first()).isEqualTo(user)
        }
    }

    @Nested
    @DisplayName("Authentication Tests")
    inner class AuthenticationTests {
        @Test
        @DisplayName("Should successfully authenticate and return JWT token")
        fun authenticateSanity() {
            // Given
            val userId = UUID.randomUUID()
            val principal = UserSecurity(1L, userId, "username", "password", "email", emptyList(), emptyList())
            val authentication = UsernamePasswordAuthenticationToken(principal, "password", emptyList())
            val expectedToken = "jwt-token-123"

            every { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) } returns authentication
            every { jwtUtils.generateJwtToken(authentication) } returns expectedToken

            // When
            val token = securityService.authenticate("username", "password")

            // Then
            assertThat(token).isEqualTo(expectedToken)
            verify(exactly = 1) { jwtUtils.generateJwtToken(authentication) }
        }
    }

    @Nested
    @DisplayName("Me Tests")
    inner class MeTests {
        @Test
        @DisplayName("Should return authenticated user information")
        fun meSanity() {
            // Given
            val userId = UUID.randomUUID()
            val principal = UserSecurity(1L, userId, "username", "password", "email@test.com", emptyList(), emptyList())
            val authentication = UsernamePasswordAuthenticationToken(principal, "password", emptyList())
            SecurityContextHolder.getContext().authentication = authentication

            // When
            val result = securityService.me()

            // Then
            assertThat(result).isNotNull
            assertThat(result.username).isEqualTo("username")
            assertThat(result.email).isEqualTo("email@test.com")
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user is not authenticated")
        fun meNotExistingSanity() {
            // Given
            val authentication = UsernamePasswordAuthenticationToken(null, null)
            SecurityContextHolder.getContext().authentication = authentication

            // When / Then
            assertThrows<AccessDeniedException> { securityService.me() }
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    inner class UpdateUserTests {
        @Test
        @DisplayName("Should successfully update user details")
        fun updateSanity() {
            // Given
            val user = UserFactory.generateUser()
            val userId = UUID.randomUUID()
            val principal = UserSecurity(1L, userId, "username", "password", "email", emptyList(), emptyList())
            val authentication = UsernamePasswordAuthenticationToken(principal, "password", emptyList())
            val updatedUser = user.copy(email = "newemail@test.com")

            every { userRepository.findByUsername("username") } returns user
            every { encoder.encode("newpassword") } returns "passwordEncoded"
            every { userRepository.save(any<UserEntity>()) } returns updatedUser
            SecurityContextHolder.getContext().authentication = authentication

            // When
            val result = securityService.updateUser("newemail@test.com", "newpassword")

            // Then
            assertThat(result.email).isEqualTo("newemail@test.com")
            verify(exactly = 1) { userRepository.save(any<UserEntity>()) }
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when authentication is invalid")
        fun updateThrowExceptionSanity() {
            // Given
            val authentication = UsernamePasswordAuthenticationToken(null, null)
            SecurityContextHolder.getContext().authentication = authentication

            // When / Then
            assertThrows<IllegalArgumentException> { securityService.updateUser("email", "password") }
        }
    }

    @Nested
    @DisplayName("Create User Tests")
    inner class CreateUserTests {
        @Test
        @DisplayName("Should successfully create new user with default role")
        fun createUserSanity() {
            // Given
            val user = UserFactory.generateUser()
            val role = RoleFactory.generateRole(user)

            every { encoder.encode(user.password) } returns "passwordEncoded"
            every { userRepository.existsByUsernameOrEmail(user.username, user.email) } returns false
            every { userRepository.save(any<UserEntity>()) } returns user
            every { roleRepository.save(any<RoleEntity>()) } returns role

            // When
            val result = securityService.createUser(user.username, user.password, user.email)

            // Then
            assertThat(result).isNotNull
            assertThat(result.username).isEqualTo(user.username)
            assertThat(result.email).isEqualTo(user.email)
            assertThat(result.roles).hasSize(1)
            assertThat(result.roles.first().role).isEqualTo("USER")
            verify(exactly = 1) { userRepository.save(any<UserEntity>()) }
            verify(exactly = 1) { roleRepository.save(any<RoleEntity>()) }
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when user already exists")
        fun createUserAlreadyExistsSanity() {
            // Given
            val user = UserFactory.generateUser()
            every { userRepository.existsByUsernameOrEmail(user.username, user.email) } returns true

            // When / Then
            assertThrows<UserAlreadyExistsException> { 
                securityService.createUser(user.username, user.password, user.email) 
            }
            verify(exactly = 0) { userRepository.save(any<UserEntity>()) }
        }
    }

    @Nested
    @DisplayName("Find All Roles Tests")
    inner class FindAllRolesTests {
        @Test
        @DisplayName("Should return all roles for a given username")
        fun findAllRolesByUsernameSanity() {
            // Given
            val user = UserFactory.generateUser()
            val roles = listOf(
                RoleFactory.generateRole(user, "USER"),
                RoleFactory.generateRole(user, "ADMIN")
            )

            every { roleRepository.findAllByUser_Username("username") } returns roles

            // When
            val result = securityService.findAllRolesByUsername("username")

            // Then
            assertThat(result).hasSize(2)
            assertThat(result.map { it.role }).containsExactlyInAnyOrder("USER", "ADMIN")
        }

        @Test
        @DisplayName("Should return empty list when user has no roles")
        fun findAllRolesByUsernameEmptySanity() {
            // Given
            every { roleRepository.findAllByUser_Username("username") } returns emptyList()

            // When
            val result = securityService.findAllRolesByUsername("username")

            // Then
            assertThat(result).isEmpty()
        }
    }
}