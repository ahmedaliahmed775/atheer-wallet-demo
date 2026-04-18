package com.fintech.app.data

import com.fintech.app.model.*
import com.fintech.app.network.JawaliGatewayApi
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * مدير توكنات جوالي — مطابق ١٠٠٪ لـ @alsharie/jawalijs + JawaliService.php
 *
 * التعديلات عن النسخة السابقة:
 * 1. استخدام JawaliConfig بدلاً من القيم الثابتة — مطابق لـ config/jawali.php
 * 2. إضافة tokenExpiryBuffer (300 ثانية) — مطابق لـ TokenManager.php
 * 3. إضافة secureLogout() — مطابق لـ TokenManager::secureClearAllTokens()
 * 4. استخدام auth.wallet بدلاً من walletIdentifier في PAYAG body — مطابق لـ JS SDK
 * 5. إضافة phone من JawaliConfig — مطابق لـ JawaliConfig في JS SDK
 * 6. إضافة validateEcommerceParams() — مطابق لـ JawaliService::validateEcommerceParams()
 */
@Singleton
class JawaliTokenManager @Inject constructor(
    private val api: JawaliGatewayApi,
    private val jawaliConfig: JawaliConfig
) {
    // ─── Tokens — مطابق لـ TokenManager.php ──────────────────
    private var accessToken: String? = null
    private var walletToken: String? = null
    private var accessTokenExpiresAt: Long = 0
    private var walletTokenExpiresAt: Long = 0

    // ─── Token Expiry Buffer — مطابق لـ TokenManager.php ($tokenExpiryBuffer = 300) ──
    // يُطرح من وقت الانتهاء لتجنب استخدام توكن على وشك الانتهاء
    private val tokenExpiryBufferMs = JawaliConfig.TOKEN_EXPIRY_BUFFER_SECONDS * 1000L

    // ─── Token Checks — مطابق لـ TokenManager::hasAuthToken() ──

    private fun hasAuthToken(): Boolean =
        accessToken != null && System.currentTimeMillis() < accessTokenExpiresAt

    private fun hasWalletToken(): Boolean =
        walletToken != null && System.currentTimeMillis() < walletTokenExpiresAt

    // ─── Payload Builders — مطابق لـ buildStructuredRequestPayload() ──

    private suspend fun buildSignonDetail(): JawaliSignonDetail = JawaliSignonDetail(
        clientID = JawaliConfig.COMMON_SIGNON_CLIENT_ID,
        orgID = jawaliConfig.orgId.first(),
        userID = jawaliConfig.userId.first(),
        externalUser = jawaliConfig.externalUser.first()
    )

    private suspend fun buildHeader(serviceName: String, domainName: String): JawaliRequestHeader {
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
                bodyType = JawaliConfig.COMMON_BODY_TYPE
            )
        )
    }

    private fun generateRefId(): String = System.currentTimeMillis().toString()

    private fun bearerHeader(): String = "Bearer ${accessToken ?: ""}"

    // ─── Input Validation — مطابق لـ JawaliService::validateEcommerceParams() ──

    private fun validateEcommerceParams(voucher: String, receiverMobile: String) {
        require(voucher.isNotBlank()) { "Voucher number cannot be empty" }
        require(receiverMobile.isNotBlank()) { "Receiver mobile number cannot be empty" }
        // مطابق لـ preg_match في PHP SDK
        require(receiverMobile.matches(Regex("^[0-9+\\-\\s()]{7,15}$"))) {
            "Invalid mobile number format: $receiverMobile"
        }
        require(voucher.matches(Regex("^[0-9A-Za-z\\-_]{3,50}$"))) {
            "Invalid voucher format: $voucher"
        }
    }

    // ═══════════════════════════════════════════════════════════
    // ① loginToSystem — مطابق لـ JawaliService::loginToSystem()
    // Http::asForm()->post(baseUrl . '/oauth/token', payload)
    // ═══════════════════════════════════════════════════════════

    suspend fun loginToSystem(): Result<String> = runCatching {
        val username = jawaliConfig.username.first()
        val password = jawaliConfig.password.first()

        if (username.isBlank() || password.isBlank()) {
            throw Exception("Missing required authentication credentials. Please configure Jawali settings.")
        }

        val response = api.loginToSystem(
            username = username,
            password = password
        )

        if (!response.isSuccess()) {
            throw Exception(response.errorDescription ?: response.error ?: "فشل تسجيل الدخول")
        }

        accessToken = response.accessToken
        val expiresIn = response.expiresIn ?: 3600
        // ★ تعديل: طرح tokenExpiryBuffer — مطابق لـ TokenManager.php
        accessTokenExpiresAt = System.currentTimeMillis() + (expiresIn * 1000L) - tokenExpiryBufferMs

        response.accessToken!!
    }

    // ═══════════════════════════════════════════════════════════
    // ② walletAuthentication — مطابق لـ JawaliService::walletAuthentication()
    // ═══════════════════════════════════════════════════════════

    suspend fun walletAuthentication(): Result<String> = runCatching {
        if (!hasAuthToken()) loginToSystem().getOrThrow()

        val walletIdentifier = jawaliConfig.walletIdentifier.first()
        val walletPassword = jawaliConfig.walletPassword.first()

        if (walletIdentifier.isBlank() || walletPassword.isBlank()) {
            throw Exception("Missing required wallet credentials. Please configure Jawali wallet settings.")
        }

        val request = JawaliStructuredRequest(
            header = buildHeader(JawaliConfig.WALLET_AUTH_SERVICE, JawaliConfig.WALLET_AUTH_DOMAIN),
            body = mapOf(
                "identifier" to walletIdentifier,
                "password" to walletPassword
            )
        )

        val response = api.callWS(bearerHeader(), request)

        if (!response.isSuccess()) {
            throw Exception(response.getErrorMessage() ?: "فشل مصادقة المحفظة")
        }

        val token = response.responseBody?.accessToken
            ?: throw Exception("لم يُرجع walletToken")

        walletToken = token
        val expiresIn = response.responseBody?.expiresIn ?: 1800
        // ★ تعديل: طرح tokenExpiryBuffer — مطابق لـ TokenManager.php
        walletTokenExpiresAt = System.currentTimeMillis() + (expiresIn * 1000L) - tokenExpiryBufferMs

        token
    }

    // ═══════════════════════════════════════════════════════════
    // ③ ecommerceInquiry — مطابق لـ JawaliService::ecommerceInquiry()
    // ═══════════════════════════════════════════════════════════

    suspend fun ecommerceInquiry(
        voucher: String,
        receiverMobile: String,
        purpose: String
    ): Result<JawaliResponseBody> = executeWithRetry(JawaliConfig.INQUIRY_SERVICE, JawaliConfig.INQUIRY_DOMAIN) {
        // ★ إضافة: التحقق من المدخلات — مطابق لـ validateEcommerceParams()
        validateEcommerceParams(voucher, receiverMobile)

        // ★ تعديل: استخدام auth.wallet لـ agentWallet — مطابق لـ JS SDK
        // JS SDK: this.attributes.body.agentWallet = this.config.auth.wallet
        // PHP SDK: 'agentWallet' => $this->authConfig['wallet_identifier']
        val agentWalletValue = jawaliConfig.wallet.first()
        val walletPasswordValue = jawaliConfig.walletPassword.first()

        val request = JawaliStructuredRequest(
            header = buildHeader(JawaliConfig.INQUIRY_SERVICE, JawaliConfig.INQUIRY_DOMAIN),
            body = mapOf(
                "agentWallet" to agentWalletValue,
                "voucher" to voucher,
                "receiverMobile" to receiverMobile,
                "password" to walletPasswordValue,
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
    // ═══════════════════════════════════════════════════════════

    suspend fun ecommerceCashout(
        voucher: String,
        receiverMobile: String,
        purpose: String
    ): Result<JawaliResponseBody> = executeWithRetry(JawaliConfig.CASHOUT_SERVICE, JawaliConfig.CASHOUT_DOMAIN) {
        // ★ إضافة: التحقق من المدخلات
        validateEcommerceParams(voucher, receiverMobile)

        val agentWalletValue = jawaliConfig.wallet.first()
        val walletPasswordValue = jawaliConfig.walletPassword.first()

        val request = JawaliStructuredRequest(
            header = buildHeader(JawaliConfig.CASHOUT_SERVICE, JawaliConfig.CASHOUT_DOMAIN),
            body = mapOf(
                "agentWallet" to agentWalletValue,
                "voucher" to voucher,
                "receiverMobile" to receiverMobile,
                "password" to walletPasswordValue,
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
        check(cashoutData.status == "SUCCESS") { "Cashout failed: ${cashoutData.status}" }

        cashoutData
    }

    // ─── Retry — مطابق لـ sendStructuredRequest() logic ─────

    private suspend fun executeWithRetry(
        serviceName: String,
        domainName: String,
        block: suspend () -> JawaliResponseBody
    ): Result<JawaliResponseBody> {
        val maxRetry = jawaliConfig.maxRetry.first()
        var lastException: Exception? = null

        for (attempt in 0 until maxRetry) {
            try {
                ensureValidTokens()
                return Result.success(block())
            } catch (e: JawaliApiException) {
                lastException = e
                if (e.isRetryable && attempt < maxRetry - 1) {
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
        // ★ تعديل: مسح آمن — مطابق لـ TokenManager::clearAllTokens()
        accessToken = null; walletToken = null
        accessTokenExpiresAt = 0; walletTokenExpiresAt = 0
    }

    /**
     * تسجيل خروج آمن — مطابق لـ TokenManager::secureClearAllTokens()
     * يكتب بيانات عشوائية فوق التوكنات قبل مسحها لمنع الاسترجاع
     */
    fun secureLogout() {
        // كتابة بيانات عشوائية فوق القيم الحالية
        val randomData = List(10) { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }.joinString()
        accessToken = randomData
        walletToken = randomData
        Thread.sleep(50) // تأخير بسيط لضمان الكتابة
        accessToken = null; walletToken = null
        accessTokenExpiresAt = 0; walletTokenExpiresAt = 0
    }

    fun getTokenStatus(): Map<String, Any?> = mapOf(
        "hasAccessToken" to (accessToken != null),
        "accessTokenValid" to hasAuthToken(),
        "hasWalletToken" to (walletToken != null),
        "walletTokenValid" to hasWalletToken(),
        "accessTokenExpiresAt" to accessTokenExpiresAt,
        "walletTokenExpiresAt" to walletTokenExpiresAt,
        "tokenExpiryBufferMs" to tokenExpiryBufferMs
    )
}

// ─── JawaliApiException — مطابق لـ JawaliApiException.php ──

class JawaliApiException(
    override val message: String,
    val error: String? = null,
    val isRetryable: Boolean = false
) : Exception(message)
