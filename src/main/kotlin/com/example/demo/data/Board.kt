package com.example.demo.data

data class Board(
    val id: Int,
    val turn: Position,
    val status: BoardStatus,
    val details: String,
    val users: List<User>
) {
    companion object {
        const val INIT_DETAILS = "_________"
    }
}