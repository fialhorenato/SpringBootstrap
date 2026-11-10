package com.renato.springbootstrap.security.service

import com.renato.springbootstrap.security.api.response.AuthTokenResponseDTO
import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.exception.JwtException
import com.renato.springbootstrap.security.utils.JwtUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthTokenService(
    private val jwtUtils: JwtUtils,
    private val refreshTokenStore: RefreshTokenStore,
) {
    fun issueFromAccessToken(accessToken: String): AuthTokenResponseDTO {
        if (!jwtUtils.validateJwtToken(accessToken)) {
            throw JwtException("Invalid token")
        }

        val authentication = toAuthentication(jwtUtils.toUserDetails(accessToken))
        val refreshToken = jwtUtils.generateRefreshJwtToken(authentication)
        refreshTokenStore.store(refreshToken, authentication.name)
        return AuthTokenResponseDTO(token = accessToken, refreshToken = refreshToken)
    }

    fun refresh(refreshToken: String?): AuthTokenResponseDTO {
        val normalizedToken = refreshToken?.trim()
        if (normalizedToken.isNullOrBlank()) {
            throw JwtException("Refresh token is required")
        }

        if (!jwtUtils.validateJwtToken(normalizedToken)) {
            refreshTokenStore.invalidate(normalizedToken)
            throw JwtException("Invalid or expired refresh token")
        }

        val userDetails = jwtUtils.toUserDetails(normalizedToken)
        val consumedUsername = refreshTokenStore.consume(normalizedToken)
            ?: throw JwtException("Invalid or expired refresh token")
        if (consumedUsername != userDetails.username) {
            throw JwtException("Invalid or expired refresh token")
        }

        val authentication = toAuthentication(userDetails)
        val newAccessToken = jwtUtils.generateJwtToken(authentication)
        val newRefreshToken = jwtUtils.generateRefreshJwtToken(authentication)
        refreshTokenStore.store(newRefreshToken, userDetails.username)

        return AuthTokenResponseDTO(token = newAccessToken, refreshToken = newRefreshToken)
    }

    private fun toAuthentication(userDetails: UserSecurity): UsernamePasswordAuthenticationToken {
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }
}
