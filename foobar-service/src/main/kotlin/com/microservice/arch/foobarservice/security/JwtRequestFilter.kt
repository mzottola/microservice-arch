package com.microservice.arch.foobarservice.security

import com.microservice.arch.foobarservice.service.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtRequestFilter(
    private val authenticationService: AuthenticationService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        val apiKey = request.getHeader("X-API-KEY")
        if (header == null && apiKey == null) {
            response.status = HttpStatus.UNAUTHORIZED.value()
        } else {
            if (header != null) {
                val userDto = authenticationService.findUser(header)
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(
                        userDto.username,
                        null,
                        userDto.authorities.map { SimpleGrantedAuthority(it) }
                    )
            } else {
                val apiKeyAuthorities = authenticationService.findApiKeyAuthorities(apiKey)
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(
                        null,
                        null,
                        apiKeyAuthorities.authorities.map { SimpleGrantedAuthority(it) }
                    )
            }
        }
        filterChain.doFilter(request, response)
    }
}
