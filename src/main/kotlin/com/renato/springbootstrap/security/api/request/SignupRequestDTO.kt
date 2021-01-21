package com.renato.springbootstrap.security.api.request

data class SignupRequestDTO(
    val username: String,
    val email: String,
    val password: String
)
