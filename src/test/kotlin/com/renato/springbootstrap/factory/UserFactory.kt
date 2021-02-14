package com.renato.springbootstrap.factory

import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity
import java.time.Instant
import java.time.Instant.now

class UserFactory {
    companion object {
        fun generateUser(username : String = "username", email : String = "email", password : String ="password", roles : List<RoleEntity> = emptyList(), createdAt : Instant = now(), updatedAt: Instant = now()): UserEntity {
            return UserEntity(username =  username, email = email, password = password, roles = roles, createdAt = createdAt, updatedAt = updatedAt)
        }
    }
}