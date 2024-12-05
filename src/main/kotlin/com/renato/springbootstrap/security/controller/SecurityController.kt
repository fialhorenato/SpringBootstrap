package com.renato.springbootstrap.security.controller

import com.renato.springbootstrap.security.api.request.LoginRequestDTO
import com.renato.springbootstrap.security.api.request.SignupRequestDTO
import com.renato.springbootstrap.security.api.response.UserResponseDTO
import com.renato.springbootstrap.security.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/security"])
class SecurityController(private val userService: UserService) {
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
    fun login(@RequestBody loginRequestDTO: LoginRequestDTO): String {
        return userService.authenticate(loginRequestDTO.username, loginRequestDTO.password)
    }

    @GetMapping(value = ["/me"])
    fun me(): UserResponseDTO {
        return UserResponseDTO(userService.me())
    }

}