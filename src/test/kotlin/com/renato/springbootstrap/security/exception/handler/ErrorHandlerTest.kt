package com.renato.springbootstrap.security.exception.handler

import com.renato.springbootstrap.api.response.GeneralFailureResponse
import com.renato.springbootstrap.exception.ExceptionHandler
import com.renato.springbootstrap.security.exception.JwtException
import com.renato.springbootstrap.security.exception.UserAlreadyExistsException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import java.util.stream.Stream

class ErrorHandlerTest {

    private lateinit var errorHandler: ExceptionHandler

    @BeforeEach
    fun setUp() {
        errorHandler = ExceptionHandler()
    }

    @ParameterizedTest(name = "{index} => exception={0}, expected={1}")
    @MethodSource("handledExceptions")
    fun `given_exception_when_handle_then_returns_expected_status_and_error`(
        exception: Exception,
        expectedStatus: HttpStatus,
        expectedCode: String,
    ) {
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/auth/login"
        }

        val response = when (exception) {
            is AccessDeniedException -> errorHandler.handle(exception, request)
            is UserAlreadyExistsException -> errorHandler.handle(exception, request)
            is BadCredentialsException -> errorHandler.handle(exception, request)
            is RuntimeException -> errorHandler.handle(exception, request)
            else -> errorHandler.handle(exception, request)
        }

        assertThat(response.statusCode).isEqualTo(expectedStatus)
        val body = response.body as GeneralFailureResponse
        assertThat(body.message).isEqualTo("myMessage")
        assertThat(body.status).isEqualTo(expectedStatus.value())
        assertThat(body.error).isEqualTo(expectedStatus.reasonPhrase)
        assertThat(body.code).isEqualTo(expectedCode)
        assertThat(body.path).isEqualTo("/api/auth/login")
        assertThat(body.details).isEmpty()
        assertThat(body.timestamp).isNotNull()
    }

    @Test
    fun `given_jwt_exception_when_handle_then_returns_unauthorized_with_invalid_token_code`() {
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/auth/refresh"
        }

        val response = errorHandler.handle(JwtException("token invalid"), request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        val body = response.body as GeneralFailureResponse
        assertThat(body.message).isEqualTo("token invalid")
        assertThat(body.status).isEqualTo(HttpStatus.UNAUTHORIZED.value())
        assertThat(body.error).isEqualTo(HttpStatus.UNAUTHORIZED.reasonPhrase)
        assertThat(body.code).isEqualTo("INVALID_TOKEN")
        assertThat(body.path).isEqualTo("/api/auth/refresh")
        assertThat(body.details).isEmpty()
    }

    @Test
    fun `given_illegal_argument_when_handle_then_returns_bad_request_with_bad_request_code`() {
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/users"
        }

        val response = errorHandler.handle(IllegalArgumentException("invalid payload"), request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        val body = response.body as GeneralFailureResponse
        assertThat(body.message).isEqualTo("invalid payload")
        assertThat(body.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(body.error).isEqualTo(HttpStatus.BAD_REQUEST.reasonPhrase)
        assertThat(body.code).isEqualTo("BAD_REQUEST")
        assertThat(body.path).isEqualTo("/api/users")
        assertThat(body.details).isEmpty()
    }

    @Test
    fun `given_blank_message_when_handle_then_response_uses_http_reason_phrase`() {
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/users"
        }

        val response = errorHandler.handle(RuntimeException("   "), request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        val body = response.body as GeneralFailureResponse
        assertThat(body.message).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase)
        assertThat(body.code).isEqualTo("RUNTIME_ERROR")
    }

    @Test
    fun `given_null_message_when_handle_then_response_uses_http_reason_phrase`() {
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/users"
        }

        val response = errorHandler.handle(Exception(), request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        val body = response.body as GeneralFailureResponse
        assertThat(body.message).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase)
        assertThat(body.code).isEqualTo("INTERNAL_ERROR")
    }

    companion object {
        @JvmStatic
        fun handledExceptions(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(AccessDeniedException("myMessage"), HttpStatus.FORBIDDEN, "ACCESS_DENIED"),
                Arguments.of(UserAlreadyExistsException("myMessage"), HttpStatus.CONFLICT, "USER_ALREADY_EXISTS"),
                Arguments.of(BadCredentialsException("myMessage"), HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS"),
                Arguments.of(Exception("myMessage"), HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR"),
                Arguments.of(RuntimeException("myMessage"), HttpStatus.INTERNAL_SERVER_ERROR, "RUNTIME_ERROR"),
            )
        }
    }
}
