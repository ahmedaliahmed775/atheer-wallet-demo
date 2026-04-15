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
    val currency: String,
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
    val posNumber: String,
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
    val posNumber: String,
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
    val transactionDetail: TransactionDetailBody? = null
)
