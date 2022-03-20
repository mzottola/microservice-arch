package com.microservice.arch.gateway

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class GatewayGlobalFilter : GlobalFilter, Ordered {
    override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {
        val request = exchange?.request
        if (request == null) {
            exchange!!.response.statusCode = HttpStatus.UNAUTHORIZED
            return exchange.response.setComplete()
        }
        val path = request.path.toString()

        if (path.startsWith("/auth/login")) {
            return chain!!.filter(exchange)
        }

        if (request.headers.getOrEmpty("X-API-KEY").isNotEmpty()) {
            return WebClient
                .create("http://localhost:8083/")
                .get()
                .uri("/api-key-info")
                .accept(MediaType.APPLICATION_JSON)
                .headers { httpHeaders -> httpHeaders.addAll(request.headers)}
                .exchangeToMono {
                    if (it.statusCode() != HttpStatus.OK) {
                        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                        return@exchangeToMono exchange.response.setComplete()
                    }
                    chain!!.filter(exchange)
                }
        }
        return WebClient
            .create("http://localhost:8083/")
            .get()
            .uri("/validate-token")
            .accept(MediaType.APPLICATION_JSON)
            .headers { httpHeaders -> httpHeaders.addAll(request.headers)}
            .exchangeToMono {
                if (it.statusCode() != HttpStatus.OK) {
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    return@exchangeToMono exchange.response.setComplete()
                }
                chain!!.filter(exchange)
            }
    }

    override fun getOrder(): Int {
        return 0
    }

}

