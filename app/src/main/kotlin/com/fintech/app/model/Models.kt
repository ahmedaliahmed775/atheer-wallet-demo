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
    val balance: Double
)

// ─── Wallet ───────────────────────────────────────────────

data class BalanceResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String,
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
    @SerializedName("ResponseMessage") val responseMessage: String,
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

// ─── Voucher ──────────────────────────────────────────────

data class GenerateVoucherRequest(
    val amount: Double
)

data class VoucherResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String,
    val body: VoucherBody?
)

data class VoucherBody(
    val voucherCode: String,
    val amount: Double,
    val expiresAt: String,
    val newBalance: Double,
    val status: String
)

// ─── Merchant ─────────────────────────────────────────────

data class CashoutRequest(
    val agentWallet: String,
    val password: String,
    val accessToken: String,
    val voucher: String
)

data class CashoutResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String,
    val body: CashoutBody?
)

data class CashoutBody(
    val transactionId: String,
    val refId: String,
    val voucherCode: String,
    val amount: Double,
    val merchantWallet: String,
    val merchantName: String,
    val timestamp: String
)

// ─── Transactions ─────────────────────────────────────────

data class TransactionsResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String,
    val body: TransactionsBody?
)

data class TransactionsBody(
    val transactions: List<TransactionDto>,
    val total: Int,
    val page: Int,
    val pages: Int
)

data class TransactionDto(
    val id: String,
    val refId: String?,
    val type: String,       // DEBIT or CREDIT
    val txnType: String?,   // TRANSFER, CASHOUT, TOPUP
    val amount: Double,
    val counterparty: String?,
    val counterPhone: String?,
    val note: String?,
    val status: String,
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
    val balance: Double = 0.0,
    val transactions: List<TransactionDto> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val lastVoucher: VoucherBody? = null,
    val lastTransfer: TransferBody? = null,
    val lastCashout: CashoutBody? = null
)
