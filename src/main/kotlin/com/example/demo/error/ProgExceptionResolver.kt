package com.example.demo.error

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
import org.springframework.web.servlet.view.json.MappingJackson2JsonView

//@Component
class CustomExceptionResolver : AbstractHandlerExceptionResolver() {
    override fun doResolveException(request: HttpServletRequest, response: HttpServletResponse, handler: Any?, ex: Exception): ModelAndView {
        val mav = ModelAndView(MappingJackson2JsonView())
        if (ex is RuntimeException) {
            mav.status = HttpStatus.BAD_REQUEST
            mav.addObject("message", "Runtime Exception is handled")
            return mav
        }
        mav.status = HttpStatus.INTERNAL_SERVER_ERROR
        mav.addObject("message", "Another exception was handled")
        return mav
    }
}