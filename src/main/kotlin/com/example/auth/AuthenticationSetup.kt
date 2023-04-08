package com.example.auth

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*

fun Application.setupAuthentication() {
    val applicationHttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
        json()
    }
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
    install(Authentication) {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = "265635337362-atqf6ivf1oh8si3s3cjubdfkjcshm4qh.apps.googleusercontent.com",
                    clientSecret = "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",
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                )
            }
            client = applicationHttpClient
        }
    }
    configureAuthRoutes(applicationHttpClient)

}