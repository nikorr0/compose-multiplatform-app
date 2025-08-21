package org.company.app.model

import kotlinx.serialization.Serializable

@Serializable
data class SessionData(
    val xsrfToken: String,
    val laravelSession: String,
    val authToken: String
)