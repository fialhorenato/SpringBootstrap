package com.renato.springbootstrap.security.service

import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

class UserDetails(username: String, password: String, authorities: List<GrantedAuthority>, val roles : List<String>, val email : String) : User(username, password, authorities) {
    override fun isEnabled(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}