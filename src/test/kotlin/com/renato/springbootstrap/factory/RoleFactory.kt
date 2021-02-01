package com.renato.springbootstrap.factory

import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity

class RoleFactory {
    companion object {
        fun generateRole(user: UserEntity?, role: String = "USER"): RoleEntity {
            return RoleEntity(user = user, role = role)
        }
    }
}