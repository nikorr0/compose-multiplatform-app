package org.company.app.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    val id: Int?,
    val name: String,
    @SerialName("short_text")
    val shortText: String,
    @SerialName("long_text")
    val longText: String,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("author")
    val author: UserDto?,
    @SerialName("comments")
    val comments: List<CommentDto> = emptyList(),
)

