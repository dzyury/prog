package com.example.demo.controller.game.room.board

import com.example.demo.data.Board
import com.example.demo.data.BoardTurn
import com.example.demo.data.User
import com.example.demo.service.BoardService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.userdetails.User as SUser

@RestController
class BoardController(val service: BoardService) {
    val user: SUser
        get() = SecurityContextHolder.getContext().authentication.principal as SUser

    @GetMapping("/game/{gid}/room/{rid}/board/{bid}")
    fun read(@PathVariable bid: Int): Board {
        return service.get(bid)
    }

    @PatchMapping("/game/{gid}/room/{rid}/board/any")
    fun create(@RequestBody user: User): Board {
        return service.findOrCreateBoard(user)
    }

    @PatchMapping("/game/{gid}/room/{rid}/board/{bid}")
    fun turn(@RequestBody turn: BoardTurn, @PathVariable bid: Int): Board {
        return service.turn(bid, user.username, turn.details)
    }
}