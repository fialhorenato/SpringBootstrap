package com.renato.springbootstrap.security.service

import com.renato.springbootstrap.security.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetailsService

interface SecurityService : UserDetailsService {
    fun addRole(username: String, role: String)

    fun removeRole(username: String, role: String)

    fun getUserByUserId(userId: Long): UserEntity

    fun getUserByUsername(username: String): UserEntity

    fun getUsers(pageable : Pageable): Page<UserEntity>

    fun authenticate(username : String, password : String): String

    fun me(): UserDetails

    fun createUser(username: String, password: String, email: String): UserEntity

    fun updateUser(email: String, password: String) : UserEntity
}