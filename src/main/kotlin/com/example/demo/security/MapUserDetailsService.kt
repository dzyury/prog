package com.example.demo.security

import com.example.demo.error.HttpException
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.User as SUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

data class User(val id: Int? = null, val name: String, val password: String)

@Service
class MapUserDetailsService(val encoder: PasswordEncoder) : UserDetailsService {
    private var lastId: Int = 0
    private val map = mutableMapOf<String, User>()

    override fun loadUserByUsername(username: String): UserDetails {
        val user = map[username] ?: throw UsernameNotFoundException("KO")
        return SUser(user.name, user.password, listOf())
    }

    fun add(username: String, password: String) {
        val encryptedPassword = encoder.encode(password)
        val user = User(lastId++, username, encryptedPassword)

        if (map.containsKey(user.name)) throw HttpException(HttpStatus.CONFLICT, "User exists")
        map[username] = user
    }

    // for testing only
    fun clear() {
        lastId = 0
        map.clear()
    }
}