package com.renato.springbootstrap.security.api.response

import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.entity.UserEntity


data class UserResponseDTO(
        val username : String,
        val email : String,
        val roles : List<String>
) {

    constructor(user : UserEntity) : this(
        user.username,
        user.email,
        user.roles.map { it.role }.toList()
    )

    constructor(userSecurity: UserSecurity) : this(
        userSecurity.username ,
        userSecurity.email,
        userSecurity.roles
    )

}