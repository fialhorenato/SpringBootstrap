package com.renato.springbootstrap.security.utils

import com.renato.springbootstrap.security.service.UserDetails
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.text.ParseException

@ExtendWith(MockitoExtension::class)
class JwtUtilsTest {
    
    @InjectMocks
    lateinit var jwtUtils: JwtUtils
    
    companion object{
        const val TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0ZSIsInBhc3N3b3JkIjoiJDJhJDEwJDlxR1pNVDZaODlUcUs4bDUzRjh6d2VjVEthZEtUNWt2b2ttY1ozMzc5UEd1SzYucEU5VURxIiwicm9sZXMiOlsiVVNFUiJdLCJleHAiOjE2MTM5Mjk5NzQsImlhdCI6MTYxMzg0MzU3NCwiZW1haWwiOiJ0ZXN0ZSJ9.zSoDviicxcuzTQAkodsnvwonp9Xj5jrvGFyHuT6mjD4"
    }
    
    @Test
    fun getClaimsFromTokenSanity() {
        assertThat(jwtUtils.getAuthoritiesFromJwtToken(TOKEN)).hasSize(1)
        assertThat(jwtUtils.getUserNameFromJwtToken(TOKEN)).isEqualTo("teste")
        assertThat(jwtUtils.getEmailFromJwtToken(TOKEN)).isEqualTo("teste")
        assertThat(jwtUtils.getPasswordFromJwtToken(TOKEN)).isNotEqualTo("teste")
        assertThat(jwtUtils.getRolesFromJwtToken(TOKEN)).hasSize(1)
    }

    @Test
    fun generateJwtTokenSanity() {
        jwtUtils.jwtSecret = "95605770-21fe-43da-9986-8506693c1327"
        jwtUtils.jwtExpirationMs = 86400000
        val principal = UserDetails("email", "username", "password", emptyList(), emptyList())
        val authentication = UsernamePasswordAuthenticationToken(principal, "password", emptyList())
        assertDoesNotThrow { jwtUtils.generateJwtToken(authentication) }
    }

    @Test
    fun toUserDetailsSanity() {
        var userDetails = jwtUtils.toUserDetails(TOKEN)
        assertThat(userDetails.myUsername).isEqualTo("teste")
        assertThat(userDetails.email).isEqualTo("teste")
        assertThat(userDetails.roles).isNotEmpty
    }

    @Test
    fun validateJwtTokenSanity() {
        jwtUtils.jwtSecret = "95605770-21fe-43da-9986-8506693c1327"
        jwtUtils.jwtExpirationMs = 86400000
        val principal = UserDetails("email", "username", "password", emptyList(), emptyList())
        val authentication = UsernamePasswordAuthenticationToken(principal, "password", emptyList())
        val token = jwtUtils.generateJwtToken(authentication)

        // Assertions
        assertDoesNotThrow { jwtUtils.validateJwtToken(token) }
        assertThrows<ParseException> { jwtUtils.validateJwtToken("notAToken") }
        assertThat(jwtUtils.validateJwtToken(TOKEN)).isFalse
    }

}