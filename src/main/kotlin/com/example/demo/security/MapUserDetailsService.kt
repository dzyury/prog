package com.example.demo.security

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class MapUserDetailsService(val encoder: PasswordEncoder) : UserDetailsService {
    private val map = mutableMapOf<String, User>()

//    init {
//        add("cat", "cat")
//    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = map[username] ?: throw UsernameNotFoundException("KO")
        return User(user.username, user.password, user.authorities)
    }

    fun add(username: String, password: String) {
        val encryptedPassword = encoder.encode(password)
        println("pass: $encryptedPassword")
        val user = User(username, encryptedPassword, listOf())
        map[username] = user
    }
}