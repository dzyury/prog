package com.example.demo.controller

import com.example.demo.data.*
import com.example.demo.data.Board.Companion.INIT_DETAILS
import com.example.demo.data.BoardStatus.*
import com.example.demo.security.MapUserDetailsService
import com.example.demo.service.BoardService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*
import com.example.demo.data.User as GUser

@SpringBootTest
@AutoConfigureMockMvc
@Suppress("NonAsciiCharacters")
class BoardControllerGameTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var authService: MapUserDetailsService

    @Autowired
    private lateinit var boardService: BoardService

    private val cat = GUser("cat", Role.PLAYER, Position.WHITE)
    private val kit = GUser("kit", Role.PLAYER, Position.BLACK)
    private val kot = GUser("kot", Role.PLAYER, Position.WHITE)

    fun auth(user: String, password: String): String {
        val code = Base64.getEncoder().encodeToString("$user:$password".toByteArray())
        return "Basic $code"
    }

    @BeforeEach
    fun start() {
        for (gameUser in listOf(kit, kot, cat)) {
            val user = User(gameUser.name, "cat")
            val content = mapper.writeValueAsString(user)
            val register = MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
            mvc.perform(register)
                .andExpect(MockMvcResultMatchers.status().isOk)
        }

        val expectedBoard1 = Board(1, Position.WHITE, WAITING, INIT_DETAILS, listOf(cat))
        val expectedBoard2 = Board(1, Position.WHITE, PLAYING, INIT_DETAILS, listOf(cat, kit))
        fun checker(expectedBoard: Board): (MvcResult) -> Unit = {
            assertEquals(200, it.response.status)
            val board: Board = mapper.readValue(it.response.contentAsString)
            assertEquals(expectedBoard, board)
        }

        createBoard(cat).andExpect(checker(expectedBoard1))
        createBoard(kit).andExpect(checker(expectedBoard2))
    }

    @AfterEach
    fun clear() {
        authService.clear()
        boardService.boards.clear()
    }

    @Test
    fun `несуществующая доска приводит к ошибке 404`() {
        turn(INIT_DETAILS, cat, 2).andExpect {
            assertEquals(404, it.response.status)
            assertEquals("{\"message\":\"Board not found: 2\"}", it.response.contentAsString)
        }
    }

    @Test
    fun `попытка игры не на своей доске приводит к ошибке`() {
        turn("x________", kot, 1).andExpect { checkError(it, NOT_FOUND, "Board with given user not found") }
    }

    @Test
    fun `ход не в своё время приводит к ошибке`() {
        turn("x________", kit, 1).andExpect { checkError(it, BAD_REQUEST, "Not your turn") }
    }

    @Test
    fun `ход без изменения позиции приводит к ошибке`() {
        turn(INIT_DETAILS, cat, 1).andExpect { checkError(it, BAD_REQUEST, "The only turn is allowed") }
    }

    @Test
    fun `ход с изменением более одной позиции приводит к ошибке`() {
        turn("_______xx", cat, 1).andExpect { checkError(it, BAD_REQUEST, "The only turn is allowed") }
    }

    @Test
    fun `ход с длинным details приводит к ошибке`() {
        turn("________xx", cat, 1).andExpect { checkError(it, BAD_REQUEST, "Invalid details length") }
    }

    @Test
    fun `ход с коротким details приводит к ошибке`() {
        turn("x_", cat, 1).andExpect { checkError(it, BAD_REQUEST, "Invalid details length") }
    }

    @Test
    fun `ход не своим цветом приводит к ошибке`() {
        turn("0________", cat, 1).andExpect { checkError(it, BAD_REQUEST, "Invalid turn") }
    }

    @Test
    fun `ход на занятое поле приводит к ошибке`() {
        turn("x________", cat, 1)
        turn("0________", kit, 1).andExpect { checkError(it, BAD_REQUEST, "Invalid turn") }
    }

    @Test
    fun `три в ряд приводит к победе крестиков`() {
        turn("x________", cat, 1).andExpect { ok(it) }
        turn("x0_______", kit, 1).andExpect { ok(it) }
        turn("x0_x_____", cat, 1).andExpect { ok(it) }
        turn("x0_x0____", kit, 1).andExpect { ok(it) }
        turn("x0_x0_x__", cat, 1).andExpect {
            ok(it)
            val board: Board = mapper.readValue(it.response.contentAsString)
            assertEquals(WHITE_WON, board.status)
        }
        turn("x0_x0_x0_", kit, 1).andExpect { checkError(it, BAD_REQUEST, "Invalid turn") }
    }

    @Test
    fun `три в ряд приводит к победе ноликов`() {
        turn("x________", cat, 1).andExpect { ok(it) }
        turn("x0_______", kit, 1).andExpect { ok(it) }
        turn("x0_x_____", cat, 1).andExpect { ok(it) }
        turn("x0_x0____", kit, 1).andExpect { ok(it) }
        turn("x0xx0____", cat, 1).andExpect { ok(it) }
        turn("x0xx0__0_", kit, 1).andExpect {
            ok(it)
            val board: Board = mapper.readValue(it.response.contentAsString)
            assertEquals(BLACK_WON, board.status)
        }
        turn("x0xx0_x0_", cat, 1).andExpect { checkError(it, BAD_REQUEST, "Invalid turn") }
    }

    @Test
    fun draw() {
        turn("x________", cat, 1).andExpect { ok(it) }
        turn("x0_______", kit, 1).andExpect { ok(it) }
        turn("x0x______", cat, 1).andExpect { ok(it) }
        turn("x0x_0____", kit, 1).andExpect { ok(it) }
        turn("x0xx0____", cat, 1).andExpect { ok(it) }
        turn("x0xx0_0__", kit, 1).andExpect { ok(it) }
        turn("x0xx0x0__", cat, 1).andExpect { ok(it) }
        turn("x0xx0x0_0", kit, 1).andExpect { ok(it) }
        turn("x0xx0x0x0", cat, 1).andExpect {
            ok(it)
            val board: Board = mapper.readValue(it.response.contentAsString)
            assertEquals(DRAW, board.status)
        }
    }

    private fun ok(result: MvcResult) {
        assertEquals(200, result.response.status)
    }

    private fun checkError(result: MvcResult, status: HttpStatus, message: String) {
        assertEquals(status.value(), result.response.status)
        val body = Response(message)
        assertEquals(mapper.writeValueAsString(body), result.response.contentAsString)
    }

    private fun turn(details: String, user: GUser, id: Int = 1): ResultActions {
        val body = mapper.writeValueAsString(BoardTurn(details))
        val builder = MockMvcRequestBuilders
            .patch("/game/1/room/1/board/$id")
            .header("Authorization", auth(user.name, "cat"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
        return mvc.perform(builder)
    }

    private fun createBoard(user: GUser): ResultActions {
        val body = mapper.writeValueAsString(user)
        val builder = MockMvcRequestBuilders
            .patch("/game/1/room/1/board/any")
            .header("Authorization", auth(user.name, "cat"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
        return mvc.perform(builder)
    }
}