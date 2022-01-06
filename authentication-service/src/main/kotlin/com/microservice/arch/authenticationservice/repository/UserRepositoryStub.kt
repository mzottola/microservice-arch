package com.microservice.arch.authenticationservice.repository

import com.microservice.arch.authenticationservice.domain.User
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryStub : UserRepository {

    private val users: Set<User> = setOf(
        User("john.doe", "password", setOf("ROLE_USER")),
        User("admin", "admin", setOf("ROLE_USER", "ROLE_ADMIN"))
    )

    override fun findByUsernameAndPassword(username: String, password: String) =
        users.firstOrNull { it.hasUsernameAndPassword(username, password) }

    override fun exists(username: String) = users.any {it.username == username}
}
