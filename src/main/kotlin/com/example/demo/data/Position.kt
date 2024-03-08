package com.example.demo.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Position(@JsonValue val value: String) {
    WHITE("white"),
    BLACK("black");

    fun invert() = if (this == WHITE) BLACK else WHITE

    companion object {
        @JsonCreator
        fun of(value: String): Position = entries.find { it.value == value }!!
    }
}