package io.sprout.api.notice.model.enum

enum class RequestResult {
    SUCCESS,             // 요청이 성공적으로 처리됨
    ALREADY_PARTICIPATED, // 이미 공지에 참여한 경우
    ALREADY_REQUESTED,    // 이미 요청한 경우
    ERROR                 // 기타 오류 발생 시
}
