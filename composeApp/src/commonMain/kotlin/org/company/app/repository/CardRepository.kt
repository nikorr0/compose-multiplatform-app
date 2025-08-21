package org.company.app.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.company.app.Config
import org.company.app.model.CardDto
import org.company.app.model.MessageDto
import org.company.app.model.MessageResponseDto
import org.company.app.model.PaginatedResponse
import org.company.app.model.SingleResponse
import org.company.app.network.HttpClientProvider

class CardRepository(private val client: HttpClient = HttpClientProvider.client) {
    private val json = Json { ignoreUnknownKeys = true }

    /** GET /api/cards */
    suspend fun list(): List<CardDto> {
        val raw = client
            .get("${Config.host}/api/cards")
            .bodyAsText()

        return try {
            json.decodeFromString(
                PaginatedResponse.serializer(CardDto.serializer()),
                raw
            ).data
        } catch (_: SerializationException) {
            json.decodeFromString(
                ListSerializer(CardDto.serializer()),
                raw
            )
        }
    }
    /** GET /api/cards/{id} */
    suspend fun get(id: Int?): CardDto {
        val wrapper: SingleResponse<CardDto> =
            client.get("${Config.host}/api/cards/$id")
                .body()
        return wrapper.data
    }

    /** DELETE /api/cards/{id} */
    suspend fun delete(id: Int): MessageResponseDto {
        val res: HttpResponse = client.delete("${Config.host}/api/cards/$id")
        val data: MessageDto = res.body()
        println("DELETE RESULT:")
        println(data.message)
        println(res.status.value)

        return MessageResponseDto(
            message = data.message,
            status = res.status.value
        )
    }

    /** POST /api/cards/{id} */
    suspend fun create(card: CardDto, bytes: ByteArray?) {
        client.submitFormWithBinaryData(
            url = "${Config.host}/api/cards",
            formData = buildFormData(card, bytes)
        )
    }

    /** PATCH /api/cards/{id} */
    // Не работает
    suspend fun update(id: Int, card: CardDto, newBytes: ByteArray?) {
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = "${Config.host}/api/cards/${id}",
            formData = formData {
                append("name", card.name)
                append("short_text", card.shortText)
                append("long_text", card.longText)
                append("image_url", card.imageUrl)
            },
            { method = HttpMethod.Patch }
        )
        println(response.status)
        println(response.body<MessageDto>().toString())
    }
    private fun buildFormData(card: CardDto, bytes: ByteArray?) = formData {
        append("name", card.name)
        append("short_text", card.shortText)
        append("long_text", card.longText)
        bytes?.let { append("image", it, Headers.build {
            append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
        }) }
    }
}