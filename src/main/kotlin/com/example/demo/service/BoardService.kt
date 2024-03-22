package com.example.demo.service

import com.example.demo.data.*
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
        val oldBoard: Board = get(id)
        if (oldBoard.status == BoardStatus.BLACK_WON || oldBoard.status == BoardStatus.WHITE_WON || oldBoard.status == BoardStatus.DRAW) {
            throw HttpException(HttpStatus.BAD_REQUEST, "Invalid turn")
        } else {
            val player: User? = oldBoard.users.find { it.name == name }
            if (player != null) {
                if (player.position == oldBoard.turn) {
                    if (oldBoard.details == details) {
                        throw HttpException(HttpStatus.BAD_REQUEST, "The only turn is allowed")
                    } else {
                        if (details.length != 9) {
                            throw HttpException(HttpStatus.BAD_REQUEST, "Invalid details length")
                        } else {
                            if (oldBoard.details.zip(details).count { it.first != it.second } >= 2) {
                                throw HttpException(HttpStatus.BAD_REQUEST, "The only turn is allowed")
                            } else {
                                val changes = oldBoard.details.zip(details).find { it.first != it.second }
                                if (changes!!.first != '_') {
                                    throw HttpException(HttpStatus.BAD_REQUEST, "Invalid turn")
                                } else {
                                    var corTurn = '_'
                                    if (player.position == Position.BLACK) {
                                        corTurn = '0'
                                    } else {
                                        corTurn = 'x'
                                    }
                                    if (changes.second != corTurn) {
                                        throw HttpException(HttpStatus.BAD_REQUEST, "Invalid turn")
                                    } else {
                                        val boardField = Array(3) { CharArray(3) }
                                        var index = 0
                                        for (i in 0 until 3) {
                                            for (j in 0 until 3) {
                                                boardField[i][j] = details[index]
                                                index++
                                            }
                                        }
                                        val changeIndex = oldBoard.details.zip(details).indexOf(changes)
                                        val x = changeIndex / 3
                                        val y = changeIndex % 3

                                        if (check(x, y, boardField)) {
                                            if (player.position == Position.BLACK) {
                                                val updatedBoard = oldBoard.copy(
                                                    details = details,
                                                    turn = oldBoard.turn.invert(),
                                                    status = BoardStatus.BLACK_WON
                                                )
                                                boards[id] = updatedBoard
                                                return updatedBoard
                                            } else {
                                                val updatedBoard = oldBoard.copy(
                                                    details = details,
                                                    turn = oldBoard.turn.invert(),
                                                    status = BoardStatus.WHITE_WON
                                                )
                                                boards[id] = updatedBoard
                                                return updatedBoard
                                            }
                                        } else {
                                            if (details.count { it == '_' } == 0){
                                                val updatedBoard = oldBoard.copy(
                                                    details = details,
                                                    turn = oldBoard.turn.invert(),
                                                    status = BoardStatus.DRAW
                                                )
                                                boards[id] = updatedBoard
                                                return updatedBoard
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    throw HttpException(HttpStatus.BAD_REQUEST, "Not your turn")
                }
            } else {
                throw HttpException(HttpStatus.NOT_FOUND, "Board with given user not found")
            }
        }
        val updatedBoard = oldBoard.copy(
            turn = oldBoard.turn.invert(),
            details = details
        )
        boards[id] = updatedBoard
        return updatedBoard
    }

}


private fun check(x: Int, y: Int, board: Array<CharArray>): Boolean {
    val x1 = (3 + x - 1) % 3
    val y1 = (3 + y - 1) % 3
    val x2 = (3 + x + 1) % 3
    val y2 = (3 + y + 1) % 3

    if (((board[x1][y] == board[x][y]) and (board[x2][y] == board[x][y])) or
        ((board[x][y1] == board[x][y]) and (board[x][y] == board[x][y2]))
    ) {
        return true
    }
    if ((x == y) and (board[x1][y1] == board[x][y]) and (board[x2][y2] == board[x][y])) {
        return true
    }

    if ((x + y == 2) and (board[x1][y2] == board[x][y]) and (board[x2][y1] == board[x][y])) {
        return true
    }
    return false
}