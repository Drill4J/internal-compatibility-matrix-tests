package com.epam.drill.compatibility.apps

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Controller
class SpringWebfluxController {
    @PostMapping("/")
    @ResponseBody
    fun getMono(@RequestBody body: String): Mono<String> {
        return Mono.just(body)
            .subscribeOn(Schedulers.single())
            .map { it }
    }
}