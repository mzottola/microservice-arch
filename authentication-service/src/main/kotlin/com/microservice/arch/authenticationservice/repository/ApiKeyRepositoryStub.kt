package com.microservice.arch.authenticationservice.repository

import com.microservice.arch.authenticationservice.domain.ApiKey
import org.springframework.stereotype.Repository

@Repository
class ApiKeyRepositoryStub : ApiKeyRepository {

    private val apiKeys = setOf(
        ApiKey("AZERTYUIOP12345", setOf("ROLE_API_KEY"))
    )

    override fun validate(apiKey: String): Set<String> =
        apiKeys.find { it.content == apiKey }!!.authorities
}
