package org.company.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto (
    val id: Int,
    @SerialName("content")
    val content: String,
    @SerialName("author")
    val author: UserDto,
    @SerialName("createdAt")
    val createdAt: String
)
