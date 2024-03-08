package com.example.demo.service

import com.example.demo.data.Board
import com.example.demo.data.User
import org.springframework.stereotype.Service
import java.util.*

@Service
class BoardService {
    val boards = TreeMap<Int, Board>()

    fun get(id: Int): Board {
        return Board.create(0, listOf())
    }

    fun findOrCreateBoard(user: User): Board {
        return Board.create(0, listOf())
    }

    fun turn(id: Int, name: String, details: String): Board {
        return Board.create(0, listOf())
    }
}