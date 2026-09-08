package com.renato.springbootstrap.security.controller

import com.renato.springbootstrap.security.api.request.LoginRequestDTO
import com.renato.springbootstrap.security.api.request.RefreshTokenRequestDTO
import com.renato.springbootstrap.security.api.request.SignupRequestDTO
import com.renato.springbootstrap.security.api.response.AuthTokenResponseDTO
import com.renato.springbootstrap.security.api.response.UserResponseDTO
import com.renato.springbootstrap.security.service.AuthTokenService
import com.renato.springbootstrap.security.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/security"])
class SecurityController(
    private val userService: UserService,
    private val authTokenService: AuthTokenService,
) {
    @PostMapping(value = ["/signup"])
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@RequestBody signupRequestDTO: SignupRequestDTO): UserResponseDTO {
        val user = userService.createUser(
            signupRequestDTO.username,
            signupRequestDTO.password,
            signupRequestDTO.email
        )
        return UserResponseDTO(user)
    }

    @PostMapping(value = ["/login"])
    fun login(@RequestBody loginRequestDTO: LoginRequestDTO): AuthTokenResponseDTO {
        val token = userService.authenticate(loginRequestDTO.username, loginRequestDTO.password)
        return authTokenService.issueFromAccessToken(token)
    }

    @PostMapping(value = ["/refresh"])
    fun refresh(@RequestBody refreshTokenRequestDTO: RefreshTokenRequestDTO): AuthTokenResponseDTO {
        return authTokenService.refresh(refreshTokenRequestDTO.refreshToken)
    }

    @GetMapping(value = ["/me"])
    fun me(): UserResponseDTO {
        return UserResponseDTO(userService.me())
    }

}