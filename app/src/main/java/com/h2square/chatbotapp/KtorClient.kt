package com.h2square.chatbotapp

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {

    const val BASE_URL = "https://wsapi.simsimi.com/190410/talk"
    private const val API_KEY = "k5uq4.ioax8rqfDapZ2u1NBqgBGUqoa2Mz9onvyu"
    val httpClient = HttpClient(CIO) {

        //json cof
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                ignoreUnknownKeys = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }

        //log
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
//        -H "Content-Type: application/json" \
//        -H "x-api-key: PASTE_YOUR_PROJECT_KEY_HERE" \
        defaultRequest {
            headers.append("Content-Type", "application/json")
            headers.append("x-api-key", API_KEY)
        }
    }

}