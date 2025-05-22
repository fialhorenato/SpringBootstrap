package com.renato.springbootstrap

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class SpringBootstrapApplicationTests {

    @Test
    fun contextLoads() {
        // This test will fail if the application context cannot be loaded
    }

    @Test
    fun mainMethodStartsApplication() {
        // This test verifies that the main method can be called without exceptions
        main(arrayOf())
    }
}