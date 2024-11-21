package io.sprout.api.store.service

import io.sprout.api.config.properties.NaverMapPropertiesConfig
import io.sprout.api.store.model.dto.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder


@Service
class NaverMapDirectionService(
    private val mapPropertiesConfig: NaverMapPropertiesConfig,
) : MapDirectionService
 {
    override fun findDirection(directionRequest: StoreDto.MapDirectionRequest): DirectionResponse {
        val restTemplate: RestTemplate =  RestTemplate()

        val requestUrl: String = UriComponentsBuilder.fromHttpUrl(mapPropertiesConfig.directionUrl)
            .queryParam("start", directionRequest.start)
            .queryParam("goal", directionRequest.goal)
            .queryParam("option", "trafast")
            .toUriString()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.add("X-NCP-APIGW-API-KEY-ID", mapPropertiesConfig.apiKeyId)
        headers.add("X-NCP-APIGW-API-KEY", mapPropertiesConfig.apiKey)
        val request = HttpEntity<Any>(headers)

        val routeApiResponse: RouteResponse = restTemplate.exchange(
            requestUrl,
            HttpMethod.GET,
            request,
            RouteResponse::class.java,
        ).body!!


        val directionResponse = DirectionResponse(routeApiResponse.code)
        if (routeApiResponse.code == 0) {
            directionResponse.path = routeApiResponse.route?.trafast?.get(0)?.path
        }

        return directionResponse
    }
}