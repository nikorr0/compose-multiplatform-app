package org.company.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    @SerialName("is_admin")
    val isAdmin: Boolean? = false
)