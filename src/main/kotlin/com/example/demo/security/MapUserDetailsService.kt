package com.example.demo.security

import com.example.demo.data.User
import org.springframework.security.core.userdetails.User as SUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class MapUserDetailsService(val encoder: PasswordEncoder) : UserDetailsService {
    private var lastId: Int = 0
    private val map = mutableMapOf<String, User>()

//    init {
//        add("cat", "cat")
//    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = map[username] ?: throw UsernameNotFoundException("KO")
        return SUser(user.name, user.password, listOf())
    }

    fun add(username: String, password: String): Boolean {
        if (username in map)
            return false
        val encryptedPassword = encoder.encode(password)
        println("pass: $encryptedPassword")
        val user = User(lastId++, username, encryptedPassword)
        map[username] = user
        return true
    }

    fun getUserByName(name: String): User? {
        return map[name]
    }
}