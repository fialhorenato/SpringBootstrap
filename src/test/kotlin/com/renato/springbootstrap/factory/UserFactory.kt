package com.renato.springbootstrap.factory

import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity
import java.time.Instant
import java.time.Instant.now
import java.util.UUID

class UserFactory {
    companion object {
        fun generateUser(username : String = "username", email : String = "email", password : String ="password", roles : List<RoleEntity> = emptyList(), createdAt : Instant = now(), updatedAt: Instant = now(), userId: UUID = UUID.randomUUID()): UserEntity {
            return UserEntity(
                userId = userId,
                username =  username,
                email = email,
                password = password,
                roles = roles,
                createdAt = createdAt,
                updatedAt = updatedAt)
        }
    }
}