package com.renato.springbootstrap.security.filter

import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.utils.JwtUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class JwtAuthorizationFilterTest {
    @Mock lateinit var jwtUtils: JwtUtils
    @InjectMocks lateinit var jwtAuthorizationFilter: JwtAuthorizationFilter

    @Test
    fun `Sanity Test`() {
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val filterChain = mock(FilterChain::class.java)
        val userDetails = UserSecurity(0L, UUID.randomUUID(),"username", "password", "email", emptyList(), emptyList())

        `when`(request.getHeader("Authorization")).thenReturn("Bearer token.token.token.token")
        `when`(jwtUtils.validateJwtToken("token.token.token.token")).thenReturn(true)
        `when`(jwtUtils.toUserDetails("token.token.token.token")).thenReturn(userDetails)

        jwtAuthorizationFilter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response))
    }

    @Test
    fun `Not Valid Token`() {
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val filterChain = mock(FilterChain::class.java)

        `when`(request.getHeader("Authorization")).thenReturn("Bearer token.token.token.token")
        `when`(jwtUtils.validateJwtToken("token.token.token.token")).thenReturn(false)

        jwtAuthorizationFilter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response))
        verify(jwtUtils, never()).toUserDetails(anyString())
    }

    @Test
    fun `No Authorization Test`() {
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val filterChain = mock(FilterChain::class.java)

        `when`(request.getHeader("Authorization")).thenReturn("")

        jwtAuthorizationFilter.doFilter(request, response, filterChain)

        verify(jwtUtils, never()).validateJwtToken(anyString())
        verify(jwtUtils, never()).toUserDetails(anyString())
        verify(filterChain).doFilter(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response))
    }

    @Test
    fun `Header without Bearer Test`() {
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val filterChain = mock(FilterChain::class.java)

        `when`(request.getHeader("Authorization")).thenReturn("Not-Bearer token.token.token.token")

        jwtAuthorizationFilter.doFilter(request, response, filterChain)

        verify(jwtUtils, never()).validateJwtToken(anyString())
        verify(jwtUtils, never()).toUserDetails(anyString())
        verify(filterChain).doFilter(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response))
    }

    @Test
    fun `Null Header Test`() {
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val filterChain = mock(FilterChain::class.java)

        `when`(request.getHeader("Authorization")).thenReturn(null)

        jwtAuthorizationFilter.doFilter(request, response, filterChain)

        verify(jwtUtils, never()).validateJwtToken(anyString())
        verify(jwtUtils, never()).toUserDetails(anyString())
        verify(filterChain).doFilter(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response))
    }
}