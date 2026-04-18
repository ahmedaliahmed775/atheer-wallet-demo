package com.fintech.app.model

import com.google.gson.annotations.SerializedName

// ─── Auth ─────────────────────────────────────────────────

data class LoginRequest(
    val phone: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val phone: String,
    val password: String,
    val role: String = "customer"
)

data class AuthResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String,
    val body: AuthBody?
)

data class AuthBody(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Long,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val name: String,
    val phone: String,
    val role: String,
    val posNumber: String? = null,
    val balance: Double
)

// ─── Wallet ───────────────────────────────────────────────

data class BalanceResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String?,
    val body: BalanceBody?
)

data class BalanceBody(
    val balance: Double,
    val currency: String? = null,
    val phone: String,
    val name: String,
    val role: String
)

data class TransferRequest(
    val receiverPhone: String,
    val amount: Double,
    val note: String = "تحويل"
)

data class TransferResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String?,
    val body: TransferBody?
)

data class TransferBody(
    val transactionId: String,
    val refId: String,
    val amount: Double,
    val receiverName: String,
    val receiverPhone: String,
    val newBalance: Double,
    val timestamp: String
)

// ─── External Transfer ───────────────────────────────────

data class ExternalTransferRequest(
    val recipientPhone: String,
    val recipientName: String = "",
    val amount: Double,
    val note: String = "حوالة خارجية"
)

data class ExternalTransferResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String?,
    val body: ExternalTransferBody?
)

data class ExternalTransferBody(
    val transactionId: String,
    val refId: String,
    val amount: Double,
    val recipientPhone: String,
    val recipientName: String,
    val withdrawalCode: String,
    val expiresAt: String,
    val newBalance: Double,
    val timestamp: String
)

// ─── Bill Payment ─────────────────────────────────────────

data class BillPaymentRequest(
    val category: String,    // TELECOM, INTERNET, ELECTRICITY, WATER
    val provider: String,    // yemen_mobile, you, sabafon, etc
    val accountNumber: String,
    val amount: Double
)

data class BillPaymentResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String?,
    val body: BillPaymentBody?
)

data class BillPaymentBody(
    val transactionId: String,
    val billId: Int,
    val refId: String,
    val category: String,
    val provider: String,
    val accountNumber: String,
    val amount: Double,
    val newBalance: Double,
    val timestamp: String
)

// ─── QR Payment ───────────────────────────────────────────

data class QrPayRequest(
    val posNumber: String,
    val amount: Double,
    val note: String = ""
)

data class QrPayResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String?,
    val body: QrPayBody?
)

data class QrPayBody(
    val transactionId: String,
    val refId: String,
    val amount: Double,
    val merchantName: String,
    val merchantPhone: String? = null,
    val newBalance: Double,
    val timestamp: String
)

// ─── Cash Out ─────────────────────────────────────────────

data class CashOutRequest(
    val amount: Double
)

data class CashOutResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String?,
    val body: CashOutBody?
)

data class CashOutBody(
    val code: String,
    val amount: Double,
    val expiresAt: String,
    val newBalance: Double,
    val type: String
)

// ─── Cash In ──────────────────────────────────────────────

data class CashInRequest(
    val amount: Double,
    val agentCode: String = "DEMO"
)

data class CashInResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String?,
    val body: CashInBody?
)

data class CashInBody(
    val transactionId: String,
    val refId: String,
    val amount: Double,
    val newBalance: Double,
    val timestamp: String
)

// ─── Merchant ─────────────────────────────────────────────



data class QrInfoResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    val body: QrInfoBody?
)

data class QrInfoBody(
    val merchantName: String,
    val merchantPhone: String? = null,
    val merchantPosNumber: String? = null,
    val qrData: String
)

// ─── Services ─────────────────────────────────────────────

data class ServicesResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    val body: ServicesBody?
)

data class ServicesBody(
    val services: List<ServiceCategory>
)

data class ServiceCategory(
    val category: String,
    val providers: List<ServiceProvider>
)

data class ServiceProvider(
    val id: String,
    val name: String,
    val icon: String
)

// ─── Transactions ─────────────────────────────────────────

data class TransactionsResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String?,
    val body: TransactionsBody?
)

data class TransactionsBody(
    val transactions: List<TransactionDto>,
    val total: Int,
    val page: Int,
    val pages: Int? = null,
    val totalAmount: Double? = null
)

data class TransactionDto(
    val id: String,
    val refId: String?,
    val type: String,       // DEBIT or CREDIT
    val txnType: String?,   // TRANSFER, CASHOUT, TOPUP, BILL_PAYMENT, etc
    val amount: Double,
    val counterparty: String?,
    val counterPhone: String?,
    val note: String?,
    val status: String,
    val metadata: Map<String, Any>? = null,
    val timestamp: String
)

// ─── Transaction Detail ──────────────────────────────────

data class TransactionDetailResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    val body: TransactionDetailBody?
)

data class TransactionDetailBody(
    val id: String,
    val refId: String?,
    val type: String,
    val txnType: String?,
    val amount: Double,
    val senderName: String?,
    val senderPhone: String?,
    val receiverName: String?,
    val receiverPhone: String?,
    val note: String?,
    val status: String,
    val metadata: Map<String, Any>? = null,
    val timestamp: String
)

// ═══════════════════════════════════════════════════════════════
// ─── Jawali Gateway Models — مطابقة لـ @alsharie/jawalijs ──
// ═══════════════════════════════════════════════════════════════
// مرجع: https://www.npmjs.com/package/@alsharie/jawalijs
// مرجع: https://github.com/Alsharie/jawali-payment
//
// التعديلات المطبقة:
// 1. JawaliResponseBody.accessToken → access_token (snake_case) ليطابق JS SDK
// 2. إضافة org_value و org_name لاستجابة مصادقة المحفظة
// 3. إضافة حقول استجابة الصرف: status, amount, balance, refId, IssuerRef, Currency
//    (استجابة الصرف تختلف عن الاستعلام — انظر التعليقات أدناه)
// ═══════════════════════════════════════════════════════════════

// ═══ POST /oauth/token — OAuth2 Login ═══════════════════════
// Content-Type: application/x-www-form-urlencoded (form-encoded)

// Login لا يحتاج data class للطلب — يُرسل كـ @FormUrlEncoded

data class JawaliLoginResponse(
    @SerializedName("access_token")  val accessToken: String? = null,
    @SerializedName("token_type")    val tokenType: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("expires_in")    val expiresIn: Int? = null,
    val scope: String? = null,
    // حقول الخطأ (OAuth2 error response)
    val error: String? = null,
    @SerializedName("error_description") val errorDescription: String? = null
) {
    fun isSuccess(): Boolean = accessToken != null
}

// ═══ POST /v1/ws/callWS — Structured Request ════════════════
// مطابق لـ buildStructuredRequestPayload() في JawaliService.php

// ── Header Components ──

data class JawaliServiceDetail(
    val corrID: String,          // UUID
    val domainName: String,      // "WalletDomain" أو "MerchantDomain"
    val serviceName: String      // "PAYWA.WALLETAUTHENTICATION" / "PAYAG.ECOMMERCEINQUIRY" / "PAYAG.ECOMMCASHOUT"
)

data class JawaliSignonDetail(
    val clientID: String = "WeCash",
    val orgID: String,
    val userID: String,
    val externalUser: String? = null
)

data class JawaliMessageContext(
    val clientDate: String,      // format: "YYYYMMDDHHmmss"
    val bodyType: String = "Clear"
)

data class JawaliRequestHeader(
    val serviceDetail: JawaliServiceDetail,
    val signonDetail: JawaliSignonDetail,
    val messageContext: JawaliMessageContext
)

// ── Full Structured Request ──

data class JawaliStructuredRequest(
    val header: JawaliRequestHeader,
    val body: Map<String, String>
)

// ── PAYWA Body — مطابق لـ walletAuthentication() ──

data class JawaliWalletAuthBody(
    val identifier: String,
    val password: String
)

// ── PAYAG Body — مطابق لـ ecommerceInquiry()/ecommerceCashout() ──

data class JawaliPayagBody(
    val agentWallet: String,
    val voucher: String,
    val receiverMobile: String,
    val password: String,
    val accessToken: String,     // هذا walletToken — يُسمّى accessToken في body
    val refId: String,
    val purpose: String = ""
)

// ═══ Structured Response — مطابق لـ JawaliResponse.php ══════
// { "responseBody": { ... }, "responseStatus": { "systemStatus": "0", ... } }

data class JawaliResponseStatus(
    val systemStatus: String? = null,
    val systemStatusDesc: String? = null,
    val systemStatusDescNative: String? = null,
    val errorCode: String? = null
)

data class JawaliStructuredResponse(
    val responseBody: JawaliResponseBody? = null,
    val responseStatus: JawaliResponseStatus? = null
) {
    fun isSuccess(): Boolean = responseStatus?.systemStatus == "0"
    fun getErrorMessage(): String? = responseStatus?.systemStatusDesc
}

// ═══════════════════════════════════════════════════════════════
// responseBody — حقول مطابقة لـ @alsharie/jawalijs + PHP SDK
// ═══════════════════════════════════════════════════════════════
//
// ★ ملاحظة مهمة: استجابة الصرف تختلف عن استجابة الاستعلام!
//
// ┌─────────────────┬───────────────────────────┬───────────────────────────┐
// │ العملية         │ حقل المبلغ                │ حقل الحالة                │
// ├─────────────────┼───────────────────────────┼───────────────────────────┤
// │ مصادقة المحفظة  │ —                         │ —                         │
// │ استعلام (INQ)   │ txnamount                 │ state                     │
// │ صرف (CASHOUT)   │ amount                    │ status                    │
// └─────────────────┴───────────────────────────┴───────────────────────────┘
//
// التعديلات:
// 1. accessToken → access_token (snake_case) — يطابق JS SDK:
//    this.authHelper.setWalletToken(responseJson.responseBody.access_token)
// 2. إضافة org_value و org_name — يطابق JawaliWalletAuthResponse.getOrgValue()/getOrgName()
// 3. إضافة حقول الصرف: status, amount, balance, refId, IssuerRef, Currency
//    - status    ← PHP: getStatue() → responseBody.status
//    - amount    ← PHP: getAmount() → responseBody.amount
//    - balance   ← PHP: getBalance() → responseBody.balance
//    - refId     ← PHP: getTransactionRef() → responseBody.refId
//    - IssuerRef ← PHP: getIssuerRef() → responseBody.IssuerRef (capital I)
//    - Currency  ← JS:  getCurrency() → responseBody.Currency (capital C)
// ═══════════════════════════════════════════════════════════════

data class JawaliResponseBody(
    // ── PAYWA.WALLETAUTHENTICATION — مصادقة المحفظة ──
    // ★ تعديل: access_token (snake_case) بدلاً من accessToken
    @SerializedName("access_token") val accessToken: String? = null,
    // ★ إضافة: org_value و org_name — مطابق لـ JawaliWalletAuthResponse
    @SerializedName("org_value") val orgValue: String? = null,
    @SerializedName("org_name") val orgName: String? = null,
    // expiresIn يُستخدم في السيرفر المحاكي فقط (ليس في المواصفات الأصلية)
    val expiresIn: Int? = null,

    // ── PAYAG.ECOMMERCEINQUIRY — حقول الاستعلام ──
    val txnamount: String? = null,          // مبلغ المعاملة (استعلام)
    val txncurrency: String? = null,        // عملة المعاملة (استعلام)
    val state: String? = null,              // حالة الاستعلام: "PENDING"
    val issuerTrxRef: String? = null,       // مرجع مُصدر الاستعلام
    val trxDate: String? = null,            // تاريخ المعاملة
    val voucher: String? = null,            // رقم القسيمة (استعلام)
    val receiverMobile: String? = null,     // رقم المستلم (استعلام)
    val purpose: String? = null,            // الغرض (استعلام)
    val inquiryRef: String? = null,         // مرجع الاستعلام

    // ── PAYAG.ECOMMCASHOUT — حقول الصرف ──
    // ★ إضافة: حقول الصرف المختلفة عن الاستعلام
    val status: String? = null,             // ★ حالة الصرف: "SUCCESS" / "FAILED" (ليس state)
    val amount: String? = null,             // ★ مبلغ الصرف (ليس txnamount)
    val balance: String? = null,            // ★ رصيد المحفظة بعد الصرف
    val refId: String? = null,              // ★ رقم مرجع الصرف
    @SerializedName("IssuerRef")            // ★ حرف I كبير — مطابق PHP SDK
    val issuerRef: String? = null,          // ★ مرجع مُصدر الصرف (ليس issuerTrxRef)
    @SerializedName("Currency")             // ★ حرف C كبير — مطابق JS SDK
    val currency: String? = null            // ★ عملة الصرف (ليس txncurrency)
)

// ── FCM ──

data class FcmTokenRequest(
    val fcmToken: String
)

data class GenericResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String?
)

// ─── App UI State ─────────────────────────────────────────

data class AppUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val token: String = "",
    val userId: Int = 0,
    val userName: String = "",
    val userPhone: String = "",
    val userRole: String = "customer",
    val posNumber: String? = null,
    val balance: Double = 0.0,
    val transactions: List<TransactionDto> = emptyList(),
    val services: List<ServiceCategory> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val lastTransfer: TransferBody? = null,
    val lastBillPayment: BillPaymentBody? = null,
    val lastQrPay: QrPayBody? = null,
    val lastCashOut: CashOutBody? = null,
    val lastCashIn: CashInBody? = null,
    val lastExternalTransfer: ExternalTransferBody? = null,
    val transactionDetail: TransactionDetailBody? = null,
    // Jawali gateway state
    val jawaliInquiryResult: JawaliResponseBody? = null,
    val jawaliCashoutResult: JawaliResponseBody? = null
)
