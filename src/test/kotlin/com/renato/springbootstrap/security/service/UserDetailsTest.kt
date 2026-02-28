package com.renato.springbootstrap.security.service

import com.renato.springbootstrap.security.domain.UserSecurity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class UserDetailsTest {

    @Test
    fun `given_user_security_when_checked_then_all_account_flags_are_active`() {
        val userDetails = UserSecurity(
            id = 1L,
            userId = UUID.randomUUID(),
            username = "username",
            password = "password",
            email = "email",
            authorities = emptyList(),
            roles = emptyList(),
        )

        assertThat(userDetails.isAccountNonExpired).isTrue
        assertThat(userDetails.isAccountNonLocked).isTrue
        assertThat(userDetails.isEnabled).isTrue
        assertThat(userDetails.isCredentialsNonExpired).isTrue
        assertThat(userDetails.id).isEqualTo(1L)
    }
}
