package com.renato.springbootstrap.helloworld.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HelloWorldControllerTest {
    private val controller = HelloWorldController()

    @Test
    fun `given_secure_endpoint_when_called_then_expected_message_is_returned`() {
        val response = controller.helloWorldSecured()

        assertThat(response).isEqualTo("Hello World with Security")
    }

    @Test
    fun `given_insecure_endpoint_when_called_then_expected_message_is_returned`() {
        val response = controller.helloWorldInsecured()

        assertThat(response).isEqualTo("Hello World Insecure")
    }
}
