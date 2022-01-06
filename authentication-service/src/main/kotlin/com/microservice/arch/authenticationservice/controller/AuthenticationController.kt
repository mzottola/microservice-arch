package com.microservice.arch.authenticationservice.controller

import com.microservice.arch.authenticationservice.repository.UserRepository
import com.microservice.arch.authenticationservice.service.JwtParseException
import com.microservice.arch.authenticationservice.service.JwtService
import com.microservice.arch.authenticationservice.service.UserInvalidException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import javax.servlet.http.HttpServletRequest


@RestController
class AuthenticationController(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    @GetMapping("/login")
    fun askToken(@RequestBody userPasswordDto: UserPasswordDto): ResponseEntity<String> {
        val user = userRepository.findByUsernameAndPassword(userPasswordDto.username, userPasswordDto.password)
            ?: return ResponseEntity.badRequest().body("User not found")

        val jwt = jwtService.createJwt(user)
        return ResponseEntity.ok(jwt)
    }

    @GetMapping("/validate-token")
    fun validateToken(request: HttpServletRequest): ResponseEntity<Any> {
        val authorizationHeader = request.getHeader("Authorization")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is not present")

        val jwt = authorizationHeader.substring("Bearer ".length)
        jwtService.validateJwt(jwt)

        return ResponseEntity.ok(null)
    }

    @GetMapping("/user-info")
    fun userInfo(request: HttpServletRequest): ResponseEntity<UserInformationDto> {
        val jwt = request.getHeader("Authorization").substring("Bearer ".length)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        return jwtService
            .extractUserFromJwt(jwt).let {
                ResponseEntity.ok(
                    UserInformationDto(it.username, it.authorities)
                )
            }
    }
}

data class UserPasswordDto(var username: String, var password: String)
data class UserInformationDto(val username: String, val authorities: Set<String>)

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler(JwtParseException::class)
    fun handle(ex: JwtParseException?, request: WebRequest?): ResponseEntity<Any?>? {
        return ResponseEntity(ex!!.message, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(UserInvalidException::class)
    fun handle(ex: UserInvalidException?, request: WebRequest?): ResponseEntity<Any?>? {
        return ResponseEntity(ex!!.message, HttpStatus.UNAUTHORIZED)
    }
}
