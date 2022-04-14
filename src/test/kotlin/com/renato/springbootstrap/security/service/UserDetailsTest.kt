package com.renato.springbootstrap.security.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class UserDetailsTest {

    @Test
    fun `Sanity Test`() {
        val userDetails = UserDetails("username", "password", emptyList(), emptyList(), "email")

        Assertions.assertThat(userDetails.isAccountNonExpired).isTrue
        Assertions.assertThat(userDetails.isAccountNonLocked).isTrue
        Assertions.assertThat(userDetails.isEnabled).isTrue
        Assertions.assertThat(userDetails.isCredentialsNonExpired).isTrue
    }
}