package com.renato.springbootstrap.security.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.UUID

class UserSecurity(
    val id: Long?,
    val userId: UUID,
    username: String,
    password: String,
    val email: String,
    val authorities: List<GrantedAuthority>,
    val roles : List<String>
) : User(username, password, authorities) {
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}