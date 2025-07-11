package com.renato.springbootstrap.security.utils

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm.HS256
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.renato.springbootstrap.security.domain.UserSecurity
import com.renato.springbootstrap.security.exception.JwtException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils {

    companion object {
        const val ROLE_PREFIX = "ROLE_"
        const val SUBJECT_CLAIM = "sub"
        const val EMAIL_CLAIM = "email"
        const val ROLES_CLAIM = "roles"
        const val PASSWORD_CLAIM = "password"
        const val USER_ID_CLAIM = "user_id"
    }

    private val logger: Logger = LoggerFactory.getLogger(JwtUtils::class.java)

    @Value("\${jwt.jwt-secret}")
    lateinit var jwtSecret: String

    @Value("\${jwt.jwt-expiration-ms}")
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
        val userPrincipal = authentication.principal as UserSecurity
        return JWTClaimsSet.Builder()
            .claim(EMAIL_CLAIM, userPrincipal.email)
            .claim(ROLES_CLAIM, userPrincipal.roles)
            .claim(PASSWORD_CLAIM, userPrincipal.password)
            .claim(USER_ID_CLAIM, userPrincipal.userId)
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

    private fun getUserIdFromJwtToken(token: String): UUID {
        val userId = JWSObject.parse(token).payload.toJSONObject()[USER_ID_CLAIM].toString()
        return UUID.fromString(userId)
    }

    fun getRolesFromJwtToken(token: String): List<String> {
        val roles = JWSObject.parse(token).payload.toJSONObject()[ROLES_CLAIM] as List<*>
        return roles.filterIsInstance<String>().map {it}
    }

    fun getAuthoritiesFromJwtToken(token: String): List<SimpleGrantedAuthority> {
        return getRolesFromJwtToken(token).stream()
            .map { role -> SimpleGrantedAuthority(ROLE_PREFIX + role) }
            .toList()
    }

    fun toUserDetails(token : String): UserSecurity {
        val username: String = getUserNameFromJwtToken(token)
        val userId : UUID = getUserIdFromJwtToken(token)
        val email = getEmailFromJwtToken(token)
        val password = getPasswordFromJwtToken(token)
        val authorities = getAuthoritiesFromJwtToken(token)
        val roles = getRolesFromJwtToken(token)
        return UserSecurity(null, userId, username, password, email, authorities, roles)
    }

    fun validateJwtToken(authToken: String): Boolean {
        return try {
            val verifier = MACVerifier(jwtSecret)
            JWSObject.parse(authToken).verify(verifier)
        } catch (ex: Exception) {
            logger.error("Invalid JWT token: {}", ex.message, ex)
            false
        }
    }
}