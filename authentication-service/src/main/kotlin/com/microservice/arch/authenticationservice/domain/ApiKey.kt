package com.microservice.arch.authenticationservice.domain

class ApiKey(
    val content: String,
    val authorities: Set<String>,
)

