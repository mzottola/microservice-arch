package com.microservice.arch.authenticationservice.repository

import com.microservice.arch.authenticationservice.domain.User

interface UserRepository {

    fun findByUsernameAndPassword(username: String, password: String): User?

    fun exists(username: String): Boolean
}
