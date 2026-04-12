package com.renato.springbootstrap.security.filter

import com.renato.springbootstrap.security.utils.JwtUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthorizationFilter(private val jwtUtils: JwtUtils) : OncePerRequestFilter() {
    companion object {
        const val TOKEN_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {
        val token = resolveToken(request.getHeader(HttpHeaders.AUTHORIZATION))
        if (token == null) {
            filterChain.doFilter(request, response);
            return
        }

        if (SecurityContextHolder.getContext().authentication == null && jwtUtils.validateJwtToken(token)) {
            val userDetails = jwtUtils.toUserDetails(token)
            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(header: String?): String? {
        if (header.isNullOrBlank() || !header.startsWith(TOKEN_PREFIX, ignoreCase = true)) {
            return null
        }
        return header.substring(TOKEN_PREFIX.length).trim().takeIf { it.isNotEmpty() }
    }
}
