package io.sprout.api.common.exeption.custom

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 응답 코드
class CustomBadRequestException(
    override val message: String
) : RuntimeException(message)