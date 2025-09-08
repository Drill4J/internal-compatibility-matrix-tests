package com.epam.drill.compatibility.apps

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SpringMvcController {
    @PostMapping(path = ["/"], consumes = ["text/plain"], produces = ["text/plain"])
    @ResponseBody
    fun handleRequest(@RequestBody body: String): String {
        return body
    }
}