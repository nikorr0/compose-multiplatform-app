package org.company.app.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.company.app.Config
import org.company.app.model.UserDto
import java.net.URLDecoder

class AuthRepository(
    private val client: HttpClient
) {
    @Serializable
    private data class LoginRequest(val email: String, val password: String)

    @Serializable
    private data class LoginResponse(val token: String)

    suspend fun login(email: String, password: String): String {
        client.get("${Config.host}/sanctum/csrf-cookie")

        val cookies = client.cookies(Url("${Config.host}/sanctum/csrf-cookie"))

        val xsrf = cookies.firstOrNull { it.name == "XSRF-TOKEN" }
            ?: error("XSRF-TOKEN cookie not found")
        val laravelSession = cookies.firstOrNull { it.name == "laravel_session" }
            ?: error("laravel_session cookie not found")

        val resp = client.post("${Config.host}/login") {
            header("X-XSRF-TOKEN", URLDecoder.decode(xsrf.value))
            header("laravel_session", URLDecoder.decode(laravelSession.value))
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = email, password = password))
        }
        val body = resp.bodyAsText()
        val login = Json.decodeFromString<LoginResponse>(body)
        Storage.token = login.token

        val me: UserDto = client
            .get("${Config.host}/api/user")
            .body()
        Storage.currentUser = me

        return login.token
    }
}