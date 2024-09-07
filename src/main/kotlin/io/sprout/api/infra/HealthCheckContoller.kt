package io.sprout.api.infra

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {

    @GetMapping("/health-check")
    fun healthCheck() = "OK"
}