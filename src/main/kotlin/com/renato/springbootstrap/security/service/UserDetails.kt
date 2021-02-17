package com.renato.springbootstrap.security.service

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetails(var email: String, var myUsername: String, private var myPassword: String, private var myAuthorities: List<SimpleGrantedAuthority>, var roles : List<String>) : UserDetails {

    override fun getAuthorities(): List<SimpleGrantedAuthority> {
        return myAuthorities
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return myUsername
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return myPassword
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}