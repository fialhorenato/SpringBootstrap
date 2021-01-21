package com.renato.springbootstrap.security.api.response

import com.renato.springbootstrap.security.entity.UserEntity
import com.renato.springbootstrap.security.service.UserDetails


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

    constructor(userDetails : UserDetails) : this(
        userDetails.myUsername ,
        userDetails.email,
        userDetails.roles
    )

}