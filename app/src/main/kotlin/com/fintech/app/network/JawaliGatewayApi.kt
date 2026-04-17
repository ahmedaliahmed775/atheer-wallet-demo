package com.fintech.app.network

import com.fintech.app.model.*
import retrofit2.http.*

/**
 * واجهة بوابة جوالي — مطابقة ١٠٠٪ للكود المصدري PHP
 * مرجع: JawaliService.php
 *
 * POST /oauth/token     → تسجيل دخول OAuth2 (form-encoded)
 * POST /v1/ws/callWS    → كل العمليات (PAYWA/PAYAG) عبر serviceName
 *
 * accessToken يُرسل كـ Authorization: Bearer header
 */
interface JawaliGatewayApi {

    /**
     * LOGIN TO SYSTEM — مطابق لـ JawaliService::loginToSystem()
     * Http::asForm()->post('/oauth/token', payload)
     *
     * Content-Type: application/x-www-form-urlencoded
     */
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun loginToSystem(
        @Field("grant_type") grantType: String = "password",
        @Field("client_id") clientId: String = "restapp",
        @Field("client_secret") clientSecret: String = "restapp",
        @Field("scope") scope: String = "read",
        @Field("username") username: String,
        @Field("password") password: String
    ): JawaliLoginResponse

    /**
     * Structured API Call — مطابق لـ sendStructuredRequest()
     * Http::asJson()->withToken(accessToken)->post('/v1/ws/callWS', payload)
     *
     * يُرسل accessToken كـ Authorization: Bearer header
     * يُميّز عبر header.serviceDetail.serviceName:
     *   - PAYWA.WALLETAUTHENTICATION
     *   - PAYAG.ECOMMERCEINQUIRY
     *   - PAYAG.ECOMMCASHOUT
     */
    @POST("v1/ws/callWS")
    suspend fun callWS(
        @Header("Authorization") bearerToken: String,
        @Body request: JawaliStructuredRequest
    ): JawaliStructuredResponse
}
