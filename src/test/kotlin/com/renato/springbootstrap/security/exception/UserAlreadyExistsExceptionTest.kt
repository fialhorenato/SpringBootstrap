package com.renato.springbootstrap.security.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class UserAlreadyExistsExceptionTest {

    @Test
    fun userAlreadyExistsExceptionSanity() {
        val userAlreadyExistsException = UserAlreadyExistsException("myMessage")
        assertThat(userAlreadyExistsException).isNotNull
        assertThat(userAlreadyExistsException.message).isEqualTo("myMessage")
        assertThrows<UserAlreadyExistsException> { throw userAlreadyExistsException }
    }
}