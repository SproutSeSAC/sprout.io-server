package io.sprout.api.test

import org.springframework.stereotype.Service

@Service
class OriginTestService {
    fun test(): String {
        return "test-response";
    }
}