package org.company.app.repository

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.company.app.Config
import org.company.app.model.SingleResponse
import org.company.app.model.UserDto

class UserRepository(
    private val client: HttpClient
) {
    suspend fun get(id: Int): UserDto {
        val wrapper: SingleResponse<UserDto> =
            client.get("${Config.host}/api/users/${id}").body()
        return wrapper.data
    }
}
