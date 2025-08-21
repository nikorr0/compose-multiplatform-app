package org.company.app.session

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.company.app.model.SessionData

class SessionManager(
    private val storage: SessionStorage,
    private val httpClient: HttpClient
) {
    // восстановление сессии
    suspend fun initSession(): Boolean {
        val s = storage.loadSession() ?: return false
        return try {
            val resp: HttpResponse = httpClient.get("/api/user") {
                header("Cookie", "XSRF-TOKEN=${s.xsrfToken}; laravel_session=${s.laravelSession}")
                header("Authorization", "Bearer ${s.authToken}")
            }
            if (resp.status.value in 200..299) true
            else { storage.clearSession(); false }
        } catch (e: Exception) {
            storage.clearSession(); false
        }
    }

    fun saveSession(session: SessionData) {
        storage.saveSession(session)
    }
}
