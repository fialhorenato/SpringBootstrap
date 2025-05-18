package com.renato.springbootstrap.security.api.response

import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.entity.UserEntity
import java.util.UUID


data class UserResponseDTO(
        val username : String,
        val userId : UUID,
        val email : String,
        val roles : List<String>
) {

    constructor(user : UserEntity) : this(
        user.username,
        user.userId,
        user.email,
        user.roles.map { it.role }.toList()
    )

    constructor(userSecurity: UserSecurity) : this(
        userSecurity.username ,
        userSecurity.userId,
        userSecurity.email,
        userSecurity.roles
    )

}