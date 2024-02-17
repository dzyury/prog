package com.example.demo.controller

import org.junit.jupiter.api.Test
import org.hamcrest.Matchers as HMatchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders as Builders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers as MMatchers

@SpringBootTest
@AutoConfigureMockMvc
class CatControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    val auth = "Basic Y2F0OmNhdA=="
    val request = """{"name":"cat","password":"cat"}"""
    val response = """{"name":"cat","age":2}"""

    @ParameterizedTest
    @CsvSource(value = ["""Kot;4;{"name":"Kot","age":4}""", """Kitten;2;{"name":"Kitten","age":2}"""], delimiter = ';')
    fun testJson(name: String, age: Int, expected: String) {
        val builder = Builders.get("/cat/$name/$age").accept(MediaType.APPLICATION_JSON)
        mvc.perform(builder)
            .andExpect(MMatchers.status().isOk())
            .andExpect(MMatchers.content().string(HMatchers.equalTo(expected)))
    }

    @Test
    fun testAuth() {
        val get = Builders.get("/cat/kit/2")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", auth)
        mvc.perform(get)
            .andExpect(MMatchers.status().isUnauthorized)

        val register = Builders.post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request)
        mvc.perform(register)
            .andExpect(MMatchers.status().isOk)

        mvc.perform(get)
            .andExpect(MMatchers.status().isOk)
            .andExpect(MMatchers.content().string(response))

        mvc.perform(register)
            .andExpect(MMatchers.status().isConflict)
    }
}