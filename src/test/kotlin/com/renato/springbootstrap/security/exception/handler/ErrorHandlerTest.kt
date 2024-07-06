package com.renato.springbootstrap.security.exception.handler

import com.renato.springbootstrap.exception.ExceptionHandler
import com.renato.springbootstrap.security.exception.UserAlreadyExistsException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import java.lang.Exception

@ExtendWith(MockitoExtension::class)
class ErrorHandlerTest {
    @InjectMocks
    lateinit var errorHandler: ExceptionHandler

    @Test
    fun errorHandlerSanity() {
        assertDoesNotThrow { errorHandler.handle(Exception("myMessage")) }
            .also { assert(it.statusCode.is5xxServerError) }
        assertDoesNotThrow { errorHandler.handle(AccessDeniedException("myMessage")) }
            .also { assert(it.statusCode.is4xxClientError) }
        assertDoesNotThrow { errorHandler.handle(UserAlreadyExistsException("myMessage")) }
            .also { assert(it.statusCode.is4xxClientError) }
        assertDoesNotThrow { errorHandler.handle(BadCredentialsException("myMessage")) }
            . also { assert(it.statusCode.is4xxClientError) }
        assertDoesNotThrow { errorHandler.handle(RuntimeException("myMessage")) }
            .also { assert(it.statusCode.is5xxServerError) }
    }
}