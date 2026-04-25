package com.renato.springbootstrap.api.response

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class GeneralFailureResponseTest {

    @Test
    fun `given_response_values_when_constructed_then_all_fields_are_mapped`() {
        val timestamp = Instant.parse("2026-04-25T10:15:30Z")

        val response = GeneralFailureResponse(
            timestamp = timestamp,
            status = 401,
            error = "Unauthorized",
            code = "BAD_CREDENTIALS",
            message = "Invalid username or password",
            path = "/api/auth/login",
        )

        assertThat(response.timestamp).isEqualTo(timestamp)
        assertThat(response.status).isEqualTo(401)
        assertThat(response.error).isEqualTo("Unauthorized")
        assertThat(response.code).isEqualTo("BAD_CREDENTIALS")
        assertThat(response.message).isEqualTo("Invalid username or password")
        assertThat(response.path).isEqualTo("/api/auth/login")
    }

    @Test
    fun `given_same_field_values_when_compared_then_objects_are_equal`() {
        val timestamp = Instant.parse("2026-04-25T10:15:30Z")
        val expected = GeneralFailureResponse(
            timestamp = timestamp,
            status = 400,
            error = "Bad Request",
            code = "BAD_REQUEST",
            message = "Malformed payload",
            path = "/api/users",
        )
        val actual = expected.copy()

        assertThat(actual).isEqualTo(expected)
        assertThat(actual.hashCode()).isEqualTo(expected.hashCode())
    }
}
