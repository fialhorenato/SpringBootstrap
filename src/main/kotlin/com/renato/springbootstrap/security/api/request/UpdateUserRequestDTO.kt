package com.renato.springbootstrap.security.api.request

data class UpdateUserRequestDTO(
    val email: String,
    val password: String
)
