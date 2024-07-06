package com.renato.springbootstrap.security.config

import com.renato.springbootstrap.security.filter.JwtAuthorizationFilter
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.toAnyEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true
)
class SecurityConfig(
    private val jwtAuthorizationFilter: JwtAuthorizationFilter ,
    private val userDetailsService: UserDetailsService
) {

    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .sessionManagement { STATELESS }
            .authorizeHttpRequests { authorizeHttpRequests(it) }
            .userDetailsService(userDetailsService)
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    private fun authorizeHttpRequests(it: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        it
                .requestMatchers("/security/**").permitAll()
                .requestMatchers(toAnyEndpoint()).permitAll()
                .requestMatchers("/hello-world/insecure").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .anyRequest()
                .authenticated()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}