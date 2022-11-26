package com.renato.springbootstrap.security.service

import com.renato.springbootstrap.exception.NotFoundException
import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity
import com.renato.springbootstrap.security.exception.UserAlreadyExistsException
import com.renato.springbootstrap.security.repository.RoleRepository
import com.renato.springbootstrap.security.repository.UserRepository
import com.renato.springbootstrap.security.utils.JwtUtils
import com.renato.springbootstrap.security.utils.JwtUtils.Companion.ROLE_PREFIX
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Collections.emptyList
import java.util.Objects.nonNull

@Service
class SecurityServiceImpl(
        @Lazy private val authenticationManager:  AuthenticationManager,
        private val userRepository: UserRepository,
        private val jwtUtils: JwtUtils,
        private val encoder: PasswordEncoder,
        private val roleRepository: RoleRepository
) : SecurityService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
        return user?.let { toUserDetails(it) } ?: throw UsernameNotFoundException("Username $username not found")
    }

    override fun addRole(username: String, role: String) {
        roleRepository.save(RoleEntity(role = role, user = getUserByUsername(username)))
    }

    override fun removeRole(username: String, role: String) {
        roleRepository.deleteByUserAndRole(role = role, user = getUserByUsername(username))
    }

    @Transactional(readOnly = true)
    override fun getUserByUserId(userId: Long): UserEntity {
        return userRepository.findById(userId).orElseThrow(NotFoundException(String.format("User %d cannot be found", userId)))
    }

    @Transactional(readOnly = true)
    override fun getUserByUsername(username: String): UserEntity {
        return userRepository.findByUsername(username) ?: throw NotFoundException(String.format("User %s cannot be found", username))
    }

    @Transactional(readOnly = true)
    override fun getUsers(pageable : Pageable): Page<UserEntity> {
        return userRepository.findAll(pageable)
    }

    @Transactional(readOnly = true)
    override fun authenticate(username : String, password : String): String {
        // Try to authenticate the user with username and password
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(username, password))

        // Set the Security context with the authentication
        SecurityContextHolder.getContext().authentication = authentication

        // Generate and return the JWT Token
        return jwtUtils.generateJwtToken(authentication)
    }

    override fun me(): UserDetails {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication.principal is UserDetails) {
            authentication.principal as UserDetails
        } else {
            throw AccessDeniedException("User not authenticated")
        }
    }

    override fun createUser(username: String, password: String, email: String): UserEntity {
        if (userExists(username, email)) {
            throw UserAlreadyExistsException()
        }

        val user = UserEntity(id = null, username = username, email = email, password = encoder.encode(password), roles = emptyList())

        // Adding the USER role
        user.roles = listOf(
            toRole(userEntity =  user)
        )

        return userRepository.save(user)
    }

    override fun updateUser(email: String, password: String): UserEntity {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication.principal is UserDetails) {
            val authDetails = authentication.principal as UserDetails
            val myUser = getUserByUsername(username = authDetails.username)
            myUser.email = email
            myUser.password = encoder.encode(password)
            return userRepository.save(myUser)
        }
        throw IllegalArgumentException("Cannot update user details")
    }

    @Transactional(readOnly = true)
    override fun findAllRolesByUsername(username: String): List<RoleEntity> {
        return roleRepository.findAllByUser_Username(username)
    }

    private fun toRole(role: String = "USER", userEntity: UserEntity): RoleEntity {
        return RoleEntity(null, userEntity, role)
    }

    private fun userExists(username: String, email: String): Boolean {
        return userRepository.existsByUsernameOrEmail(username, email)
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
            userEntity.username,
            userEntity.password,
            authorities,
            roles,
            userEntity.email
        )
    }
}