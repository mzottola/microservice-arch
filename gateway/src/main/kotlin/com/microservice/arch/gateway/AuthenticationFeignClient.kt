package com.microservice.arch.gateway

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient("authentication-service")
interface AuthenticationFeignClient {
    @RequestMapping(method = [RequestMethod.GET], value = ["/login"])
    fun login(): String
}
