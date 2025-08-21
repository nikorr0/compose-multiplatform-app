package org.company.app.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.company.app.repository.Storage

object HttpClientProvider {
    val client: HttpClient by lazy {
        HttpClient {
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = false
                    }
                )
            }

            // авторизация
            defaultRequest {
                Storage.token?.let { bearerToken ->
                    header("Authorization", "Bearer $bearerToken")
                }
            }
        }
    }
}

