package com.fintech.app.utils

import com.fintech.app.model.RequestHeader
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID

class NetworkInterceptor : Interceptor {
    private val json = Json { ignoreUnknownKeys = true }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalBody = originalRequest.body

        if (originalBody == null || originalRequest.method == "GET") {
            return chain.proceed(originalRequest)
        }

        // Buffer the original body
        val buffer = okio.Buffer()
        originalBody.writeTo(buffer)
        val originalBodyString = buffer.readUtf8()

        // Create the structured request
        val header = RequestHeader(
            messageId = UUID.randomUUID().toString(),
            messageTimestamp = Instant.now().toString(),
            callerId = Constants.CALLER_ID
        )

        val originalBodyJson = try {
            json.parseToJsonElement(originalBodyString)
        } catch (e: Exception) {
            JsonNull
        }

        val wrappedRequestJson = buildJsonObject {
            put("header", json.encodeToJsonElement(header))
            put("body", originalBodyJson)
        }

        val newBodyString = json.encodeToString(wrappedRequestJson)
        val newBody = newBodyString.toRequestBody("application/json".toMediaType())

        val newRequest = originalRequest.newBuilder()
            .post(newBody)
            .build()

        return chain.proceed(newRequest)
    }
}
