package com.fintech.app.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseRequest<T>(
    val header: RequestHeader,
    val body: T
)

@Serializable
data class RequestHeader(
    val messageId: String,
    val messageTimestamp: String,
    val callerId: String
)

@Serializable
data class BaseResponse<T>(
    val header: ResponseHeader,
    val body: T? = null,
    val error: ErrorDetail? = null
)

@Serializable
data class ResponseHeader(
    val messageId: String,
    val messageTimestamp: String,
    val status: String
)

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String
)

@Serializable
data class VoucherRequest(
    val amount: Double,
    val currency: String = "USD"
)

@Serializable
data class VoucherResponse(
    val voucherCode: String,
    val expiryTimestamp: String,
    val amount: Double
)

@Serializable
data class MerchantChargeRequest(
    val agentWallet: String,
    val password: String,
    val accessToken: String,
    val voucher: String
)

@Serializable
data class MerchantChargeResponse(
    val transactionId: String,
    val status: String,
    val message: String
)
