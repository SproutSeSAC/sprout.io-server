package io.sprout.api.infra.sse.model

import reactor.core.publisher.Sinks

data class SubscriberDto(
        val sink: Sinks.Many<String>,
        var isAlive: Boolean
)
