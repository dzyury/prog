package com.example.demo.error

import com.example.demo.data.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class DefaultAdvice {
    @ExceptionHandler(HttpException::class)
    fun handleException(e: HttpException): ResponseEntity<Response> {
        return ResponseEntity<Response>(Response(e.message), e.status)
    }
}