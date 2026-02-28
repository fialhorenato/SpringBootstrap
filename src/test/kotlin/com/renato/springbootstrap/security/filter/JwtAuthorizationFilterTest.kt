package com.renato.springbootstrap.security.filter

import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.utils.JwtUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class JwtAuthorizationFilterTest {

    @Mock
    lateinit var jwtUtils: JwtUtils

    @InjectMocks
    lateinit var jwtAuthorizationFilter: JwtAuthorizationFilter

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `given_valid_bearer_token_when_filter_executes_then_request_is_authenticated`() {
        val request = org.mockito.Mockito.mock(HttpServletRequest::class.java)
        val response = org.mockito.Mockito.mock(HttpServletResponse::class.java)
        val filterChain = org.mockito.Mockito.mock(FilterChain::class.java)
        val userDetails = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "password",
            email = "email",
            authorities = emptyList(),
            roles = listOf("USER"),
        )

        `when`(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token")
        `when`(jwtUtils.validateJwtToken("token")).thenReturn(true)
        `when`(jwtUtils.toUserDetails("token")).thenReturn(userDetails)

        jwtAuthorizationFilter.doFilter(request, response, filterChain)

        assertThat(SecurityContextHolder.getContext().authentication?.principal).isEqualTo(userDetails)
        verify(jwtUtils).validateJwtToken("token")
        verify(jwtUtils).toUserDetails("token")
        verify(filterChain).doFilter(request, response)
        verifyNoMoreInteractions(jwtUtils, filterChain)
    }

    @Test
    fun `given_invalid_bearer_token_when_filter_executes_then_request_is_not_authenticated`() {
        val request = org.mockito.Mockito.mock(HttpServletRequest::class.java)
        val response = org.mockito.Mockito.mock(HttpServletResponse::class.java)
        val filterChain = org.mockito.Mockito.mock(FilterChain::class.java)

        `when`(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token")
        `when`(jwtUtils.validateJwtToken("token")).thenReturn(false)

        jwtAuthorizationFilter.doFilter(request, response, filterChain)

        assertThat(SecurityContextHolder.getContext().authentication).isNull()
        verify(jwtUtils).validateJwtToken("token")
        verify(jwtUtils, never()).toUserDetails(anyString())
        verify(filterChain).doFilter(request, response)
        verifyNoMoreInteractions(jwtUtils, filterChain)
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = ["", "Basic abc", "Token xyz"])
    fun `given_missing_or_non_bearer_header_when_filter_executes_then_jwt_processing_is_skipped`(header: String?) {
        val request = org.mockito.Mockito.mock(HttpServletRequest::class.java)
        val response = org.mockito.Mockito.mock(HttpServletResponse::class.java)
        val filterChain = org.mockito.Mockito.mock(FilterChain::class.java)

        `when`(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(header)

        jwtAuthorizationFilter.doFilter(request, response, filterChain)

        verify(jwtUtils, never()).validateJwtToken(anyString())
        verify(jwtUtils, never()).toUserDetails(anyString())
        verify(filterChain).doFilter(request, response)
        verifyNoMoreInteractions(jwtUtils, filterChain)
    }
}
