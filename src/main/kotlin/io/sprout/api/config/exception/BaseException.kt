package io.sprout.api.config.exception

open class BaseException(exceptionCode: ExceptionCode): RuntimeException(){
    val baseResponseCode: ExceptionCode = exceptionCode
    override val message: String = this.baseResponseCode.message
}