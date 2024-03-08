package com.example.demo.security

import com.example.demo.error.HttpException
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.User as SUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class MapUserDetailsService(val encoder: PasswordEncoder) : UserDetailsService {
    private val map = mutableMapOf<String, SUser>()

//    init {
//        add("cat", "cat")
//    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = map[username] ?: throw UsernameNotFoundException("KO")
        return SUser(user.username, user.password, user.authorities)
    }

    fun add(username: String, password: String) {
        val encryptedPassword = encoder.encode(password)
        val user = SUser(username, encryptedPassword, listOf())

        if (map.containsKey(user.username)) throw HttpException(HttpStatus.CONFLICT, "User exists")
        map[username] = user
    }

    // for testing only
    fun clear() {
        map.clear()
    }
}