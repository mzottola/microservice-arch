package com.microservice.arch.authenticationservice.domain

class User(
    val username: String,
    val password: String,
    val authorities: Set<String>
) {
    fun hasUsernameAndPassword(username: String, password: String) =
        hasUsername(username) && hasPassword(password)
    private fun hasUsername(username: String) = this.username == username
    private fun hasPassword(password: String) = this.password == password
}
