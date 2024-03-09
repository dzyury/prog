package com.example.demo.service

import com.example.demo.data.Board
import com.example.demo.data.BoardStatus
import com.example.demo.data.Role
import com.example.demo.data.User
import org.springframework.stereotype.Service
import java.util.*
import com.example.demo.error.HttpException
import org.springframework.http.HttpStatus

@Service
class BoardService {
    val boards = TreeMap<Int, Board>()

    fun get(id: Int): Board {
        if (!boards.containsKey(id)) throw HttpException(HttpStatus.NOT_FOUND, "Board not found: $id")
        return boards[id]!!
    }

    fun findOrCreateBoard(user: User): Board {
        val waitingBoard: Board? = boards.values.find { it.status == BoardStatus.WAITING }
        return if (waitingBoard != null) {
            val playerUser = waitingBoard.users.find { it.role == Role.PLAYER }
            if (user.name == playerUser!!.name) return waitingBoard
            val updatedUser =
                if (playerUser.position == user.position) user.copy(position = user.position.invert()) else user.copy()
            val updatedBoard = waitingBoard.copy(
                status = BoardStatus.PLAYING,
                users = waitingBoard.users + updatedUser
            )
            boards[waitingBoard.id] = updatedBoard
            updatedBoard
        } else {
            val newBoard = Board.create(boards.size + 1, listOf(user))
            boards[newBoard.id] = newBoard
            newBoard
        }
    }
    fun turn(id: Int, name: String, details: String): Board {
        return Board.create(0, listOf())
    }

}