package com.renato.springbootstrap.security.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class JwtExceptionTest {

    @Test
    fun jwtExceptionSanity() {
        val jwtException = JwtException("myMessage")
        assertThat(jwtException).isNotNull
        assertThat(jwtException.message).isEqualTo("myMessage")
        assertThrows<JwtException> { throw jwtException }
    }
}