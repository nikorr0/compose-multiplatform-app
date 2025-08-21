package org.company.app.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>
)

@Serializable
data class SingleResponse<T>(
    val data: T
)

@Serializable
data class MessageResponse<T>(
    val message: T
)