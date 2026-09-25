package com.renato.springbootstrap.security.api.request

import com.fasterxml.jackson.annotation.JsonProperty

data class RefreshTokenRequestDTO(
    @JsonProperty("refresh_token")
    val refreshToken: String?,
)
