package io.sprout.api.common.exeption.custom

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 응답 코드
class CustomSystemException(
    override val message: String
) : RuntimeException(message)