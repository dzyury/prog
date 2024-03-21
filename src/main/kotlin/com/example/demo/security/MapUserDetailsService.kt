package com.example.demo.security

import com.example.demo.error.HttpException
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.security.core.userdetails.User as SUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap


data class User(val id: Int? = null, val name: String, val password: String)

@Service
class MapUserDetailsService(val encoder: PasswordEncoder) : UserDetailsService {
    private var lastId: Int = 0
    private val map = ConcurrentHashMap<String, User>()

    override fun loadUserByUsername(username: String): UserDetails {
        val user = map[username] ?: throw UsernameNotFoundException("KO")
        return SUser(user.name, user.password, listOf())
    }

    fun add(username: String, password: String) {
        val encryptedPassword = encoder.encode(password)
        val user = User(lastId++, username, encryptedPassword)
        val message = "User is already registered"
        map.compute(username) { _, v -> if (v == null) user else throw HttpException(CONFLICT, message) }
    }

    // for testing only
    fun clear() {
        lastId = 0
        map.clear()
    }
}