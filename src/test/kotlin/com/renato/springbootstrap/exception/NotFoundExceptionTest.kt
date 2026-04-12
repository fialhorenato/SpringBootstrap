package com.renato.springbootstrap.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NotFoundExceptionTest {

    @Test
    fun `given_not_found_exception_when_created_then_message_is_preserved_and_throwable`() {
        val exception = NotFoundException("not found")

        assertThat(exception.message).isEqualTo("not found")
        assertThrows<NotFoundException> {
            throw exception
        }
    }

    @Test
    fun `given_not_found_exception_with_cause_when_created_then_cause_is_preserved`() {
        val cause = IllegalStateException("root cause")

        val exception = NotFoundException("not found", cause)

        assertThat(exception.cause).isSameAs(cause)
    }
}
