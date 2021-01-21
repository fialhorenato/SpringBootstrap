package com.renato.springbootstrap.security.service

import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity
import com.renato.springbootstrap.security.repository.RoleRepository
import com.renato.springbootstrap.security.repository.UserRepository
import com.renato.springbootstrap.exception.NotFoundException
import com.renato.springbootstrap.security.api.request.LoginRequestDTO
import com.renato.springbootstrap.security.api.request.SignupRequestDTO
import com.renato.springbootstrap.security.exception.UserAlreadyExistsException
import com.renato.springbootstrap.security.utils.JwtUtils
import com.renato.springbootstrap.security.utils.JwtUtils.Companion.ROLE_PREFIX
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Collections.emptyList
import java.util.Objects.nonNull
import javax.transaction.Transactional
import kotlin.streams.toList

@Service
class SecurityService(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val encoder: PasswordEncoder,
    private val roleRepository: RoleRepository
) : UserDetailsService {
    fun authenticate(loginRequestDTO: LoginRequestDTO): String {
        // Try to authenticate the user with username and password
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequestDTO.username, loginRequestDTO.password))

        // Set the Security context with the authentication
        SecurityContextHolder.getContext().authentication = authentication

        // Generate and return the JWT Token
        return jwtUtils.generateJwtToken(authentication)
    }

    fun me(): UserDetails {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication.principal is UserDetails) {
            authentication.principal as UserDetails
        } else {
            throw AccessDeniedException("User not authenticated")
        }
    }

    fun createUser(signupRequestDTO: SignupRequestDTO): UserEntity {
        if (userExists(signupRequestDTO.username, signupRequestDTO.email)) {
            throw UserAlreadyExistsException()
        }

        val user = UserEntity(id = null, username = signupRequestDTO.username, email = signupRequestDTO.email, password = encoder.encode(signupRequestDTO.password), roles = emptyList())

        user.roles = listOf(toRole(userEntity =  user), toRole("ADMIN", user))

        return userRepository.save(user)
    }

    private fun toRole(role: String = "USER", userEntity: UserEntity): RoleEntity {
        return RoleEntity(null, userEntity, role)
    }

    private fun userExists(username: String, email: String): Boolean {
        return userRepository.existsByUsernameOrEmail(username, email)
    }

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
        return user?.let { toUserDetails(it) } ?: throw UsernameNotFoundException(String.format("Username %s not found", username))
    }

    private fun toUserDetails(userEntity: UserEntity): UserDetails {
        val roles = userEntity.roles
            .filter { nonNull(it) }
            .map { it.role }
            .toList()


        val authorities = roles
                .map { SimpleGrantedAuthority(ROLE_PREFIX + it) }
                .toList()

        return UserDetails(
            userEntity.email,
            userEntity.username,
            userEntity.password,
            authorities,
            roles
        )
    }

    fun addRole(username: String, role: String) {
        roleRepository.save(RoleEntity(role = role, user = getUser(username)))
    }

    fun removeRole(userId: String, role: String) {
        roleRepository.deleteByUserAndRole(role = role, user = getUser(userId))
    }

    fun getUser(userId: Long): UserEntity {
        return userRepository.findById(userId).orElseThrow(NotFoundException(String.format("User %d cannot be found", userId)))
    }

    fun getUser(username: String): UserEntity {
        return userRepository.findByUsername(username) ?: throw NotFoundException(String.format("User %s cannot be found", username))
    }

    fun getUser(pageable : Pageable): Page<UserEntity> {
        return userRepository.findAll(pageable)
    }
}