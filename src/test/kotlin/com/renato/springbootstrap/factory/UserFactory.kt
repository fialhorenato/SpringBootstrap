package com.renato.springbootstrap.factory

import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity
import java.time.Instant
import java.util.UUID

object UserFactory {
    fun createUser(
        username: String = "username",
        email: String = "email",
        password: String = "password",
        roles: List<RoleEntity> = emptyList(),
        createdAt: Instant = Instant.now(),
        updatedAt: Instant = Instant.now(),
        userId: UUID = UUID.randomUUID(),
    ): UserEntity {
        return UserEntity(
            userId = userId,
            username = username,
            email = email,
            password = password,
            roles = roles,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}
