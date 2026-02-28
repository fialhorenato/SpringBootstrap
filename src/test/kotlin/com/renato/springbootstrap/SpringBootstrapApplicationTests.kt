package com.renato.springbootstrap

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringBootstrapApplicationTests {

    @Test
    fun `given_spring_context_when_bootstrapped_then_it_loads`() {
        // Context load is verified by @SpringBootTest.
    }

    @Test
    fun `given_main_method_when_invoked_then_no_exception_is_thrown`() {
        assertDoesNotThrow {
            main(emptyArray())
        }
    }
}
