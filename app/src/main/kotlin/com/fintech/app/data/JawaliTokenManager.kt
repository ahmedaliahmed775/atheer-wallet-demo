package com.fintech.app.data

import com.fintech.app.model.*
import com.fintech.app.network.JawaliGatewayApi
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * مدير توكنات جوالي — مطابق لـ @alsharie/jawalijs + JawaliService.php
 * مرجع: https://www.npmjs.com/package/@alsharie/jawalijs
 * مرجع: https://github.com/Alsharie/jawali-payment
 *
 * يدير:
 * - accessToken (من loginToSystem — OAuth2)
 * - walletToken (من walletAuthentication — PAYWA.WALLETAUTHENTICATION)
 * - بناء الـ Structured Payload (header + body) — مطابق لـ buildStructuredRequestPayload()
 * - إعادة المحاولة — مطابق لـ sendStructuredRequest() retry logic
 * - التدفق الرباعي:  login → walletAuth → inquiry → cashout
 *
 * ═══════════════════════════════════════════════════════════════
 * التعديلات المطبقة للتطابق مع @alsharie/jawalijs:
 *
 * 1. processFullPayment: التحقق من cashoutData.status بدلاً من cashoutData.state
 *    (استجابة الصرف تستخدم "status" وليس "state" — مطابق لـ PHP SDK + JS SDK)
 *
 * 2. walletAuthentication: قراءة accessToken الذي يُخزّن كـ @SerializedName("access_token")
 *    (Gson يربط JSON key "access_token" بالـ Kotlin property "accessToken" تلقائياً)
 * ═══════════════════════════════════════════════════════════════
 */
@Singleton
class JawaliTokenManager @Inject constructor(
    private val api: JawaliGatewayApi
) {
    // ─── Tokens — مطابق لـ TokenManager.php ──────────────────
    private var accessToken: String? = null
    private var walletToken: String? = null
    private var accessTokenExpiresAt: Long = 0
    private var walletTokenExpiresAt: Long = 0

    // ─── بيانات الوكيل — مطابق لـ config/jawali.php ──────────
    private val merchantUsername = "atheer_merchant"
    private val merchantPassword = "atheer_pass_123"
    private val walletIdentifier = "777000001"       // auth.wallet_identifier
    private val walletPassword = "wallet_pass_123"   // auth.wallet_password
    private val orgId = "atheer-org-001"              // auth.org_id
    private val userId = "atheer.api.user"            // auth.user_id
    private val externalUser = "atheer_ext_1"         // auth.external_user

    // ─── Constants — مطابق لـ JawaliService.php constants ────
    private companion object {
        const val CLIENT_ID = "WeCash"                    // COMMON_SIGNON_CLIENT_ID
        const val BODY_TYPE = "Clear"                     // COMMON_BODY_TYPE
        const val WALLET_AUTH_SERVICE = "PAYWA.WALLETAUTHENTICATION"
        const val WALLET_AUTH_DOMAIN = "WalletDomain"
        const val INQUIRY_SERVICE = "PAYAG.ECOMMERCEINQUIRY"
        const val INQUIRY_DOMAIN = "MerchantDomain"
        const val CASHOUT_SERVICE = "PAYAG.ECOMMCASHOUT"
        const val CASHOUT_DOMAIN = "MerchantDomain"
        // retry — مطابق لـ config retry.max_attempts
        const val MAX_RETRY = 2
    }

    // ─── Token Checks — مطابق لـ TokenManager::hasAuthToken() ──

    private fun hasAuthToken(): Boolean =
        accessToken != null && System.currentTimeMillis() < accessTokenExpiresAt

    private fun hasWalletToken(): Boolean =
        walletToken != null && System.currentTimeMillis() < walletTokenExpiresAt

    // ─── Payload Builders — مطابق لـ buildStructuredRequestPayload() ──

    private fun buildSignonDetail(): JawaliSignonDetail = JawaliSignonDetail(
        clientID = CLIENT_ID,
        orgID = orgId,
        userID = userId,
        externalUser = externalUser
    )

    private fun buildHeader(serviceName: String, domainName: String): JawaliRequestHeader {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
        return JawaliRequestHeader(
            serviceDetail = JawaliServiceDetail(
                corrID = UUID.randomUUID().toString(),
                domainName = domainName,
                serviceName = serviceName
            ),
            signonDetail = buildSignonDetail(),
            messageContext = JawaliMessageContext(
                clientDate = dateFormat.format(Date()),
                bodyType = BODY_TYPE
            )
        )
    }

    private fun generateRefId(): String = System.currentTimeMillis().toString()

    private fun bearerHeader(): String = "Bearer ${accessToken ?: ""}"

    // ═══════════════════════════════════════════════════════════
    // ① loginToSystem — مطابق لـ JawaliService::loginToSystem()
    // Http::asForm()->post(baseUrl . '/oauth/token', payload)
    // ═══════════════════════════════════════════════════════════

    suspend fun loginToSystem(): Result<String> = runCatching {
        val response = api.loginToSystem(
            username = merchantUsername,
            password = merchantPassword
        )

        if (!response.isSuccess()) {
            throw Exception(response.errorDescription ?: response.error ?: "فشل تسجيل الدخول")
        }

        accessToken = response.accessToken
        val expiresIn = response.expiresIn ?: 3600
        accessTokenExpiresAt = System.currentTimeMillis() + (expiresIn * 1000L)

        response.accessToken!!
    }

    // ═══════════════════════════════════════════════════════════
    // ② walletAuthentication — مطابق لـ JawaliService::walletAuthentication()
    // Http::asJson()->withToken(accessToken)->post(baseUrl . '/v1/ws/callWS', payload)
    //
    // ★ Gson يربط JSON key "access_token" بالـ Kotlin property "accessToken"
    //   عبر @SerializedName("access_token") في JawaliResponseBody
    //   مطابق لـ JS SDK: this.authHelper.setWalletToken(responseJson.responseBody.access_token)
    // ═══════════════════════════════════════════════════════════

    suspend fun walletAuthentication(): Result<String> = runCatching {
        if (!hasAuthToken()) loginToSystem().getOrThrow()

        val request = JawaliStructuredRequest(
            header = buildHeader(WALLET_AUTH_SERVICE, WALLET_AUTH_DOMAIN),
            body = mapOf(
                "identifier" to walletIdentifier,
                "password" to walletPassword
            )
        )

        val response = api.callWS(bearerHeader(), request)

        if (!response.isSuccess()) {
            throw Exception(response.getErrorMessage() ?: "فشل مصادقة المحفظة")
        }

        // ★ accessToken هنا يقرأ JSON key "access_token" عبر @SerializedName
        val token = response.responseBody?.accessToken
            ?: throw Exception("لم يُرجع walletToken")

        walletToken = token
        // expiresIn يُستخدم في السيرفر المحاكي فقط — الافتراضي 1800 ثانية
        val expiresIn = response.responseBody?.expiresIn ?: 1800
        walletTokenExpiresAt = System.currentTimeMillis() + (expiresIn * 1000L)

        token
    }

    // ═══════════════════════════════════════════════════════════
    // ③ ecommerceInquiry — مطابق لـ JawaliService::ecommerceInquiry()
    // ═══════════════════════════════════════════════════════════

    suspend fun ecommerceInquiry(
        voucher: String,
        receiverMobile: String,
        purpose: String
    ): Result<JawaliResponseBody> = executeWithRetry(INQUIRY_SERVICE, INQUIRY_DOMAIN) {
        val request = JawaliStructuredRequest(
            header = buildHeader(INQUIRY_SERVICE, INQUIRY_DOMAIN),
            body = mapOf(
                "agentWallet" to walletIdentifier,
                "voucher" to voucher,
                "receiverMobile" to receiverMobile,
                "password" to walletPassword,
                "accessToken" to (walletToken ?: ""),
                "refId" to generateRefId(),
                "purpose" to purpose
            )
        )

        val response = api.callWS(bearerHeader(), request)

        if (!response.isSuccess()) {
            throw JawaliApiException(
                message = response.getErrorMessage() ?: "فشل الاستعلام",
                error = response.responseStatus?.errorCode,
                isRetryable = isTokenError(response)
            )
        }

        response.responseBody ?: throw Exception("responseBody is null")
    }

    // ═══════════════════════════════════════════════════════════
    // ④ ecommerceCashout — مطابق لـ JawaliService::ecommerceCashout()
    //
    // ★ استجابة الصرف تستخدم حقولاً مختلفة عن الاستعلام:
    //   - status بدلاً من state
    //   - amount بدلاً من txnamount
    //   - balance (إضافة)
    //   - refId (إضافة)
    //   - IssuerRef بدلاً من issuerTrxRef
    //   - Currency بدلاً من txncurrency
    // ═══════════════════════════════════════════════════════════

    suspend fun ecommerceCashout(
        voucher: String,
        receiverMobile: String,
        purpose: String
    ): Result<JawaliResponseBody> = executeWithRetry(CASHOUT_SERVICE, CASHOUT_DOMAIN) {
        val request = JawaliStructuredRequest(
            header = buildHeader(CASHOUT_SERVICE, CASHOUT_DOMAIN),
            body = mapOf(
                "agentWallet" to walletIdentifier,
                "voucher" to voucher,
                "receiverMobile" to receiverMobile,
                "password" to walletPassword,
                "accessToken" to (walletToken ?: ""),
                "refId" to generateRefId(),
                "purpose" to purpose
            )
        )

        val response = api.callWS(bearerHeader(), request)

        if (!response.isSuccess()) {
            throw JawaliApiException(
                message = response.getErrorMessage() ?: "فشل الصرف",
                error = response.responseStatus?.errorCode,
                isRetryable = isTokenError(response)
            )
        }

        response.responseBody ?: throw Exception("responseBody is null")
    }

    // ═══════════════════════════════════════════════════════════
    // processFullPayment — مطابق لـ processPayment() في الوثائق
    //
    // ★ تعديل: التحقق من cashoutData.status بدلاً من cashoutData.state
    //   استجابة الصرف تستخدم "status" (SUCCESS/FAILED) وليس "state"
    //   مطابق لـ PHP SDK: getStatue() → responseBody.status
    //   مطابق لـ JS SDK: ecommcaShout → responseBody.status
    // ═══════════════════════════════════════════════════════════

    suspend fun processFullPayment(
        voucher: String,
        receiverMobile: String,
        purpose: String,
        expectedAmount: String? = null,
        expectedCurrency: String? = null
    ): Result<JawaliResponseBody> = runCatching {
        // ① login
        loginToSystem().getOrThrow()
        // ② walletAuth
        walletAuthentication().getOrThrow()
        // ③ inquiry
        val inquiryData = ecommerceInquiry(voucher, receiverMobile, purpose).getOrThrow()

        check(inquiryData.state == "PENDING") {
            "Transaction not PENDING: ${inquiryData.state}"
        }
        if (expectedAmount != null) {
            check(inquiryData.txnamount == expectedAmount) {
                "Amount mismatch: expected=$expectedAmount, actual=${inquiryData.txnamount}"
            }
        }
        if (expectedCurrency != null) {
            check(inquiryData.txncurrency == expectedCurrency) {
                "Currency mismatch: expected=$expectedCurrency, actual=${inquiryData.txncurrency}"
            }
        }

        // ④ cashout
        val cashoutData = ecommerceCashout(voucher, receiverMobile, purpose).getOrThrow()

        // ★ تعديل: التحقق من status بدلاً من state
        // استجابة الصرف تستخدم حقل "status" (وليس "state")
        check(cashoutData.status == "SUCCESS") { "Cashout failed: ${cashoutData.status}" }

        cashoutData
    }

    // ─── Retry — مطابق لـ sendStructuredRequest() logic ─────

    private suspend fun executeWithRetry(
        serviceName: String,
        domainName: String,
        block: suspend () -> JawaliResponseBody
    ): Result<JawaliResponseBody> {
        var lastException: Exception? = null

        for (attempt in 0 until MAX_RETRY) {
            try {
                ensureValidTokens()
                return Result.success(block())
            } catch (e: JawaliApiException) {
                lastException = e
                if (e.isRetryable && attempt < MAX_RETRY - 1) {
                    refreshTokens()
                    continue
                }
                break
            } catch (e: Exception) {
                lastException = e
                break
            }
        }
        return Result.failure(lastException ?: Exception("فشل غير متوقع"))
    }

    // ─── ensureValidTokens — مطابق لـ ensureValidTokens() ───

    private suspend fun ensureValidTokens() {
        if (!hasAuthToken()) loginToSystem().getOrThrow()
        if (!hasWalletToken()) walletAuthentication().getOrThrow()
    }

    // ─── refreshTokens — مطابق لـ refreshTokens() ───────────

    private suspend fun refreshTokens() {
        try {
            invalidateTokens()
            loginToSystem().getOrThrow()
            walletAuthentication().getOrThrow()
        } catch (_: Exception) { /* silent fail — matching PHP */ }
    }

    // ─── Token error detection — مطابق لـ tokenErrorKeywords ──

    private fun isTokenError(response: JawaliStructuredResponse): Boolean {
        val desc = (response.responseStatus?.systemStatusDesc ?: "").lowercase()
        val descNative = (response.responseStatus?.systemStatusDescNative ?: "").lowercase()
        val combined = "$desc $descNative"
        val keywords = listOf("invalid access token", "expired", "unauthorized", "authentication failed", "token")
        return keywords.any { combined.contains(it) }
    }

    // ─── مساعدات ─────────────────────────────────────────────

    fun invalidateTokens() {
        accessToken = null; walletToken = null
        accessTokenExpiresAt = 0; walletTokenExpiresAt = 0
    }

    fun getTokenStatus(): Map<String, Any?> = mapOf(
        "hasAccessToken" to (accessToken != null),
        "accessTokenValid" to hasAuthToken(),
        "hasWalletToken" to (walletToken != null),
        "walletTokenValid" to hasWalletToken()
    )
}

// ─── JawaliApiException — مطابق لـ JawaliApiException.php ──

class JawaliApiException(
    override val message: String,
    val error: String? = null,
    val isRetryable: Boolean = false
) : Exception(message)
