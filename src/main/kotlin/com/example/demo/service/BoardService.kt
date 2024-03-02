package com.example.demo.service

import com.example.demo.data.Board
import com.example.demo.data.User
import com.example.demo.error.HttpException
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Service

@Service
class BoardService {
    private val boards = HashMap<Int, Board>()

    fun get(id: Int) = boards[id] ?: throw HttpException(NOT_FOUND, "Board not found: $id")

    fun findOrCreateBoard(user: User): Board {
        throw HttpException(INTERNAL_SERVER_ERROR, "Not yet implemented")
    }

    fun turn(id: Int, username: String, details: String): Board {
        throw HttpException(INTERNAL_SERVER_ERROR, "Not yet implemented")
    }
}