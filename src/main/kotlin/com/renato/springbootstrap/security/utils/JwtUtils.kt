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
    var jwtExpirationMs = 86400000L

    @Value("\${jwt.jwt-refresh-expiration-ms}")
    var jwtRefreshExpirationMs = 604800000L

    fun generateJwtToken(authentication: Authentication, expirationMs: Long = jwtExpirationMs): String {
        val payload = Payload(getClaims(authentication, expirationMs).toJSONObject())
        val header = JWSHeader(HS256)
        val signer = MACSigner(jwtSecret)
        val jwsObject = JWSObject(header, payload)

        // Sign object with signature
        jwsObject.sign(signer)
        return jwsObject.serialize()
    }

    fun generateRefreshJwtToken(authentication: Authentication): String {
        return generateJwtToken(authentication, jwtRefreshExpirationMs)
    }

    private fun getClaims(authentication: Authentication, expirationMs: Long): JWTClaimsSet {
        val userPrincipal = authentication.principal as UserSecurity
        return JWTClaimsSet.Builder()
            .claim(EMAIL_CLAIM, userPrincipal.email)
            .claim(ROLES_CLAIM, userPrincipal.roles)
            .claim(PASSWORD_CLAIM, userPrincipal.password)
            .claim(USER_ID_CLAIM, userPrincipal.userId)
            .jwtID(UUID.randomUUID().toString())
            .expirationTime(Date(Date().time + expirationMs))
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
        return UserSecurity(
            id = null,
            userId = userId,
            username = username,
            password = password,
            email = email,
            authorities = authorities,
            roles = roles
        )
    }

    fun validateJwtToken(authToken: String): Boolean {
        return try {
            val verifier = MACVerifier(jwtSecret)
            val jwsObject = JWSObject.parse(authToken)
            if (!jwsObject.verify(verifier)) {
                return false
            }
            val expiration = JWTClaimsSet.parse(jwsObject.payload.toJSONObject()).expirationTime ?: return false
            expiration.after(Date())
        } catch (ex: Exception) {
            logger.error("Invalid JWT token: {}", ex.message, ex)
            false
        }
    }
}