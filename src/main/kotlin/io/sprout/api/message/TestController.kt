package io.sprout.api.message

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class TestController {
    @GetMapping("/index")
    fun test(): String {
        return "index"
    }

}