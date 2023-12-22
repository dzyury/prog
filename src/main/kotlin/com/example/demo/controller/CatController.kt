package com.example.demo.controller

import com.example.demo.data.Cat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import javax.swing.Spring

//@Controller
@RestController
class CatController {
    @GetMapping("/cat/{name}/{age}", produces = ["application/json"])
    fun x(@PathVariable("name") name: String, @PathVariable("age") age: Int): Cat {
        return Cat(name, age)
    }
    @GetMapping("/cat/{name}/{age}", produces = ["application/xml"])
    fun j(@PathVariable("name") name: String, @PathVariable("age") age: Int): Cat {
        return Cat(name, age)
    }
}