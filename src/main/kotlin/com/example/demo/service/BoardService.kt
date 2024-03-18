package com.example.demo.service

import com.example.demo.data.*
import com.example.demo.error.HttpException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

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
        if (!boards.containsKey(id)) throw HttpException(HttpStatus.NOT_FOUND, "Board not found: $id")
        val board = boards[id]!!
        val user = board.users.find { it.name == name }
        if (user == null) throw HttpException(HttpStatus.NOT_FOUND, "Board with given user not found")
        if (user.position != board.turn) throw HttpException(HttpStatus.BAD_REQUEST, "Not your turn")
        if (board.details == details) throw HttpException(HttpStatus.BAD_REQUEST, "The only turn is allowed")
        if (details.length != 9) throw HttpException(HttpStatus.BAD_REQUEST, "Invalid details length")
        if (board.details.zip(details).count { it.first != it.second } > 1)
            throw HttpException(HttpStatus.BAD_REQUEST, "The only turn is allowed")
        val changes = board.details.zip(details).find { it.first != it.second }!!
        if (changes.second == '0' && user.position == Position.WHITE ||
            changes.second == 'x' && user.position == Position.BLACK
        ) throw HttpException(HttpStatus.BAD_REQUEST, "Invalid turn")
        if (changes.first != '_') throw HttpException(HttpStatus.BAD_REQUEST, "Invalid turn")
        if (board.status.ordinal >= BoardStatus.DRAW.ordinal) throw HttpException(
            HttpStatus.BAD_REQUEST,
            "Invalid turn"
        )

        val boardMatrix = Array(3) { CharArray(3) }
        details.chunked(3).forEachIndexed { index, line -> boardMatrix[index] = line.toCharArray() }

        val index: Int = board.details.zip(details).indexOf(changes)
        val x = index / 3
        val y = index % 3
        if (check(boardMatrix, x, y)) {
            return if (user.position == Position.WHITE) {
                val updatedBoard = board.copy(
                    turn = board.turn.invert(),
                    status = BoardStatus.WHITE_WON,
                    details = details
                )
                boards[id] = updatedBoard
                updatedBoard
            } else {
                val updatedBoard = board.copy(
                    turn = board.turn.invert(),
                    status = BoardStatus.BLACK_WON,
                    details = details
                )
                boards[id] = updatedBoard
                updatedBoard
            }
        } else if (details.count { it == '_' } == 0) {
            val updatedBoard = board.copy(
                turn = board.turn.invert(),
                status = BoardStatus.DRAW,
                details = details
            )
            boards[id] = updatedBoard
            return updatedBoard
        }

        val updatedBoard = board.copy(
            turn = board.turn.invert(),
            details = details
        )
        boards[id] = updatedBoard
        return updatedBoard
    }

    private fun check(board: Array<CharArray>, x: Int, y: Int): Boolean {
        val x1 = (3 + x - 1) % 3
        val y1 = (3 + y - 1) % 3
        val x2 = (3 + x + 1) % 3
        val y2 = (3 + y + 1) % 3

        return (((board[x1][y] == board[x][y] && board[x][y] == board[x2][y]) ||
                (board[x][y1] == board[x][y] && board[x][y] == board[x][y2]) ||
                (x == y && board[x1][y1] == board[x][y] && board[x][y] == board[x2][y2]) ||
                (x == 2 - y && board[x1][y2] == board[x][y] && board[x][y] == board[x2][y1])))
    }
}