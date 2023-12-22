package com.example.demo.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable

data class Cat(val name: String, val age: Int)

@RestController
class TestController {

    @GetMapping("/test1")
    fun getCat(@RequestParam name: String, @RequestParam age: Int): Cat {
        return Cat(name, age)
    }
    @GetMapping("/cat/{name}/{age}")
    fun getCatByPath(@PathVariable name: String, @PathVariable age: Int): Cat {
        return Cat(name, age)
    }
}