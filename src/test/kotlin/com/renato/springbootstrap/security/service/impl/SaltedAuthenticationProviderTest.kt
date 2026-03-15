package com.renato.springbootstrap.security.service.impl

import com.renato.springbootstrap.security.domain.UserSecurity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SaltedAuthenticationProviderTest {

    @InjectMocks
    lateinit var provider: SaltedAuthenticationProvider

    @Mock
    lateinit var userDetailsService: UserDetailsService

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `given_valid_credentials_when_authenticate_then_authenticated_token_is_returned`() {
        val authentication = UsernamePasswordAuthenticationToken("username", "plain-password")
        val authority = SimpleGrantedAuthority("ROLE_USER")
        val userDetails = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "encoded-password",
            email = "user@example.com",
            salt = "salt-",
            authorities = listOf(authority),
            roles = listOf("USER"),
        )
        `when`(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails)
        `when`(passwordEncoder.matches("salt-plain-password", "encoded-password")).thenReturn(true)

        val result = provider.authenticate(authentication) as UsernamePasswordAuthenticationToken

        assertThat(result.principal).isEqualTo(userDetails)
        assertThat(result.credentials).isEqualTo("plain-password")
        assertThat(result.authorities).containsExactly(authority)
        verify(userDetailsService).loadUserByUsername("username")
        verify(passwordEncoder).matches("salt-plain-password", "encoded-password")
        verifyNoMoreInteractions(userDetailsService, passwordEncoder)
    }

    @Test
    fun `given_non_user_security_details_when_authenticate_then_bad_credentials_is_thrown`() {
        val authentication = UsernamePasswordAuthenticationToken("username", "plain-password")
        val userDetails = User.withUsername("username")
            .password("encoded-password")
            .authorities("ROLE_USER")
            .build()
        `when`(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails)

        val exception = assertThrows<BadCredentialsException> {
            provider.authenticate(authentication)
        }

        assertThat(exception.message).isEqualTo("Invalid user details")
        verify(userDetailsService).loadUserByUsername("username")
        verifyNoMoreInteractions(userDetailsService, passwordEncoder)
    }

    @Test
    fun `given_invalid_password_when_authenticate_then_bad_credentials_is_thrown`() {
        val authentication = UsernamePasswordAuthenticationToken("username", "plain-password")
        val userDetails = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "encoded-password",
            email = "user@example.com",
            salt = "salt-",
            authorities = listOf(SimpleGrantedAuthority("ROLE_USER")),
            roles = listOf("USER"),
        )
        `when`(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails)
        `when`(passwordEncoder.matches("salt-plain-password", "encoded-password")).thenReturn(false)

        val exception = assertThrows<BadCredentialsException> {
            provider.authenticate(authentication)
        }

        assertThat(exception.message).isEqualTo("Invalid credentials")
        verify(userDetailsService).loadUserByUsername("username")
        verify(passwordEncoder).matches("salt-plain-password", "encoded-password")
        verifyNoMoreInteractions(userDetailsService, passwordEncoder)
    }

    @Test
    fun `given_authentication_type_when_supports_then_only_username_password_tokens_are_supported`() {
        assertThat(provider.supports(UsernamePasswordAuthenticationToken::class.java)).isTrue()
        assertThat(provider.supports(String::class.java)).isFalse()
    }
}
