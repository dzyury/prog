package com.example.demo.controller.game.room.board

import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BoardController {
    @PatchMapping("/game/{gid}/room/{rid}/board/any")
    fun create() = "OK"
}