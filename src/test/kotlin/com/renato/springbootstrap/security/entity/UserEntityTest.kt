package com.renato.springbootstrap.security.entity

import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.factory.UserFactory.Companion.generateUser
import com.renato.springbootstrap.security.repository.UserRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class UserEntityTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun saveSanity() {
        val user = generateUser()
        val userSaved = userRepository.save(user)
        assertThat(userSaved).isNotNull
    }

    @Test
    fun findByUsernameSanity() {
        val user = generateUser()
        userRepository.save(user)
        val userFound = userRepository.findByUsername("username")
        val userNotFound = userRepository.findByUsername("notExistingUser")
        assertThat(userFound).isNotNull
        assertThat(userNotFound).isNull()
    }

    @Test
    fun existsByUsernameOrEmailSanity() {
        val user = generateUser()
        userRepository.save(user)
        val userFound = userRepository.existsByUsernameOrEmail("username", "email")
        val userNotFound = userRepository.existsByUsernameOrEmail("notExistingUser", "emailNotExisting")
        assertThat(userFound).isTrue
        assertThat(userNotFound).isFalse
    }
}