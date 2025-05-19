package com.renato.springbootstrap.factory

import com.renato.springbootstrap.security.entity.RoleEntity
import com.renato.springbootstrap.security.entity.UserEntity
import java.util.UUID

class RoleFactory {
    companion object {
        fun generateRole(user: UserEntity, role: String = "USER", roleId : UUID = UUID.randomUUID(),): RoleEntity {
            return RoleEntity(roleId = roleId, user = user, role = role)
        }
    }
}