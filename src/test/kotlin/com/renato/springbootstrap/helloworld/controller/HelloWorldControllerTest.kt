package com.renato.springbootstrap.helloworld.controller

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class HelloWorldControllerTest {
    @InjectMocks lateinit var helloWorldController: HelloWorldController

    @Test
    fun `Hello World secure Sanity` () {
        val result = helloWorldController.helloWorldSecured()

        Assertions.assertThat(result).isEqualTo("Hello World with Security")
    }

    @Test
    fun `Hello World insecure Sanity` () {
        val result = helloWorldController.helloWorldInsecured()

        Assertions.assertThat(result).isEqualTo("Hello World Insecure")
    }
}