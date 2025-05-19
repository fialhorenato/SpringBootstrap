package com.renato.springbootstrap.security.service

import com.renato.springbootstrap.security.domain.UserSecurity
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

class UserDetailsTest {

    @Test
    fun `Sanity Test`() {
        val userDetails = UserSecurity(1L, UUID.randomUUID(),"username", "password", "email", emptyList(), emptyList())

        Assertions.assertThat(userDetails.isAccountNonExpired).isTrue
        Assertions.assertThat(userDetails.isAccountNonLocked).isTrue
        Assertions.assertThat(userDetails.isEnabled).isTrue
        Assertions.assertThat(userDetails.isCredentialsNonExpired).isTrue
    }
}