package com.renato.springbootstrap.security.controller

import com.renato.springbootstrap.security.api.request.UpdateUserRequestDTO
import com.renato.springbootstrap.security.api.response.UserResponseDTO
import com.renato.springbootstrap.security.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/security/admin"])
@Secured("ROLE_ADMIN")
class SecurityAdminController(private val userService: UserService) {
    @PostMapping("/user/{username}/role/{role}")
    fun addRole(@PathVariable(value = "username") username : String, @PathVariable(value = "role") role : String) : ResponseEntity<Any> {
        userService.addRole(username, role)
        return ResponseEntity(CREATED)
    }

    @DeleteMapping("/user/{username}/role/{role}")
    fun removeRole(@PathVariable(value = "username") username : String, @PathVariable(value = "role") role : String) : ResponseEntity<Any> {
        userService.removeRole(username, role)
        return ResponseEntity(OK)
    }

    @GetMapping("/user")
    fun getUsers(pageable: Pageable): Page<UserResponseDTO> {
        val page = userService.getUsers(pageable)
        return PageImpl(page.get().map { r -> UserResponseDTO(r) }.toList(), page.pageable, page.totalElements)
    }

    @GetMapping("/user/username/{username}")
    fun getUserByUsername(@PathVariable(value = "username") username : String): UserResponseDTO {
        return UserResponseDTO(userService.getUserByUsername(username = username))
    }

    @GetMapping("/user/user_id/{user_id}")
    fun getUserByUserId(@PathVariable(value = "user_id") userId : Long): UserResponseDTO {
        return UserResponseDTO(userService.getUserByUserId(userId = userId))
    }

    @PatchMapping(value = ["/update"])
    fun update(@RequestBody updateUserRequestDTO: UpdateUserRequestDTO) : String {
        val user = userService.updateUser(email = updateUserRequestDTO.email, password = updateUserRequestDTO.password)
        return userService.authenticate(username = user.username, password = updateUserRequestDTO.password)
    }
}