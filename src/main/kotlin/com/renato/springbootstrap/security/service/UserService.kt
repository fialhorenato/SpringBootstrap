package com.renato.springbootstrap.security.service

import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {
    fun addRole(username: String, role: String)

    fun removeRole(username: String, role: String)

    fun getUserByUserId(userId: Long): UserEntity

    fun getUserByUsername(username: String): UserEntity

    fun getUsers(pageable : Pageable): Page<UserEntity>

    fun authenticate(username : String, password : String): String

    fun me(): UserSecurity

    fun createUser(username: String, password: String, email: String): UserEntity

    fun updateUser(email: String, password: String) : UserEntity

    fun findAllRolesByUsername(username: String) : List<RoleEntity>
}