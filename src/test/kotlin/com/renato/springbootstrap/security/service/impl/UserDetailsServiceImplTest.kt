package com.renato.springbootstrap.security.service.impl

import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.security.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.userdetails.UsernameNotFoundException

@ExtendWith(MockitoExtension::class)
class UserDetailsServiceImplTest {

    @InjectMocks lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    @Mock lateinit var userRepository: UserRepository

    @Test
    fun loadUserByUsername() {
        val username = "test"
        Mockito.`when`(userRepository.findByUsername(username)).thenReturn(UserFactory.generateUser(username))
        val user = userDetailsServiceImpl.loadUserByUsername(username)
        assertThat(user.username).isEqualTo(username)
    }

    @Test
    fun loadUserByUsernameThrowsException() {
        val username = "test"
        Mockito.`when`(userRepository.findByUsername(username)).thenReturn(null)
        assertThrows<UsernameNotFoundException> {  userDetailsServiceImpl.loadUserByUsername(username) }
    }
}