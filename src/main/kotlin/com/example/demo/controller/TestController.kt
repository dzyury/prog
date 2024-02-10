package com.example.demo.controller

import com.example.demo.data.Cat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

//@Controller
@RestController
class TestController {
    @GetMapping("/test1")
    fun index(
        @RequestParam name: String,
        @RequestParam age: Int,
    ) = Cat(name, age)
}