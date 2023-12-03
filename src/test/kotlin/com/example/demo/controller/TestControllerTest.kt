package com.example.demo.controller

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
class TestControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @ParameterizedTest
    @CsvSource(value = ["""Kot;4;{"name":"Kot","age":4}""", """Kitten;2;{"name":"Kitten","age":2}"""], delimiter = ';')
    fun testJson(name: String, age: Int, expected: String) {
        val builder = Builders.get("/test1?name=$name&age=$age").accept(MediaType.APPLICATION_JSON)
        mvc.perform(builder)
            .andExpect(MMatchers.status().isOk())
            .andExpect(MMatchers.content().string(HMatchers.equalTo(expected)))
    }

    @ParameterizedTest
    @CsvSource(value = ["""Kot;4;<Cat><name>Kot</name><age>4</age></Cat>""", """Kitten;2;<Cat><name>Kitten</name><age>2</age></Cat>"""], delimiter = ';')
    fun testXml(name: String, age: Int, expected: String) {
        val builder = Builders.get("/test1?name=$name&age=$age").accept(MediaType.APPLICATION_XML)
        mvc.perform(builder)
            .andExpect(MMatchers.status().isOk())
            .andExpect(MMatchers.content().string(HMatchers.equalTo(expected)))
    }
}