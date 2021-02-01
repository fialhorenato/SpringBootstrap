package com.renato.springbootstrap.helloworld.controller

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("hello-world")
class HelloWorldController {
    @GetMapping
    @RequestMapping("/secured")
    @Secured("ROLE_ADMIN")
    fun helloWorldSecured(): String {
        return "Hello World Secured"
    }

    @GetMapping
    @RequestMapping("/insecured")
    fun helloWorldInsecured(): String {
        return "Hello World Insecured"
    }
}