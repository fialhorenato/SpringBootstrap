package com.renato.springbootstrap.security.exception.handler

import com.renato.springbootstrap.api.response.GeneralFailureResponse
import com.renato.springbootstrap.exception.ExceptionHandler
import com.renato.springbootstrap.security.exception.UserAlreadyExistsException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
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
    ) {
        val response = when (exception) {
            is AccessDeniedException -> errorHandler.handle(exception)
            is UserAlreadyExistsException -> errorHandler.handle(exception)
            is BadCredentialsException -> errorHandler.handle(exception)
            is RuntimeException -> errorHandler.handle(exception)
            else -> errorHandler.handle(exception)
        }

        assertThat(response.statusCode).isEqualTo(expectedStatus)
        assertThat((response.body as GeneralFailureResponse).errors).containsExactly("myMessage")
    }

    companion object {
        @JvmStatic
        fun handledExceptions(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(AccessDeniedException("myMessage"), HttpStatus.FORBIDDEN),
                Arguments.of(UserAlreadyExistsException("myMessage"), HttpStatus.CONFLICT),
                Arguments.of(BadCredentialsException("myMessage"), HttpStatus.NOT_FOUND),
                Arguments.of(Exception("myMessage"), HttpStatus.INTERNAL_SERVER_ERROR),
                Arguments.of(RuntimeException("myMessage"), HttpStatus.INTERNAL_SERVER_ERROR),
            )
        }
    }
}
