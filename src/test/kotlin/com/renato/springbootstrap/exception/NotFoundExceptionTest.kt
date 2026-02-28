package com.renato.springbootstrap.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NotFoundExceptionTest {

    @Test
    fun `given_not_found_exception_when_get_is_called_then_same_instance_is_returned`() {
        val exception = NotFoundException("not found")

        val supplied = exception.get()

        assertThat(exception.message).isEqualTo("not found")
        assertThat(supplied).isSameAs(exception)
    }

    @Test
    fun `given_not_found_exception_when_message_is_updated_then_new_message_is_exposed`() {
        val exception = NotFoundException("initial")

        exception.message = "updated"

        assertThat(exception.message).isEqualTo("updated")
    }
}
