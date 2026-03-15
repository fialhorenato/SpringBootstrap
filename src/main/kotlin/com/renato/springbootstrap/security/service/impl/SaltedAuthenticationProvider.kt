package com.renato.springbootstrap.security.service.impl

import com.renato.springbootstrap.security.domain.UserSecurity
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder

class SaltedAuthenticationProvider(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val rawPassword = authentication.credentials.toString()

        val userDetails = userDetailsService.loadUserByUsername(username)

        if (userDetails !is UserSecurity) {
            throw BadCredentialsException("Invalid user details")
        }

        val saltedPassword = userDetails.salt + rawPassword

        if (!passwordEncoder.matches(saltedPassword, userDetails.password)) {
            throw BadCredentialsException("Invalid credentials")
        }

        return UsernamePasswordAuthenticationToken(
            userDetails,
            rawPassword,
            userDetails.authorities
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}