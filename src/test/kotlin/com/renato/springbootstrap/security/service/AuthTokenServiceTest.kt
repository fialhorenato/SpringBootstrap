package com.renato.springbootstrap.security.service

import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.exception.JwtException
import com.renato.springbootstrap.security.utils.JwtUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AuthTokenServiceTest {

    @Mock
    lateinit var jwtUtils: JwtUtils

    @Mock
    lateinit var refreshTokenStore: RefreshTokenStore

    @InjectMocks
    lateinit var authTokenService: AuthTokenService

    @Test
    fun `given_valid_access_token_when_issue_from_access_token_then_refresh_token_is_generated_and_stored`() {
        val userDetails = userDetails()
        `when`(jwtUtils.validateJwtToken("access-token")).thenReturn(true)
        `when`(jwtUtils.toUserDetails("access-token")).thenReturn(userDetails)
        `when`(jwtUtils.generateRefreshJwtToken(anyAuthentication())).thenReturn("refresh-token")

        val response = authTokenService.issueFromAccessToken("access-token")

        assertThat(response.token).isEqualTo("access-token")
        assertThat(response.refreshToken).isEqualTo("refresh-token")
        verify(jwtUtils).validateJwtToken("access-token")
        verify(jwtUtils).toUserDetails("access-token")
        verify(jwtUtils).generateRefreshJwtToken(anyAuthentication())
        verify(refreshTokenStore).store("refresh-token", userDetails.username)
    }

    @Test
    fun `given_missing_refresh_token_when_refresh_is_called_then_unauthorized_error_is_thrown`() {
        assertThrows<JwtException> {
            authTokenService.refresh(null)
        }

    }

    @Test
    fun `given_invalid_refresh_token_when_refresh_is_called_then_token_is_invalidated_and_unauthorized_is_thrown`() {
        `when`(jwtUtils.validateJwtToken("invalid-token")).thenReturn(false)

        assertThrows<JwtException> {
            authTokenService.refresh("invalid-token")
        }

        verify(jwtUtils).validateJwtToken("invalid-token")
        verify(refreshTokenStore).invalidate("invalid-token")
    }

    @Test
    fun `given_known_valid_refresh_token_when_refresh_is_called_then_old_token_is_consumed_and_new_tokens_are_issued`() {
        val userDetails = userDetails(username = "refresh-user")
        `when`(jwtUtils.validateJwtToken("refresh-token")).thenReturn(true)
        `when`(jwtUtils.toUserDetails("refresh-token")).thenReturn(userDetails)
        `when`(refreshTokenStore.consume("refresh-token")).thenReturn("refresh-user")
        `when`(jwtUtils.generateJwtToken(anyAuthentication(), anyLong())).thenReturn("new-access-token")
        `when`(jwtUtils.generateRefreshJwtToken(anyAuthentication())).thenReturn("new-refresh-token")

        val response = authTokenService.refresh("refresh-token")

        assertThat(response.token).isEqualTo("new-access-token")
        assertThat(response.refreshToken).isEqualTo("new-refresh-token")
        verify(jwtUtils).validateJwtToken("refresh-token")
        verify(jwtUtils).toUserDetails("refresh-token")
        verify(refreshTokenStore).consume("refresh-token")
        verify(jwtUtils).generateJwtToken(anyAuthentication(), anyLong())
        verify(jwtUtils).generateRefreshJwtToken(anyAuthentication())
        verify(refreshTokenStore).store("new-refresh-token", "refresh-user")
    }

    @Test
    fun `given_consumed_refresh_token_when_refresh_is_called_then_unauthorized_error_is_thrown`() {
        val userDetails = userDetails()
        `when`(jwtUtils.validateJwtToken("refresh-token")).thenReturn(true)
        `when`(jwtUtils.toUserDetails("refresh-token")).thenReturn(userDetails)
        `when`(refreshTokenStore.consume("refresh-token")).thenReturn(null)

        assertThrows<JwtException> {
            authTokenService.refresh("refresh-token")
        }

        verify(jwtUtils).validateJwtToken("refresh-token")
        verify(jwtUtils).toUserDetails("refresh-token")
        verify(refreshTokenStore).consume("refresh-token")
    }

    @Test
    fun `given_refresh_token_with_mismatched_subject_when_refresh_is_called_then_unauthorized_error_is_thrown`() {
        val userDetails = userDetails(username = "token-user")
        `when`(jwtUtils.validateJwtToken("refresh-token")).thenReturn(true)
        `when`(jwtUtils.toUserDetails("refresh-token")).thenReturn(userDetails)
        `when`(refreshTokenStore.consume("refresh-token")).thenReturn("different-user")

        assertThrows<JwtException> {
            authTokenService.refresh("refresh-token")
        }

        verify(jwtUtils).validateJwtToken("refresh-token")
        verify(jwtUtils).toUserDetails("refresh-token")
        verify(refreshTokenStore).consume("refresh-token")
    }

    private fun userDetails(username: String = "username"): UserSecurity {
        return UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = username,
            password = "password",
            email = "$username@email.com",
            authorities = emptyList(),
            roles = listOf("USER"),
        )
    }

    private fun anyAuthentication(): Authentication {
        return any(Authentication::class.java) ?: UsernamePasswordAuthenticationToken("user", null, emptyList())
    }
}
