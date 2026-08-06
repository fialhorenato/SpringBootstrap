package com.renato.springbootstrap.security.api.response

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthTokenResponseDTO(
    val token: String,
    @JsonProperty("refresh_token")
    val refreshToken: String,
)
