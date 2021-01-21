package com.renato.springbootstrap.security.controller

import com.renato.springbootstrap.security.api.request.LoginRequestDTO
import com.renato.springbootstrap.security.api.request.SignupRequestDTO
import com.renato.springbootstrap.security.api.response.UserResponseDTO
import com.renato.springbootstrap.security.service.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/security"])
class SecurityController(val securityService: SecurityService) {
    @PostMapping(value = ["/signup"])
    fun signup(@RequestBody signupRequestDTO: SignupRequestDTO): ResponseEntity<UserResponseDTO> {
        val user = securityService.createUser(signupRequestDTO)
        val userResponseDTO = UserResponseDTO(user)
        return ResponseEntity(userResponseDTO, HttpStatus.CREATED)
    }

    @PostMapping(value = ["/login"])
    fun login(@RequestBody loginRequestDTO: LoginRequestDTO): String {
        return securityService.authenticate(loginRequestDTO)
    }

    @GetMapping(value = ["/me"])
    fun me(): UserResponseDTO {
        return UserResponseDTO(securityService.me())
    }

}