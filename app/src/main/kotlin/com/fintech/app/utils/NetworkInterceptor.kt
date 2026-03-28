package com.fintech.app.utils

import okhttp3.Interceptor
import okhttp3.Response

class NetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Remove any wrapping logic. 
        // We just proceed with the original request which should be a Flat JSON.
        // We can add global headers here if needed (like Content-Type which is usually handled by Retrofit)
        
        val newRequest = originalRequest.newBuilder()
            .header("Accept", "application/json")
            .build()

        return chain.proceed(newRequest)
    }
}
