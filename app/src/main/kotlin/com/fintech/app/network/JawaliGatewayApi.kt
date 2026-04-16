package com.fintech.app.network

import com.fintech.app.model.*
import retrofit2.http.*

/**
 * واجهة بوابة جوالي — مطابقة ١٠٠٪ للمسارات الحقيقية
 *
 * POST /paygate/login  → تسجيل الدخول
 * POST /paygate/PAYWA  → مصادقة المحفظة
 * POST /paygate/PAYAG  → استعلام / صرف
 */
interface JawaliGatewayApi {

    @POST("paygate/login")
    suspend fun login(@Body request: JawaliLoginRequest): JawaliLoginResponse

    @POST("paygate/PAYWA")
    suspend fun walletAuthentication(@Body request: JawaliWalletAuthRequest): JawaliWalletAuthResponse

    @POST("paygate/PAYAG")
    suspend fun payag(@Body request: JawaliPayagRequest): JawaliPayagResponse
}
