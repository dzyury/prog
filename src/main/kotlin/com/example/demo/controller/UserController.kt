package com.example.demo.controller

import com.example.demo.data.User
import com.example.demo.security.MapUserDetailsService
import com.example.demo.security.SecurityConfiguration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

var people: MutableList<User> = mutableListOf()
var peoplePasswords = MapUserDetailsService(SecurityConfiguration().passwordEncoder())

fun isInPeople(name: String): Boolean {
    for (user in people) if (user.name == name) return true
    return false
}

@RestController
class UserController {
    @PostMapping("/user")
    fun create(@RequestBody user: User): ResponseEntity<Any> {
        if (isInPeople(user.name)) {
            return ResponseEntity<Any>(null, HttpStatus.CONFLICT)
        } else {
            peoplePasswords.add(user.name, user.password)
            val newUser = peoplePasswords.loadUserByUsername(user.name)
            people.add(User(people.size, newUser.username, newUser.password))
            return ResponseEntity<Any>(User(people.size, user.name, user.password), HttpStatus.OK)
        }
    }
}