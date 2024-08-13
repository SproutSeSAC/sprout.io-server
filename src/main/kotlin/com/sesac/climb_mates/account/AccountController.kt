package com.sesac.climb_mates.account

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class AccountController(
    private val accountService: AccountService
) {
    @GetMapping("/account")
    fun createAccount(model:Model): String {
        model.addAttribute("head_title", "회원가입 약관")
        return "account/account_seq/account_1"
    }

    @GetMapping("/account/main")
    fun createAccountMain(model: Model): String {
        model.addAttribute("head_title", "회원가입")
        return "account/account"
    }

    @GetMapping("/account/{num}")
    fun createAccountSeq(@PathVariable("num") num:Int, model: Model): String {
        model.addAttribute("head_title", "회원가입 $num")
        return "account/account_seq/account_${num}"
    }
}


