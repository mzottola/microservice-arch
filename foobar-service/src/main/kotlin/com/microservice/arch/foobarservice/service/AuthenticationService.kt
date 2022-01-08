package com.microservice.arch.foobarservice.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient("authentication-service")
interface AuthenticationService {
    @RequestMapping(value = ["/user-info"], method = [RequestMethod.GET])
    fun findUser(@RequestHeader("Authorization") jwt: String): UserDto

    @RequestMapping(value = ["/api-key-info"], method = [RequestMethod.GET])
    fun findApiKeyAuthorities(@RequestHeader("X-API-KEY") apiKey: String): ApiKeyInformationDto
}

data class UserDto(
    val username: String,
    val authorities: Set<String>
)

data class ApiKeyInformationDto(
    val authorities: Set<String>
)
