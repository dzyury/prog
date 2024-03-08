package com.example.demo.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Role(@JsonValue val value: String) {
    PLAYER("player"),
    OBSERVER("observer");

    companion object {
        @JsonCreator
        fun of(value: String): Role = entries.find { it.value == value }!!
    }
}