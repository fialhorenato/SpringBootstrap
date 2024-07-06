package com.renato.springbootstrap.security.service.impl

import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByUsername(username)
            ?.let { UserSecurity(
                it.id,
                it.username,
                it.password,
                it.email,
                it.roles.map { role -> SimpleGrantedAuthority(role.role) },
                it.roles.map { role -> role.role }.toList())
            }
            ?: throw UsernameNotFoundException("User not found")
    }
}