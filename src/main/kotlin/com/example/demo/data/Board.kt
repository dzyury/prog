package com.example.demo.data

import com.example.demo.data.BoardStatus.WAITING
import com.example.demo.data.Position.WHITE

data class Board(
    val id: Int,
    val turn: Position,
    val status: BoardStatus,
    val details: String,
    val users: List<User>
) {
    companion object {
        const val INIT_DETAILS = "_________"

        fun create(id: Int, users: List<User>): Board {
            return Board(id, WHITE, WAITING, INIT_DETAILS, users)
        }
    }
}