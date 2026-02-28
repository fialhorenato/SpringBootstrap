package com.renato.springbootstrap.security.controller

import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.security.api.request.LoginRequestDTO
import com.renato.springbootstrap.security.api.request.SignupRequestDTO
import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SecurityControllerTest {

    @Mock
    lateinit var userService: UserService

    @InjectMocks
    lateinit var securityController: SecurityController

    @Test
    fun `given_signup_request_when_signup_is_called_then_service_is_delegated_and_user_is_returned`() {
        val request = SignupRequestDTO("username", "email", "password")
        val user = UserFactory.createUser(username = "username", email = "email")

        `when`(userService.createUser("username", "password", "email")).thenReturn(user)

        val response = securityController.signup(request)

        assertThat(response.username).isEqualTo("username")
        assertThat(response.email).isEqualTo("email")
        verify(userService).createUser("username", "password", "email")
        verifyNoMoreInteractions(userService)
    }

    @Test
    fun `given_login_request_when_login_is_called_then_service_is_delegated_and_token_is_returned`() {
        val request = LoginRequestDTO("username", "password")
        `when`(userService.authenticate("username", "password")).thenReturn("jwt-token")

        val token = securityController.login(request)

        assertThat(token).isEqualTo("jwt-token")
        verify(userService).authenticate("username", "password")
        verifyNoMoreInteractions(userService)
    }

    @Test
    fun `given_authenticated_user_when_me_is_called_then_service_is_delegated_and_user_is_returned`() {
        val me = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "password",
            email = "email",
            authorities = emptyList(),
            roles = listOf("USER"),
        )
        `when`(userService.me()).thenReturn(me)

        val response = securityController.me()

        assertThat(response.username).isEqualTo("username")
        assertThat(response.roles).containsExactly("USER")
        verify(userService).me()
        verifyNoMoreInteractions(userService)
    }
}
