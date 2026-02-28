package com.renato.springbootstrap.security.utils

import com.renato.springbootstrap.security.domain.UserSecurity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.UUID

class JwtUtilsTest {

    private lateinit var jwtUtils: JwtUtils

    @BeforeEach
    fun setUp() {
        jwtUtils = JwtUtils().apply {
            jwtSecret = "0123456789abcdef0123456789abcdef"
            jwtExpirationMs = 86_400_000
        }
    }

    @Test
    fun `given_authenticated_principal_when_generate_token_then_expected_claims_are_present`() {
        val principal = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "password",
            email = "email@example.com",
            authorities = listOf(SimpleGrantedAuthority("USER")),
            roles = listOf("USER"),
        )
        val authentication = UsernamePasswordAuthenticationToken(principal, "password", principal.authorities)

        val token = jwtUtils.generateJwtToken(authentication)

        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("username")
        assertThat(jwtUtils.getEmailFromJwtToken(token)).isEqualTo("email@example.com")
        assertThat(jwtUtils.getPasswordFromJwtToken(token)).isEqualTo("password")
        assertThat(jwtUtils.getRolesFromJwtToken(token)).containsExactly("USER")
        assertThat(jwtUtils.getAuthoritiesFromJwtToken(token).map { it.authority }).containsExactly("ROLE_USER")
    }

    @Test
    fun `given_valid_token_when_to_user_details_is_called_then_domain_user_is_mapped`() {
        val principal = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "password",
            email = "email@example.com",
            authorities = listOf(SimpleGrantedAuthority("USER")),
            roles = listOf("USER"),
        )
        val authentication = UsernamePasswordAuthenticationToken(principal, "password", principal.authorities)
        val token = jwtUtils.generateJwtToken(authentication)

        val userDetails = jwtUtils.toUserDetails(token)

        assertThat(userDetails.username).isEqualTo("username")
        assertThat(userDetails.email).isEqualTo("email@example.com")
        assertThat(userDetails.roles).containsExactly("USER")
    }

    @Test
    fun `given_valid_and_invalid_tokens_when_validate_is_called_then_expected_flags_are_returned`() {
        val principal = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "password",
            email = "email@example.com",
            authorities = listOf(SimpleGrantedAuthority("USER")),
            roles = listOf("USER"),
        )
        val authentication = UsernamePasswordAuthenticationToken(principal, "password", principal.authorities)
        val token = jwtUtils.generateJwtToken(authentication)

        assertThat(jwtUtils.validateJwtToken(token)).isTrue
        assertThat(jwtUtils.validateJwtToken("not-a-token")).isFalse
    }

    @Test
    fun `given_missing_secret_when_generate_token_then_uninitialized_property_exception_is_thrown`() {
        val jwtWithoutSecret = JwtUtils().apply {
            jwtExpirationMs = 86_400_000
        }
        val principal = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "password",
            email = "email@example.com",
            authorities = emptyList(),
            roles = emptyList(),
        )
        val authentication = UsernamePasswordAuthenticationToken(principal, "password", emptyList())

        assertThrows<UninitializedPropertyAccessException> {
            jwtWithoutSecret.generateJwtToken(authentication)
        }
    }

    @Test
    fun `given_invalid_secret_when_validate_is_called_then_false_is_returned`() {
        jwtUtils.jwtSecret = ""

        assertThat(jwtUtils.validateJwtToken("invalid")).isFalse
    }
}
