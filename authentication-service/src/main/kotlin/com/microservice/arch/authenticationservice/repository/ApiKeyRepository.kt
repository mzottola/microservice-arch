package com.microservice.arch.authenticationservice.repository

interface ApiKeyRepository {
    fun validate(apiKey: String): Set<String>
}
