package com.debugdesk.mono.model

data class ValidLoginData(
    val username: String,
    val password: String,
    val rememberMe: Boolean = false
)

data class LoginData(
    val username: String = "qwert",
    val password: String = "wrtertwrt",
    val rememberMe: Boolean = true,
    val wrongUsername: Boolean = false,
    val wrongPassword: Boolean = false,
    val showPassword: Boolean = false,
    val enableUserInteraction: Boolean = true,
    val loginSuccess: Boolean = false,
    val message: Int? = null
)
