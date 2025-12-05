package com.renato.springbootstrap.security.service

import com.renato.springbootstrap.exception.NotFoundException
import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity
import com.renato.springbootstrap.security.exception.UserAlreadyExistsException
import com.renato.springbootstrap.security.repository.RoleRepository
import com.renato.springbootstrap.security.repository.UserRepository
import com.renato.springbootstrap.security.utils.JwtUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserServiceImpl (
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val encoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val authenticationManager: AuthenticationManager
) : UserService {
    override fun addRole(username: String, role: String) {
        roleRepository.save(RoleEntity(
            roleId = UUID.randomUUID(),
            role = role,
            user = getUserByUsername(username))
        )
    }

    override fun removeRole(username: String, role: String) {
        roleRepository.deleteByUserAndRole(role = role, user = getUserByUsername(username))
    }

    override fun getUserByUserId(userId: Long): UserEntity {
        return userRepository
            .findById(userId)
            .orElseThrow(NotFoundException(String.format("User %d cannot be found", userId)))
    }

    override fun getUserByUsername(username: String): UserEntity {
        return userRepository
            .findByUsername(username)
            ?: throw NotFoundException(String.format("User %s cannot be found", username))
    }

    override fun getUsers(pageable : Pageable): Page<UserEntity> {
        return userRepository.findAll(pageable)
    }

    override fun authenticate(username : String, password : String): String {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )

        SecurityContextHolder.getContext().authentication = authentication

        return jwtUtils.generateJwtToken(authentication)
    }

    override fun me(): UserSecurity {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication?.principal is UserSecurity) {
            authentication.principal as UserSecurity
        } else {
            throw AccessDeniedException("User not authenticated")
        }
    }

    override fun createUser(username: String, password: String, email: String): UserEntity {
        if (userExists(username, email)) {
            throw UserAlreadyExistsException()
        }


        val user = UserEntity(
            id = null,
            userId = UUID.randomUUID(),
            username = username,
            email = email,
            password = encoder.encode(password).toString(),
            roles = Collections.emptyList()
        )
        
        val savedUser = userRepository.save(user);
        val role = addRole(userEntity = savedUser);



        return savedUser.copy(roles = listOf(role));
    }

    override fun updateUser(email: String, password: String): UserEntity {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication?.principal is UserSecurity) {
            val authDetails = authentication.principal as UserSecurity
            val myUser = getUserByUsername(username = authDetails.username)
            val updatedUser = myUser.copy(email = email, password = encoder.encode(password).toString());
            return userRepository.save(updatedUser)
        }
        throw IllegalArgumentException("Cannot update user details")
    }

    override fun findAllRolesByUsername(username: String): List<RoleEntity> {
        return roleRepository.findAllByUser_Username(username)
    }

    private fun addRole(role: String = "USER", userEntity: UserEntity): RoleEntity {
        return roleRepository.save(
            RoleEntity(
                id = null,
                UUID.randomUUID(),
                userEntity,
                role
            )
        )
    }

    private fun userExists(username: String, email: String): Boolean {
        return userRepository.existsByUsernameOrEmail(username, email)
    }
}