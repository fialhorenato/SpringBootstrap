package com.renato.springbootstrap.security.exception

import java.lang.RuntimeException

class JwtException(override val message: String) : RuntimeException() {
}