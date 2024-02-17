package com.example.demo.controller

import com.example.demo.data.User
import com.example.demo.security.MapUserDetailsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.example.demo.security.SecurityConfiguration


@RestController
class UserController {
    @PostMapping("/user")
    fun create(@RequestBody user: User) : ResponseEntity<*>{
        id += 1
        return if (everything_is_fine(user)) {
            password_memory.add(user.name, user.password)
            val new_user = password_memory.loadUserByUsername(user.name)
            memory.add(User(user.name, new_user.password, id))
            ResponseEntity<Any>(User(user.name, user.password, id), HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.CONFLICT)
        }
    }
}


var Encoder = SecurityConfiguration().passwordEncoder()
var password_memory = MapUserDetailsService(Encoder)

var memory: MutableList<User> = mutableListOf()
var id: Int = 0

fun everything_is_fine(user: User): Boolean{
    for (i in 0 until memory.size) {
        if(memory[i].name == user.name){
            return false
        }
    }
    return true
}


