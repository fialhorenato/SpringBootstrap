package com.renato.springbootstrap.factory

import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity

class UserFactory {
    companion object {
        fun generateUser(username : String = "username", email : String = "email", password : String ="password", roles : List<RoleEntity> = emptyList()): UserEntity {
            return UserEntity(username =  username, email = email, password = password, roles = roles)
        }
    }
}