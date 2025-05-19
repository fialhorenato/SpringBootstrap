package com.renato.springbootstrap.security.controller

import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.security.api.request.LoginRequestDTO
import com.renato.springbootstrap.security.api.request.SignupRequestDTO
import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SecurityControllerTest {
    companion object {
        @Mock
        lateinit var securityService: UserService

        @BeforeEach
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
        `when`(securityService.me()).thenReturn(UserSecurity(0L, UUID.randomUUID(),"username", "password", "email", emptyList(), emptyList()))
        securityController.me()
        verify(securityService).me()
    }


}