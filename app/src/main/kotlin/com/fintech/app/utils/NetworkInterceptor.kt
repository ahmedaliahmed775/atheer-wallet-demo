package com.fintech.app.utils

import android.content.Context
import okhttp3.*
import java.io.IOException

class NetworkInterceptor(context: Context) : Interceptor {
    // Requirements:
    // 1. No RequestEnvelope wrapping (send flat JSON directly).
    // 2. accessToken in body, not in header (handled in ViewModel/Repository).
    // 3. Flat JSON in the root.

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Simply proceed with the original request to keep the body flat and as-is.
        return chain.proceed(requestBuilder.build())
    }
}
