package com.example.demo.data

data class User(val id: Int? = null, val name: String, val password: String)

data class UserS(
    val name: String,
    val role: Role,
    val position: Position
)