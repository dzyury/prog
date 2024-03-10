package com.example.demo.security

import com.example.demo.error.HttpException
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import org.springframework.security.core.userdetails.User as SUser

@Service
class MapUserDetailsService(val encoder: PasswordEncoder) : UserDetailsService {
    private val map = ConcurrentHashMap<String, SUser>()

//    init {
//        add("kit", "cat")
//        add("cat", "cat")
//    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = map[username] ?: throw UsernameNotFoundException("KO")
        return SUser(user.username, user.password, user.authorities)
    }

    fun add(username: String, password: String) {
        val encryptedPassword = encoder.encode(password)
        val user = SUser(username, encryptedPassword, listOf())
        val message = "User is already registered"
        map.compute(username) { _, v -> if (v == null) user else throw HttpException(CONFLICT, message) }
    }

    // for testing only
    fun clear() {
        map.clear()
    }
}