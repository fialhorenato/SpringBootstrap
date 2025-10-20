package com.renato.springbootstrap.helloworld.controller

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("hello-world")
class HelloWorldController {

    @GetMapping("/secure")
    @Secured("ROLE_ADMIN")
    fun helloWorldSecured(): String = "Hello World with Security"

    @GetMapping("/insecure")
    fun helloWorldInsecured(): String = "Hello World Insecure"
}