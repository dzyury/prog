package com.example.demo.controller

import com.example.demo.data.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

var people: MutableList<User> = mutableListOf()

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
            val newUser = User(people.size, user.name, user.password)
            people.add(newUser)
            return ResponseEntity<Any>(newUser, HttpStatus.OK)
        }
    }
}