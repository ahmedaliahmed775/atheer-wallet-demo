package com.fintech.app.data

import com.fintech.app.model.*
import com.fintech.app.network.JawaliGatewayApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * مدير توكنات جوالي — مطابق لـ JawaliService في حزمة جوالي الرسمية
 *
 * يدير:
 * - accessToken (من loginToSystem)
 * - walletToken (من walletAuthentication)
 * - إعادة المحاولة التلقائية عند انتهاء صلاحية التوكنات
 * - التدفق الرباعي الكامل: login → walletAuth → inquiry → cashout
 *
 * بيانات الوكيل (Merchant) تُقرأ من الـ config (مطابق لـ config/jawali.php)
 */
@Singleton
class JawaliTokenManager @Inject constructor(
    private val api: JawaliGatewayApi
) {
    // ─── توكنات ─────────────────────────────────────────────
    private var accessToken: String? = null
    private var walletToken: String? = null
    private var accessTokenExpiresAt: Long = 0
    private var walletTokenExpiresAt: Long = 0

    // ─── بيانات الوكيل (مطابقة لـ config/jawali.php) ────────
    // في الإنتاج: تُقرأ من BuildConfig أو Remote Config
    private val merchantUsername = "atheer_merchant"
    private val merchantPassword = "atheer_pass_123"
    private val merchantWallet = "777000001"
    private val merchantWalletPassword = "wallet_pass_123"
    private val merchantOrgId = "atheer-org-001"
    private val merchantUserId = "atheer.api.user"
    private val merchantExternalUser = "atheer_ext_1"

    // ─── Retry settings (مطابق لـ retry.max_attempts) ───────
    private val maxRetryAttempts = 2
    private val retryStatusCodes = listOf(400, 401)

    // ─── SignonDetail ────────────────────────────────────────

    private fun buildSignonDetail(): SignonDetail = SignonDetail(
        orgID = merchantOrgId,
        userID = merchantUserId,
        externalUser = merchantExternalUser
    )

    // ─── Token Checks ───────────────────────────────────────

    private fun isAccessTokenValid(): Boolean =
        accessToken != null && System.currentTimeMillis() < accessTokenExpiresAt

    private fun isWalletTokenValid(): Boolean =
        walletToken != null && System.currentTimeMillis() < walletTokenExpiresAt

    // ═══════════════════════════════════════════════════════════
    // ① loginToSystem — مطابق لـ Jawali::loginToSystem()
    // ═══════════════════════════════════════════════════════════

    suspend fun loginToSystem(): Result<String> = runCatching {
        val response = api.login(
            JawaliLoginRequest(
                username = merchantUsername,
                password = merchantPassword
            )
        )

        if (!response.success || response.accessToken == null) {
            throw Exception(response.message ?: "فشل تسجيل الدخول للنظام")
        }

        accessToken = response.accessToken
        val expiresIn = response.expiresIn ?: 3600
        accessTokenExpiresAt = System.currentTimeMillis() + (expiresIn * 1000L)

        response.accessToken
    }

    // ═══════════════════════════════════════════════════════════
    // ② walletAuthentication — مطابق لـ Jawali::walletAuthentication()
    // ═══════════════════════════════════════════════════════════

    suspend fun walletAuthentication(): Result<String> = runCatching {
        // التأكد من وجود accessToken
        if (!isAccessTokenValid()) {
            loginToSystem().getOrThrow()
        }

        val response = api.walletAuthentication(
            JawaliWalletAuthRequest(
                header = JawaliWalletAuthHeader(
                    signonDetail = buildSignonDetail(),
                    accessToken = accessToken
                ),
                body = JawaliWalletAuthBody(
                    wallet = merchantWallet,
                    walletPassword = merchantWalletPassword
                )
            )
        )

        if (!response.success || response.walletToken == null) {
            throw Exception(response.message ?: "فشل مصادقة المحفظة")
        }

        walletToken = response.walletToken
        val expiresIn = response.expiresIn ?: 1800
        walletTokenExpiresAt = System.currentTimeMillis() + (expiresIn * 1000L)

        response.walletToken
    }

    // ═══════════════════════════════════════════════════════════
    // ③ ecommerceInquiry — مطابق لـ Jawali::ecommerceInquiry()
    // ═══════════════════════════════════════════════════════════

    suspend fun ecommerceInquiry(
        voucher: String,
        receiverMobile: String,
        purpose: String
    ): Result<JawaliPayagData> = executeWithRetry { attempt ->
        val response = api.payag(
            JawaliPayagRequest(
                header = JawaliPayagHeader(
                    signonDetail = buildSignonDetail(),
                    accessToken = accessToken ?: throw Exception("لا يوجد accessToken"),
                    walletToken = walletToken ?: throw Exception("لا يوجد walletToken")
                ),
                body = JawaliPayagBody(
                    voucher = voucher,
                    receiverMobile = receiverMobile,
                    purpose = purpose
                )
            )
        )

        if (!response.success || response.data == null) {
            throw JawaliApiException(
                message = response.message ?: "فشل الاستعلام",
                error = response.error,
                isRetryable = response.error in listOf("ACCESS_TOKEN_EXPIRED", "WALLET_TOKEN_EXPIRED")
            )
        }

        if (response.data.state != "PENDING") {
            throw Exception("حالة غير متوقعة: ${response.data.state}")
        }

        response.data
    }

    // ═══════════════════════════════════════════════════════════
    // ④ ecommerceCashout — مطابق لـ Jawali::ecommerceCashout()
    // ═══════════════════════════════════════════════════════════

    suspend fun ecommerceCashout(
        voucher: String,
        receiverMobile: String,
        purpose: String
    ): Result<JawaliPayagData> = executeWithRetry { attempt ->
        val response = api.payag(
            JawaliPayagRequest(
                header = JawaliPayagHeader(
                    signonDetail = buildSignonDetail(),
                    accessToken = accessToken ?: throw Exception("لا يوجد accessToken"),
                    walletToken = walletToken ?: throw Exception("لا يوجد walletToken")
                ),
                body = JawaliPayagBody(
                    voucher = voucher,
                    receiverMobile = receiverMobile,
                    purpose = purpose
                )
            )
        )

        if (!response.success || response.data == null) {
            throw JawaliApiException(
                message = response.message ?: "فشل الصرف",
                error = response.error,
                isRetryable = response.error in listOf("ACCESS_TOKEN_EXPIRED", "WALLET_TOKEN_EXPIRED")
            )
        }

        response.data
    }

    // ═══════════════════════════════════════════════════════════
    // التدفق الكامل — مطابق لـ processPayment() في الوثائق
    // ═══════════════════════════════════════════════════════════
    //
    //  login → walletAuth → inquiry(PENDING) → cashout(SUCCESS)

    suspend fun processFullPayment(
        voucher: String,
        receiverMobile: String,
        purpose: String,
        expectedAmount: Double? = null,
        expectedCurrency: String? = null
    ): Result<JawaliPayagData> = runCatching {

        // ① تسجيل الدخول
        loginToSystem().getOrThrow()

        // ② مصادقة المحفظة
        walletAuthentication().getOrThrow()

        // ③ استعلام
        val inquiryData = ecommerceInquiry(voucher, receiverMobile, purpose).getOrThrow()

        // ④ التحقق من الحالة والمبلغ والعملة (مطابق للوثائق)
        check(inquiryData.state == "PENDING") {
            "Transaction is not in PENDING state: ${inquiryData.state}"
        }

        if (expectedAmount != null) {
            check(inquiryData.amount == expectedAmount) {
                "Amount mismatch — expected: $expectedAmount, actual: ${inquiryData.amount}"
            }
        }

        if (expectedCurrency != null) {
            check(inquiryData.currency == expectedCurrency) {
                "Currency mismatch — expected: $expectedCurrency, actual: ${inquiryData.currency}"
            }
        }

        // ⑤ صرف
        val cashoutData = ecommerceCashout(voucher, receiverMobile, purpose).getOrThrow()

        check(cashoutData.state == "SUCCESS") {
            "Cashout failed with state: ${cashoutData.state}"
        }

        cashoutData
    }

    // ─── إعادة المحاولة (مطابق لـ retry.max_attempts + status_codes) ──

    private suspend fun <T> executeWithRetry(
        block: suspend (attempt: Int) -> T
    ): Result<T> {
        var lastException: Exception? = null

        for (attempt in 0..maxRetryAttempts) {
            try {
                // تجديد التوكنات إذا منتهية
                if (!isAccessTokenValid()) loginToSystem().getOrThrow()
                if (!isWalletTokenValid()) walletAuthentication().getOrThrow()

                return Result.success(block(attempt))
            } catch (e: JawaliApiException) {
                lastException = e
                if (e.isRetryable && attempt < maxRetryAttempts) {
                    // إعادة تسجيل الدخول + المصادقة
                    invalidateTokens()
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

    // ─── مساعدات ─────────────────────────────────────────────

    fun invalidateTokens() {
        accessToken = null
        walletToken = null
        accessTokenExpiresAt = 0
        walletTokenExpiresAt = 0
    }

    fun getTokenStatus(): Map<String, Any?> = mapOf(
        "hasAccessToken" to (accessToken != null),
        "accessTokenValid" to isAccessTokenValid(),
        "hasWalletToken" to (walletToken != null),
        "walletTokenValid" to isWalletTokenValid()
    )
}

// ─── استثناء مخصص (مطابق لـ JawaliApiException في الحزمة) ──

class JawaliApiException(
    override val message: String,
    val error: String? = null,
    val isRetryable: Boolean = false
) : Exception(message)
