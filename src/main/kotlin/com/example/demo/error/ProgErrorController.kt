package com.example.demo.error

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

//@Controller
class ProgErrorController(
    val attributes: ErrorAttributes,
    val properties: ServerProperties
) : BasicErrorController(attributes, properties.error) {
    override fun error(request: HttpServletRequest ): ResponseEntity<Map<String, Any>> {
        val status = this.getStatus(request)
        if (status == HttpStatus.NO_CONTENT) {
            return ResponseEntity(mapOf("message" to "No content"), status)
        } else {
//            val body = mapOf("message" to "Error")
            val body = super.error(request).body
            return ResponseEntity(body, HttpStatus.OK)
        }
    }
}