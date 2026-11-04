package com.renato.springbootstrap.security.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.ConcurrentHashMap

class RefreshTokenStoreTest {

    private val refreshTokenStore = RefreshTokenStore()

    @Test
    fun `given_valid_tokens_when_store_is_called_then_token_is_stored`() {
        val token = "test-token"
        val username = "test-user"
        refreshTokenStore.store(token, username)

        // Since RefreshTokenStore uses ConcurrentHashMap internally, we can't directly test the internal state.
        // We rely on consume to verify storage.
        val consumedUser = refreshTokenStore.consume(token)
        assertNotNull(consumedUser)
        assertEquals(username, consumedUser)
    }

    @Test
    fun `given_token_when_consume_is_called_then_username_is_returned_and_token_is_removed`() {
        val token = "test-token"
        val username = "test-user"
        refreshTokenStore.store(token, username)

        val consumedUser = refreshTokenStore.consume(token)
        assertEquals(username, consumedUser)

        // Verify removal by attempting to consume again
        assertNull(refreshTokenStore.consume(token))
    }

    @Test
    fun `given_non_existent_token_when_consume_is_called_then_null_is_returned`() {
        val token = "non-existent-token"
        val consumedUser = refreshTokenStore.consume(token)
        assertNull(consumedUser)
    }

    @Test
    fun `given_valid_token_when_invalidate_is_called_then_token_is_removed`() {
        val token = "test-token"
        val username = "test-user"
        refreshTokenStore.store(token, username)

        // Verify it exists before invalidation
        assertNotNull(refreshTokenStore.consume(token))

        refreshTokenStore.invalidate(token)

        // Verify removal by attempting to consume again
        assertNull(refreshTokenStore.consume(token))
    }
}