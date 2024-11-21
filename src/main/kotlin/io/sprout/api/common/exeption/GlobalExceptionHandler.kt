package io.sprout.api.common.exeption

import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.common.exeption.custom.CustomSystemException
import io.sprout.api.common.exeption.custom.CustomUnexpectedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(CustomBadRequestException::class)
    fun handleBadRequestException(ex: CustomBadRequestException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(CustomDataIntegrityViolationException::class)
    fun handleDataIntegrityException(ex: CustomDataIntegrityViolationException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(CustomSystemException::class)
    fun handleSystemException(ex: CustomSystemException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(CustomUnexpectedException::class)
    fun handleUnexpectedException(ex: CustomUnexpectedException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(HttpClientErrorException::class)
    fun handleHttpClientErrorException(ex: HttpClientErrorException): ResponseEntity<String> {
        return ResponseEntity(ex.message, ex.statusCode)
    }
}