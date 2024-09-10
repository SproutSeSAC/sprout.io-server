package io.sprout.api.config.exception

import org.springframework.http.HttpStatus

enum class ExceptionCode(httpStatusCode: HttpStatus, message: String) {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생하였습니다."), //500

    CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "등록에 실패하였습니다."),
    DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "삭제에 실패하였습니다."),
    UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "수정에 실패하였습니다."),

    NOT_FOUND_CONTENTS(HttpStatus.NO_CONTENT, "데이터가 존재하지 않습니다."), // 204
    NOT_FOUND_CAMPUS(HttpStatus.NO_CONTENT, "존재하지 않는 캠퍼스입니다."),
    NOT_FOUND_COURSE(HttpStatus.NO_CONTENT, "존재하지 않는 코스입니다."),
    NOT_FOUND_MEMBER(HttpStatus.NO_CONTENT, "존재하지 않는 회원입니다."),

    ALREADY_REGISTERED_USER(HttpStatus.CONFLICT, "이미 등록된 회원입니다.");

    val httpStatus: HttpStatus = httpStatusCode
    val message: String = message
}