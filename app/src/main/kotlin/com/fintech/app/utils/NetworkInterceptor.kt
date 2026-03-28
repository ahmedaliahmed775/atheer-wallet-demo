package com.fintech.app.utils

import android.content.Context
import com.fintech.app.model.RequestEnvelope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.io.IOException

class NetworkInterceptor(context: Context) : Interceptor {
    private val securityManager = SecurityManager(context)
    private val json = Json { ignoreUnknownKeys = true }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // 1. Inject Authorization Header if token exists
        securityManager.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // 2. Wrap POST/PUT request body in Envelope
        val method = originalRequest.method
        if ((method == "POST" || method == "PUT") && originalRequest.body != null) {
            val originalBodyString = requestBodyToString(originalRequest.body)
            
            try {
                // Parse the original body as a JSON element
                val bodyJson = json.parseToJsonElement(originalBodyString)
                
                // Wrap it in RequestEnvelope
                val envelope = RequestEnvelope(body = bodyJson)
                val wrappedBodyString = json.encodeToString(envelope)
                
                val newBody = wrappedBodyString.toRequestBody("application/json".toMediaType())
                requestBuilder.method(method, newBody)
            } catch (e: Exception) {
                // If parsing fails, proceed with original body (fallback)
                e.printStackTrace()
            }
        }

        return chain.proceed(requestBuilder.build())
    }

    private fun requestBodyToString(requestBody: RequestBody?): String {
        if (requestBody == null) return ""
        val buffer = Buffer()
        requestBody.writeTo(buffer)
        return buffer.readUtf8()
    }
}
