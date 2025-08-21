package org.company.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val message: String
)

data class MessageResponseDto(
    val message: String,
    val status: Int
)