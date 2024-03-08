package com.example.demo.error

import org.springframework.http.HttpStatus

class HttpException(val status: HttpStatus, override val message: String) : Exception()