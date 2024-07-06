package com.renato.springbootstrap.exception

import com.renato.springbootstrap.api.response.GeneralFailureResponse
import com.renato.springbootstrap.security.exception.UserAlreadyExistsException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(ExceptionHandler::class.java)

    @ExceptionHandler(AccessDeniedException::class)
    fun handle(ex: AccessDeniedException): ResponseEntity<Any> {
        logger.error(ex.localizedMessage,ex)
        return ResponseEntity(generateErrorResponse(ex.localizedMessage), HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handle(ex: UserAlreadyExistsException): ResponseEntity<Any> {
        logger.error(ex.localizedMessage,ex)
        return ResponseEntity(generateErrorResponse(ex.localizedMessage),HttpStatus.CONFLICT)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handle(ex: BadCredentialsException): ResponseEntity<Any> {
        logger.error(ex.localizedMessage,ex)
        return ResponseEntity(generateErrorResponse(ex.localizedMessage),HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception): ResponseEntity<Any> {
        logger.error(ex.localizedMessage,ex)
        return ResponseEntity(generateErrorResponse(ex.localizedMessage), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handle(ex: RuntimeException): ResponseEntity<Any> {
        logger.error(ex.localizedMessage,ex)
        return ResponseEntity(generateErrorResponse(ex.localizedMessage), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun generateErrorResponse(message : String) : GeneralFailureResponse {
        return GeneralFailureResponse(listOf(message))
    }
}