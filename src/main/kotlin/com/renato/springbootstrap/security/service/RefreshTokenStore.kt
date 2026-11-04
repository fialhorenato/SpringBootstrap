package com.renato.springbootstrap.security.service

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class RefreshTokenStore {
    private val refreshTokens = ConcurrentHashMap<String, String>()

    fun store(token: String, username: String) {
        refreshTokens[token] = username
    }

    fun consume(token: String): String? {
        return refreshTokens.remove(token)
    }

    fun invalidate(token: String) {
        refreshTokens.remove(token)
    }
}
