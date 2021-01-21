package com.renato.springbootstrap.security.exception

class UserAlreadyExistsException(override val message: String? = "User already exists") : RuntimeException()