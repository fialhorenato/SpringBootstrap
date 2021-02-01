package com.renato.springbootstrap.security.controller

import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.factory.UserFactory.Companion.generateUser
import com.renato.springbootstrap.security.api.request.LoginRequestDTO
import com.renato.springbootstrap.security.api.request.SignupRequestDTO
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Pageable.unpaged

@ExtendWith(MockitoExtension::class)
class SecurityAdminControllerTest {
    companion object {
        @Mock
        lateinit var securityService: SecurityService

        @BeforeAll
        fun setup() {
            verifyNoInteractions(securityService)
        }
    }
    @InjectMocks
    lateinit var securityAdminController: SecurityAdminController

    @Test
    fun addRoleMustDelegateToService() {
        securityAdminController.addRole("username", "ADMIN")
        verify(securityService).addRole("username", "ADMIN")
    }

    @Test
    fun getUserByUserIdMustDelegateToService() {
        `when`(securityService.getUserByUserId(1L)).thenReturn(generateUser())
        securityAdminController.getUserByUserId(1L)
        verify(securityService).getUserByUserId(1L)
    }

    @Test
    fun getUserByUsernameMustDelegateToService() {
        `when`(securityService.getUserByUsername("username")).thenReturn(generateUser())
        securityAdminController.getUserByUsername("username")
        verify(securityService).getUserByUsername("username")
    }

    @Test
    fun removeRoleMustDelegateToService() {
        securityAdminController.removeRole("username", "ADMIN")
        verify(securityService).removeRole("username", "ADMIN")
    }

    @Test
    fun getUsersMustDelegateToService() {
        `when`(securityService.getUsers(unpaged())).thenReturn(PageImpl(listOf(generateUser())))
        securityAdminController.getUsers(unpaged())
        verify(securityService).getUsers(unpaged())
    }
}