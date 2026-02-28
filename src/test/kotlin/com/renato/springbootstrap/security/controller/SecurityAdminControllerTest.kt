package com.renato.springbootstrap.security.controller

import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.security.api.request.UpdateUserRequestDTO
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class SecurityAdminControllerTest {

    @Mock
    lateinit var userService: UserService

    @InjectMocks
    lateinit var controller: SecurityAdminController

    @Test
    fun `given_role_assignment_request_when_add_role_is_called_then_service_is_delegated_and_created_is_returned`() {
        val response = controller.addRole("username", "ADMIN")

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        verify(userService).addRole("username", "ADMIN")
        verifyNoMoreInteractions(userService)
    }

    @Test
    fun `given_role_removal_request_when_remove_role_is_called_then_service_is_delegated_and_ok_is_returned`() {
        val response = controller.removeRole("username", "ADMIN")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        verify(userService).removeRole("username", "ADMIN")
        verifyNoMoreInteractions(userService)
    }

    @Test
    fun `given_get_users_request_when_get_users_is_called_then_service_is_delegated_and_users_are_mapped`() {
        val user = UserFactory.createUser()
        val page = PageImpl(listOf(user), Pageable.unpaged(), 1)
        `when`(userService.getUsers(Pageable.unpaged())).thenReturn(page)

        val response = controller.getUsers(Pageable.unpaged())

        assertThat(response.content).hasSize(1)
        assertThat(response.content.first().username).isEqualTo(user.username)
        verify(userService).getUsers(Pageable.unpaged())
        verifyNoMoreInteractions(userService)
    }

    @Test
    fun `given_username_when_get_user_by_username_is_called_then_service_is_delegated`() {
        val user = UserFactory.createUser(username = "john")
        `when`(userService.getUserByUsername("john")).thenReturn(user)

        val response = controller.getUserByUsername("john")

        assertThat(response.username).isEqualTo("john")
        verify(userService).getUserByUsername("john")
        verifyNoMoreInteractions(userService)
    }

    @Test
    fun `given_user_id_when_get_user_by_user_id_is_called_then_service_is_delegated`() {
        val user = UserFactory.createUser()
        `when`(userService.getUserByUserId(1L)).thenReturn(user)

        val response = controller.getUserByUserId(1L)

        assertThat(response.userId).isEqualTo(user.userId)
        verify(userService).getUserByUserId(1L)
        verifyNoMoreInteractions(userService)
    }

    @Test
    fun `given_update_request_when_update_is_called_then_service_is_delegated_and_new_token_is_returned`() {
        val request = UpdateUserRequestDTO("new@email.com", "new-password")
        val updatedUser = UserFactory.createUser(username = "username", email = request.email)
        `when`(userService.updateUser(request.email, request.password)).thenReturn(updatedUser)
        `when`(userService.authenticate(updatedUser.username, request.password)).thenReturn("new-token")

        val token = controller.update(request)

        assertThat(token).isEqualTo("new-token")
        verify(userService).updateUser(request.email, request.password)
        verify(userService).authenticate(updatedUser.username, request.password)
        verifyNoMoreInteractions(userService)
    }
}
