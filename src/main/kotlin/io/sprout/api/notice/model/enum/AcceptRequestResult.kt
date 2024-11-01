package io.sprout.api.notice.model.enum

enum class AcceptRequestResult {
    SUCCESS,
    REQUEST_NOT_FOUND,  // 요청이 이미 취소된 경우
    VERSION_CONFLICT,    // 버전 충돌 발생 시
    CAPACITY_EXCEEDED, // 정원 초과
}