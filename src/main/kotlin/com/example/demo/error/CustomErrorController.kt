package com.example.demo.error

import com.example.demo.data.ErrorData
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


//@RestController
class CustomErrorController : ErrorController {
    @RequestMapping("/error")
    fun error(request: HttpServletRequest, response: HttpServletResponse): ErrorData {
        return ErrorData(412, "Error occurred")
    }
}