package com.example.demo.controller

import com.example.demo.data.Cat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

//@Controller
@RestController
class CatController {
    @GetMapping("/cat/{name}/{age}")
    fun index(
        @PathVariable name: String,
        @PathVariable age: Int,
    ) = Cat(name, age)
}