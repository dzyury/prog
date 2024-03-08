package com.example.demo.controller

import com.example.demo.security.MapUserDetailsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(val authService : MapUserDetailsService) {
    @PostMapping("/user")
    fun create(@RequestBody user: User): User {
        authService.add(user.name, user.password)
        return user
    }
}