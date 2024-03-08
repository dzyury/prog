package com.example.demo.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class BoardStatus(@JsonValue val value: String) {
    WAITING("waiting"),
    PLAYING("playing");

    companion object {
        @JsonCreator
        fun of(value: String): BoardStatus = entries.find { it.value == value }!!
    }
}