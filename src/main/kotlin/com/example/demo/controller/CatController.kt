package com.example.demo.controller

import com.example.demo.data.Cat
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CatController {
    @GetMapping("/cat/{name}/{age}")
    fun index(
        @PathVariable name: String,
        @PathVariable age: Int,
    ): Cat {
        println("principal: ${SecurityContextHolder.getContext().authentication.principal}")
        val user = SecurityContextHolder.getContext().authentication.principal as? User
        return Cat(user?.username ?: name, age)
    }
}