package com.example.demo.controller

import com.example.demo.data.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class UserController {
    @PostMapping("/user")
    fun create(@RequestBody user: User) : ResponseEntity<*>{
        id += 1
        return if (everything_is_fine(user)) {
            memory.add(User(user.name, user.password, id))
            ResponseEntity<Any>(User(user.name, user.password, id), HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.CONFLICT)
        }
    }
}




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


