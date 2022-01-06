package com.microservice.arch.authenticationservice.service

import com.microservice.arch.authenticationservice.domain.User
import com.microservice.arch.authenticationservice.repository.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService(
    private val userRepository: UserRepository
) {

    private val secretkey = "mysecretkeytosign".toByteArray()

    fun createJwt(user: User): String {
        val now = Date()
        return Jwts.builder()
            .setSubject(user.username)
            .claim("authorities", user.authorities)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + 60 * 10 * 1000))
            .signWith(SignatureAlgorithm.HS512, secretkey)
            .compact()
    }

    fun validateJwt(jwt: String) {
        val claims = extractClaims(jwt)
        val username: String = claims.subject
        if (userRepository.exists(username).not()) {
            throw UserInvalidException()
        }
    }

    fun extractUserFromJwt(jwt: String): User {
        return extractClaims(jwt).let {
            User(
                username = it.subject,
                password = "",
                authorities = extractAuthorities(it["authorities"]!!)
            )
        }
    }

    private fun extractClaims(jwt: String): Claims {
        try {
            return Jwts.parser()
                .setSigningKey(secretkey)
                .parseClaimsJws(jwt)
                .body
        } catch (ex: Exception) {
            throw JwtParseException(ex)
        }
    }

    private fun extractAuthorities(authoritiesFromClaims: Any): Set<String> =
        (authoritiesFromClaims as List<String>).toSet()
}

class JwtParseException(cause: Throwable) : RuntimeException(cause)
class UserInvalidException : RuntimeException("The user does not exist anymore")
