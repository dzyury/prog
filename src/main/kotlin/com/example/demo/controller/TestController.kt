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
    fun index(@RequestParam("name") name : String, @RequestParam("age") age : Int,  @RequestParam("err", defaultValue = "0") err: Int): Cat {
        when(err) {
            1 -> throw RuntimeException("KO")
            2 -> throw IOException("IO")
        }

        return Cat(name, age) // Cat("Kot", 4)
    }
}