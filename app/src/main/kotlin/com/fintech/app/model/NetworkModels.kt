package com.fintech.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- Auth (Signup) ---
@Serializable
data class SignupRequest(
    val phone: String,
    val password: String,
    val name: String,
    val role: String // "customer" or "merchant"
)

@Serializable
data class SignupResponse(
    val success: Boolean,
    val message: String,
    val data: SignupResponseData
)

@Serializable
data class SignupResponseData(
    @SerialName("access_token") val accessToken: String,
    val user: SignupUser
)

@Serializable
data class SignupUser(
    val phone: String,
    val name: String,
    val role: String,
    @SerialName("initial_balance") val initialBalance: Double
)

// --- Auth (Login) ---
@Serializable
data class LoginRequest(
    val grant_type: String = "password",
    val client_id: String,
    val client_secret: String,
    val scope: String = "read",
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val refresh_token: String? = null,
    val expires_in: Int,
    val scope: String? = null
)

// --- Wallet Auth ---
@Serializable
data class WalletAuthRequest(
    val identifier: String,
    val password: String
)

@Serializable
data class WalletAuthResponse(
    val access_token: String,
    val org_value: String? = null,
    val org_name: String? = null
)

// --- Voucher ---
@Serializable
data class VoucherRequest(
    val amount: Double
)

@Serializable
data class VoucherResponse(
    val voucherCode: String,
    val amount: Double,
    val expiresAt: String,
    val status: String
)

// --- Merchant Cashout ---
@Serializable
data class MerchantChargeRequest(
    val agentWallet: String,
    val voucher: String,
    val receiverMobile: String,
    val password: String,
    val accessToken: String,
    val refId: String,
    val purpose: String
)

@Serializable
data class MerchantChargeResponse(
    val amount: Double? = null,
    val balance: Double? = null,
    val IssuerRef: String? = null,
    val refId: String? = null,
    val userId: String? = null,
    val trxDate: String? = null,
    val status: String? = null,
    val message: String? = null // For error cases
)

// --- Inquiry ---
@Serializable
data class InquiryRequest(
    val agentWallet: String,
    val password: String,
    val accessToken: String,
    val refId: String
)

@Serializable
data class InquiryResponse(
    val issuerTrxRef: String? = null,
    val txnamount: Double? = null,
    val receiverMobile: String? = null,
    val senderMobile: String? = null,
    val updateTime: String? = null,
    val state: String? = null,
    val trxDate: String? = null
)
