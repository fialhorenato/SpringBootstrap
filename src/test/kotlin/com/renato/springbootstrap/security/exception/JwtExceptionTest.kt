package com.renato.springbootstrap.security.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JwtExceptionTest {

    @Test
    fun `given_jwt_exception_when_created_then_message_is_preserved_and_throwable`() {
        val exception = JwtException("myMessage")

        assertThat(exception.message).isEqualTo("myMessage")
        assertThrows<JwtException> {
            throw exception
        }
    }
}
