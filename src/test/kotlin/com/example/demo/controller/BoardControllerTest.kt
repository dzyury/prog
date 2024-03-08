@file:Suppress("NonAsciiCharacters")

package com.example.demo.controller

import com.example.demo.data.Board
import com.example.demo.data.Board.Companion.INIT_DETAILS
import com.example.demo.data.BoardStatus.PLAYING
import com.example.demo.data.BoardStatus.WAITING
import com.example.demo.data.Position.BLACK
import com.example.demo.data.Position.WHITE
import com.example.demo.data.Role.PLAYER
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
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import java.util.*
import com.example.demo.data.User as GUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders as Builders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers as MMatchers

@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var authService: MapUserDetailsService

    @Autowired
    private lateinit var boardService: BoardService

    fun auth(user: String, password: String): String {
        val code = Base64.getEncoder().encodeToString("$user:$password".toByteArray())
        return "Basic $code"
    }

    @BeforeEach
    fun start() {
        for (name in listOf("kit", "cat")) {
            val user = User(name, "cat")
            val content = mapper.writeValueAsString(user)
            val register = Builders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
            mvc.perform(register)
                .andExpect(MMatchers.status().isOk)
        }
    }

    @AfterEach
    fun clear() {
        authService.clear()
        boardService.boards.clear()
    }

    @Test
    fun `попытка получить несуществующую доску приводит к ошибке`() {
        val user = GUser("cat", PLAYER, WHITE)
        getBoard(5, user).andExpect {
            assertEquals(404, it.response.status)
            assertEquals("{\"message\":\"Board not found: 5\"}", it.response.contentAsString)
        }
    }

    @Test
    fun `второй запрос с таким же юзером возвращает ту же доску`() {
        val user = GUser("cat", PLAYER, WHITE)
        val checker: (MvcResult) -> Unit = {
            assertEquals(200, it.response.status)
            val expectedBoard = Board(1, WHITE, WAITING, INIT_DETAILS, listOf(user))
            val board: Board = mapper.readValue(it.response.contentAsString)
            assertEquals(expectedBoard, board)
        }

        createBoard(user).andExpect(checker)
        createBoard(user).andExpect(checker)
    }

    @Test
    fun `второй запрос с другим юзером начинает игру`() {
        val user1 = GUser("cat", PLAYER, WHITE)
        val expectedBoard1 = Board(1, WHITE, WAITING, INIT_DETAILS, listOf(user1))
        val user2 = GUser("kit", PLAYER, BLACK)
        val expectedBoard2 = Board(1, WHITE, PLAYING, INIT_DETAILS, listOf(user1, user2))
        fun checker(expectedBoard: Board): (MvcResult) -> Unit = {
            assertEquals(200, it.response.status)
            val board: Board = mapper.readValue(it.response.contentAsString)
            assertEquals(expectedBoard, board)
        }

        createBoard(user1).andExpect(checker(expectedBoard1))
        createBoard(user2).andExpect(checker(expectedBoard2))
    }

    @Test
    fun `второй запрос с другим юзером такого же цвета меняет цвет у второго юзера и начинает игру`() {
        val user1 = GUser("cat", PLAYER, WHITE)
        val expectedBoard1 = Board(1, WHITE, WAITING, INIT_DETAILS, listOf(user1))
        val user2 = GUser("kit", PLAYER, WHITE)
        val expectedBoard2 = Board(1, WHITE, PLAYING, INIT_DETAILS, listOf(user1, user2.copy(position = BLACK)))
        fun checker(expectedBoard: Board): (MvcResult) -> Unit = {
            assertEquals(200, it.response.status)
            val board: Board = mapper.readValue(it.response.contentAsString)
            assertEquals(expectedBoard, board)
        }

        createBoard(user1).andExpect(checker(expectedBoard1))
        createBoard(user2).andExpect(checker(expectedBoard2))
    }

    @Test
    fun `четыре запроса создают две игры`() {
        val user1 = GUser("cat", PLAYER, WHITE)
        val expectedBoard1 = Board(1, WHITE, WAITING, INIT_DETAILS, listOf(user1))
        val user2 = GUser("kit", PLAYER, WHITE)
        val expectedBoard2 = Board(1, WHITE, PLAYING, INIT_DETAILS, listOf(user1, user2.copy(position = BLACK)))
        fun checker(expectedBoard: Board): (MvcResult) -> Unit = {
            assertEquals(200, it.response.status)
            val board: Board = mapper.readValue(it.response.contentAsString)
            assertEquals(expectedBoard, board)
        }

        createBoard(user1).andExpect(checker(expectedBoard1))
        getBoard(1, user1).andExpect(checker(expectedBoard1))
        createBoard(user2).andExpect(checker(expectedBoard2))
        getBoard(1, user2).andExpect(checker(expectedBoard2))
        createBoard(user1).andExpect(checker(expectedBoard1.copy(id = 2)))
        getBoard(2, user2).andExpect(checker(expectedBoard1.copy(id = 2)))
        createBoard(user2).andExpect(checker(expectedBoard2.copy(id = 2)))
        getBoard(2, user1).andExpect(checker(expectedBoard2.copy(id = 2)))
    }

    private fun getBoard(id: Int, user: GUser): ResultActions {
        val builder = Builders
            .get("/game/1/room/1/board/$id")
            .header("Authorization", auth(user.name, "cat"))
            .contentType(MediaType.APPLICATION_JSON)
        return mvc.perform(builder)
    }

    private fun createBoard(user: GUser): ResultActions {
        val body = mapper.writeValueAsString(user)
        val builder = Builders
            .patch("/game/1/room/1/board/any")
            .header("Authorization", auth(user.name, "cat"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
        return mvc.perform(builder)
    }
}