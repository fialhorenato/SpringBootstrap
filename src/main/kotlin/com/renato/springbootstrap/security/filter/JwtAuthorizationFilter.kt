package com.renato.springbootstrap.security.filter

import com.renato.springbootstrap.security.utils.JwtUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthorizationFilter(private val jwtUtils: JwtUtils) : OncePerRequestFilter() {
    companion object {
        const val HEADER = "Authorization"
        const val TOKEN_PREFIX = "Bearer"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader(HEADER)
        if (header.isNullOrEmpty() || !header.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }
        
        val token = getToken(header)

        if(jwtUtils.validateJwtToken(token)) {
            val userDetails = jwtUtils.toUserDetails(token)
            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun getToken(header: String): String {
        return header.substring(7, header.length)
    }
}