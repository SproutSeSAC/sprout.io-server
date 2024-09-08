package io.sprout.api.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

object CookieUtils {

    fun createCookie(name: String, value: String, httpOnly: Boolean = true, path: String = "/"): Cookie {
        return Cookie(name, value).apply {
            isHttpOnly = httpOnly
            this.path = path
        }
    }

    fun addCookie(response: HttpServletResponse, name: String, value: String, maxAge: Int) {
        val cookie = Cookie(name, value)
        cookie.path = "/"
        cookie.isHttpOnly = true
        cookie.maxAge = maxAge
        cookie.secure = false
        response.addCookie(cookie)
    }

    fun getCookie(request: HttpServletRequest, name: String): Cookie? {
        return request.cookies.find { it.name.equals(name) }
    }

}