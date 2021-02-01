package com.renato.springbootstrap.security

import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.security.api.request.LoginRequestDTO
import com.renato.springbootstrap.security.api.request.SignupRequestDTO
import com.renato.springbootstrap.security.controller.SecurityController
import com.renato.springbootstrap.security.service.SecurityService
import com.renato.springbootstrap.security.service.UserDetails
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SecurityControllerTest {
    companion object {
        @Mock
        lateinit var securityService: SecurityService

        @BeforeAll
        fun setup() {
            verifyNoInteractions(securityService)
        }
    }
    @InjectMocks
    lateinit var securityController: SecurityController

    @Test
    fun signupMustDelegateToService() {
        val signupRequestDTO = SignupRequestDTO("username", "email", "password")
        `when`(securityService.createUser("username", "password", "email")).thenReturn(UserFactory.generateUser())
        securityController.signup(signupRequestDTO)
        verify(securityService).createUser("username", "password", "email")
    }

    @Test
    fun loginMustDelegateToService() {
        val loginRequestDTO = LoginRequestDTO("username", "password")
        securityController.login(loginRequestDTO)
        verify(securityService).authenticate("username", "password")
    }

    @Test
    fun meMustDelegateToService() {
        `when`(securityService.me()).thenReturn(UserDetails("email", "username", "password", emptyList(), emptyList()))
        securityController.me()
        verify(securityService).me()
    }


}