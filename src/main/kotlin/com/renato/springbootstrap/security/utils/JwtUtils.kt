package com.renato.springbootstrap.security.utils

import com.nimbusds.jose.*
import com.nimbusds.jose.JWSAlgorithm.HS256
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.renato.springbootstrap.security.service.UserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.lang.IllegalStateException
import java.util.*
import kotlin.streams.toList

@Component
class JwtUtils {

    companion object {
        const val ROLE_PREFIX = "ROLE_"
        const val SUBJECT_CLAIM = "sub"
        const val EMAIL_CLAIM = "email"
        const val ROLES_CLAIM = "roles"
        const val PASSWORD_CLAIM = "password"
    }

    private val logger: Logger = LoggerFactory.getLogger(JwtUtils::class.java)

    @Value("\${jwt.jwtSecret}")
    lateinit var jwtSecret: String

    @Value("\${jwt.jwtExpirationMs}")
    var jwtExpirationMs = 86400000

    fun generateJwtToken(authentication: Authentication): String {
        val payload = Payload(getClaims(authentication).toJSONObject())
        val header = JWSHeader(HS256)
        val signer = MACSigner(jwtSecret)
        val jwsObject = JWSObject(header, payload)

        // Sign object with signature
        jwsObject.sign(signer)
        return jwsObject.serialize()
    }

    private fun getClaims(authentication: Authentication): JWTClaimsSet {
        val userPrincipal = authentication.principal as UserDetails
        return JWTClaimsSet.Builder()
            .claim(EMAIL_CLAIM, userPrincipal.email)
            .claim(ROLES_CLAIM, userPrincipal.roles)
            .claim(PASSWORD_CLAIM, userPrincipal.password)
            .expirationTime(Date(Date().time + jwtExpirationMs))
            .issueTime(Date())
            .subject(userPrincipal.username)
            .build()
    }

    fun getUserNameFromJwtToken(token: String): String {
        return JWSObject.parse(token).payload.toJSONObject()[SUBJECT_CLAIM].toString()
    }

    fun getPasswordFromJwtToken(token: String): String {
        return JWSObject.parse(token).payload.toJSONObject()[PASSWORD_CLAIM].toString()
    }

    fun getEmailFromJwtToken(token: String): String {
        return JWSObject.parse(token).payload.toJSONObject()[EMAIL_CLAIM].toString()
    }

    fun getRolesFromJwtToken(token: String): List<String> {
        val roles = JWSObject.parse(token).payload.toJSONObject()[ROLES_CLAIM]  as List<*>
        return roles.filterIsInstance<String>().map {it}
    }

    fun getAuthoritiesFromJwtToken(token: String): List<SimpleGrantedAuthority> {
        return getRolesFromJwtToken(token).stream()
            .map { role -> SimpleGrantedAuthority(ROLE_PREFIX + role) }
            .toList()
    }

    fun toUserDetails(token : String): UserDetails {
        val username: String = getUserNameFromJwtToken(token)
        val email = getEmailFromJwtToken(token)
        val password = getPasswordFromJwtToken(token)
        val authorities = getAuthoritiesFromJwtToken(token)
        val roles = getRolesFromJwtToken(token)
        return UserDetails(email, username, password, authorities, roles)
    }

    fun validateJwtToken(authToken: String): Boolean {
        return try {
            val verifier = MACVerifier(jwtSecret)
            JWSObject.parse(authToken).verify(verifier)
        } catch (e: IllegalStateException) {
            logger.error("Illegal state exception: {}", e.message, e)
            false
        } catch (e: JOSEException) {
            logger.error("Invalid JWT token: {}", e.message, e)
            false
        }
    }
}