package com.renato.springbootstrap.exception

import com.renato.springbootstrap.api.response.GeneralFailureResponse
import com.renato.springbootstrap.security.exception.JwtException
import com.renato.springbootstrap.security.exception.UserAlreadyExistsException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class ExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(ExceptionHandler::class.java)

    @ExceptionHandler(AccessDeniedException::class)
    fun handle(ex: AccessDeniedException, request: HttpServletRequest): ResponseEntity<GeneralFailureResponse> {
        logger.warn(ex.localizedMessage, ex)
        return buildErrorResponse(
            status = HttpStatus.FORBIDDEN,
            code = "ACCESS_DENIED",
            message = ex.localizedMessage,
            request = request,
        )
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handle(ex: UserAlreadyExistsException, request: HttpServletRequest): ResponseEntity<GeneralFailureResponse> {
        logger.warn(ex.localizedMessage, ex)
        return buildErrorResponse(
            status = HttpStatus.CONFLICT,
            code = "USER_ALREADY_EXISTS",
            message = ex.localizedMessage,
            request = request,
        )
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handle(ex: BadCredentialsException, request: HttpServletRequest): ResponseEntity<GeneralFailureResponse> {
        logger.warn(ex.localizedMessage, ex)
        return buildErrorResponse(
            status = HttpStatus.UNAUTHORIZED,
            code = "BAD_CREDENTIALS",
            message = ex.localizedMessage,
            request = request,
        )
    }

    @ExceptionHandler(JwtException::class)
    fun handle(ex: JwtException, request: HttpServletRequest): ResponseEntity<GeneralFailureResponse> {
        logger.warn(ex.localizedMessage, ex)
        return buildErrorResponse(
            status = HttpStatus.UNAUTHORIZED,
            code = "INVALID_TOKEN",
            message = ex.localizedMessage,
            request = request,
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handle(ex: IllegalArgumentException, request: HttpServletRequest): ResponseEntity<GeneralFailureResponse> {
        logger.warn(ex.localizedMessage, ex)
        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            code = "BAD_REQUEST",
            message = ex.localizedMessage,
            request = request,
        )
    }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception, request: HttpServletRequest): ResponseEntity<GeneralFailureResponse> {
        logger.error(ex.localizedMessage, ex)
        return buildErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            code = "INTERNAL_ERROR",
            message = ex.localizedMessage,
            request = request,
        )
    }

    @ExceptionHandler(RuntimeException::class)
    fun handle(ex: RuntimeException, request: HttpServletRequest): ResponseEntity<GeneralFailureResponse> {
        logger.error(ex.localizedMessage, ex)
        return buildErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            code = "RUNTIME_ERROR",
            message = ex.localizedMessage,
            request = request,
        )
    }

    private fun buildErrorResponse(
        status: HttpStatus,
        code: String,
        message: String?,
        request: HttpServletRequest,
        details: List<String> = emptyList(),
    ): ResponseEntity<GeneralFailureResponse> {
        val response = GeneralFailureResponse(
            timestamp = Instant.now(),
            status = status.value(),
            error = status.reasonPhrase,
            code = code,
            message = message?.ifBlank { status.reasonPhrase } ?: status.reasonPhrase,
            path = request.requestURI,
            details = details,
        )
        return ResponseEntity.status(status).body(response)
    }
}
