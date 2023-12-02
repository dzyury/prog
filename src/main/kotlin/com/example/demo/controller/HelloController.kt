package com.example.demo.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

//@Controller
@RestController
class HelloController {
    @GetMapping("/")
    fun index(@RequestParam("err", defaultValue = "0") err: Int): String {
        when(err) {
            1 -> throw RuntimeException("KO")
            2 -> throw IOException("IO")
        }
        return "Hello" // Cat("Kot", 4)
    }
}