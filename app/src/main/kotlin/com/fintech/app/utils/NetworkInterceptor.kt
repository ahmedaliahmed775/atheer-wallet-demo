package com.fintech.app.utils

import android.content.Context
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONException
import okio.Buffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class NetworkInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // لا نقوم بتغليف طلبات الـ GET
        if (originalRequest.method == "GET" || originalRequest.body == null) {
            return chain.proceed(originalRequest)
        }

        try {
            // 1. استخراج الـ JSON الأصلي من جسم الطلب
            val buffer = Buffer()
            originalRequest.body?.writeTo(buffer)
            val originalBodyStr = buffer.readUtf8()

            // 2. محاولة تحويله إلى كائن JSON
            val originalJson = JSONObject(originalBodyStr)

            // 3. بناء كائن الـ Header الذي يطلبه السيرفر
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val timestamp = dateFormat.format(Date())

            val headerJson = JSONObject().apply {
                put("messageId", UUID.randomUUID().toString())
                put("messageTimestamp", timestamp)
                put("callerId", "ANDROID_APP") // كما يطلب نظام جوالي
            }

            // 4. تجميع الـ Header والـ Body في غلاف واحد (Envelope)
            val envelopeJson = JSONObject().apply {
                put("header", headerJson)
                put("body", originalJson) // الـ body هو البيانات التي أرسلناها من التطبيق
            }

            // 5. إنشاء جسم الطلب الجديد
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val newBody = envelopeJson.toString().toRequestBody(mediaType)

            // 6. استبدال الطلب القديم بالطلب المغلف
            val newRequest = originalRequest.newBuilder()
                .method(originalRequest.method, newBody)
                .build()

            return chain.proceed(newRequest)

        } catch (e: JSONException) {
            // في حال فشل التحويل (مثلاً إذا كان الطلب ليس JSON)، نمرره كما هو
            return chain.proceed(originalRequest)
        }
    }
}