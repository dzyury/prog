package com.example.demo.controller

import com.example.demo.data.Cat
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

//@Controller
@RestController
class TestController {
    @GetMapping("/test1", produces = ["application/xml"])
    fun x(@RequestParam("name", defaultValue = "0") name: String, @RequestParam("age", defaultValue = "0") age: Int): Cat {
        return Cat(name, age)
    }
    @GetMapping("/test1", produces = ["application/json"])
    fun j(@RequestParam("name", defaultValue = "KOT") name: String, @RequestParam("age", defaultValue = "0") age: Int): Cat {
        return Cat(name, age)
    }
}