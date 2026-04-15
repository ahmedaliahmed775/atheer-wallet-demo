package com.fintech.app.network

import com.fintech.app.model.*
import retrofit2.http.*

interface WalletApiService {

    // ─── Auth ─────────────────────────────────────────────
    @POST("api/v1/auth/signup")
    suspend fun signup(@Body request: SignupRequest): AuthResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // ─── Wallet ───────────────────────────────────────────
    @GET("api/v1/wallet/balance")
    suspend fun getBalance(): BalanceResponse

    @POST("api/v1/wallet/transfer")
    suspend fun transfer(@Body request: TransferRequest): TransferResponse

    @POST("api/v1/wallet/transfer-external")
    suspend fun transferExternal(@Body request: ExternalTransferRequest): ExternalTransferResponse

    @POST("api/v1/wallet/generate-voucher")
    suspend fun generateVoucher(@Body request: GenerateVoucherRequest): VoucherResponse

    @POST("api/v1/wallet/pay-bill")
    suspend fun payBill(@Body request: BillPaymentRequest): BillPaymentResponse

    @POST("api/v1/wallet/qr-pay")
    suspend fun qrPay(@Body request: QrPayRequest): QrPayResponse

    @POST("api/v1/wallet/generate-cashout")
    suspend fun generateCashout(@Body request: CashOutRequest): CashOutResponse

    @POST("api/v1/wallet/cash-in")
    suspend fun cashIn(@Body request: CashInRequest): CashInResponse

    @GET("api/v1/wallet/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): TransactionsResponse

    @GET("api/v1/wallet/transactions/{id}")
    suspend fun getTransactionDetail(@Path("id") id: String): TransactionDetailResponse

    @GET("api/v1/wallet/services")
    suspend fun getServices(): ServicesResponse

    @GET("api/v1/wallet/profile")
    suspend fun getProfile(): BalanceResponse

    // ─── Merchant ─────────────────────────────────────────
    @POST("api/v1/merchant/switch-charge")
    suspend fun cashout(@Body request: CashoutRequest): CashoutResponse

    @GET("api/v1/merchant/transactions")
    suspend fun getMerchantTransactions(@Query("page") page: Int = 1): TransactionsResponse

    @GET("api/v1/merchant/qr-info")
    suspend fun getMerchantQrInfo(): QrInfoResponse
}
