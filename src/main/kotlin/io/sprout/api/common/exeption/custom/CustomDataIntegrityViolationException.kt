package io.sprout.api.common.exeption.custom

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT) // 409 응답 코드
// 의미: 데이터 무결성 위반 또는 중복된 데이터로 인해 충돌이 발생할 때 사용됩니다.
class CustomDataIntegrityViolationException(
    override val message: String
) : RuntimeException(message)