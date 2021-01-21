package com.renato.springbootstrap.helloworld.controller

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Secured("ROLE_ADMIN")
class HelloWorldController {
    @GetMapping
    fun helloWorld(): String {
        return "Hello World"
    }
}