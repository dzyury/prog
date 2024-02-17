package com.example.demo.controller

import com.example.demo.data.User
import com.example.demo.security.MapUserDetailsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class UserController(val userService: MapUserDetailsService) {
    @PostMapping("/user")
    fun create(@RequestBody user: User): ResponseEntity<Any> {
        val status = userService.add(user.name, user.password)
        return if (status)
            ResponseEntity<Any>(userService.getUserByName(user.name), HttpStatus.OK)
        else
            ResponseEntity<Any>(null, HttpStatus.CONFLICT)
    }
}