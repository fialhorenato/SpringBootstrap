package com.renato.springbootstrap.security.service.impl

import com.renato.springbootstrap.factory.RoleFactory
import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.security.repository.UserRepository
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
import org.springframework.security.core.userdetails.UsernameNotFoundException

@ExtendWith(MockitoExtension::class)
class UserDetailsServiceImplTest {

    @InjectMocks
    lateinit var service: UserDetailsServiceImpl

    @Mock
    lateinit var userRepository: UserRepository

    @Test
    fun `given_existing_username_when_loadUserByUsername_then_user_details_are_returned`() {
        val username = "test"
        val baseUser = UserFactory.createUser(username = username)
        val userWithRoles = baseUser.copy(roles = listOf(RoleFactory.createRole(baseUser, "ADMIN")))
        `when`(userRepository.findByUsername(username)).thenReturn(userWithRoles)

        val user = service.loadUserByUsername(username)

        assertThat(user.username).isEqualTo(username)
        assertThat(user.authorities.map { it.authority }).containsExactly("ADMIN")
        verify(userRepository).findByUsername(username)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `given_missing_username_when_loadUserByUsername_then_username_not_found_is_thrown`() {
        val username = "missing"
        `when`(userRepository.findByUsername(username)).thenReturn(null)

        assertThrows<UsernameNotFoundException> {
            service.loadUserByUsername(username)
        }

        verify(userRepository).findByUsername(username)
        verifyNoMoreInteractions(userRepository)
    }
}
