package com.microservice.arch.foobarservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

// INFO (mzo) remove this class to avoid default "user:password configuration"
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
@EnableFeignClients
@EnableEurekaClient
class FoobarServiceApplication

fun main(args: Array<String>) {
    runApplication<FoobarServiceApplication>(*args)
}

@RestController
class FooBarController {

    @GetMapping("/open")
    fun open(): String {
        return "Accessing foobar-service"
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun user(): String {
        return "User restricted"
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_API_KEY')")
    fun admin(): String {
        return "Admin restricted"
    }
}
