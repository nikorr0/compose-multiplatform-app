package org.company.app.session

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.company.app.model.SessionData

class SessionStorage(private val fileOperator: FileOperator) {
    fun saveSession(session: SessionData) {
        fileOperator.writeText(SESSION_FILE_NAME, Json.encodeToString(session))
    }

    fun loadSession(): SessionData? {
        val data = fileOperator.readText(SESSION_FILE_NAME) ?: return null
        return try {
            Json.decodeFromString<SessionData>(data)
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        fileOperator.writeText(SESSION_FILE_NAME, "")
    }
}
