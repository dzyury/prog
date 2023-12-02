package com.example.demo.error

import com.example.demo.data.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.io.IOException


//@ControllerAdvice
class DefaultAdvice {
    @ExceptionHandler(IOException::class)
    fun handleException(e: IOException): ResponseEntity<Response> {
        val response = Response(e.message ?: "No message")
        return ResponseEntity<Response>(response, HttpStatus.OK)
    }
}