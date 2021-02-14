package com.renato.springbootstrap.security.controller

import com.renato.springbootstrap.security.api.request.LoginRequestDTO
import com.renato.springbootstrap.security.api.request.SignupRequestDTO
import com.renato.springbootstrap.security.api.request.UpdateUserRequestDTO
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
        val user = securityService.createUser(signupRequestDTO.username, signupRequestDTO.password, signupRequestDTO.email)
        val userResponseDTO = UserResponseDTO(user)
        return ResponseEntity(userResponseDTO, HttpStatus.CREATED)
    }

    @PatchMapping(value = ["/update"])
    fun update(@RequestBody updateUserRequestDTO: UpdateUserRequestDTO) : String {
        val user = securityService.updateUser(email = updateUserRequestDTO.email, password = updateUserRequestDTO.password)
        return securityService.authenticate(username = user.username, password = updateUserRequestDTO.password)
    }

    @PostMapping(value = ["/login"])
    fun login(@RequestBody loginRequestDTO: LoginRequestDTO): String {
        return securityService.authenticate(loginRequestDTO.username, loginRequestDTO.password)
    }

    @GetMapping(value = ["/me"])
    fun me(): UserResponseDTO {
        return UserResponseDTO(securityService.me())
    }

}