package com.example.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureAuthRoutes(httpClient: HttpClient) {
    routing {
        authenticate("auth-oauth-google") {
            get("/login") {}

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                call.sessions.set(UserSession(principal!!.state!!, principal.accessToken))
                call.respondRedirect("/user-infos")
            }
        }
        get("/") {
            call.respondRedirect("/login")
        }
        get("/user-infos ") {
            val userSession: UserSession? = call.sessions.get()
            if (userSession != null) {
                val userInfo = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer ${userSession.token}")
                    }
                }.body<UserInfo>()

                call.respond(userInfo)
            } else {
                call.respondText("null")
            }
        }
    }
}