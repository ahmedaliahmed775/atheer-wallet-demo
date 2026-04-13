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

    @POST("api/v1/wallet/generate-voucher")
    suspend fun generateVoucher(@Body request: GenerateVoucherRequest): VoucherResponse

    @GET("api/v1/wallet/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): TransactionsResponse

    @GET("api/v1/wallet/profile")
    suspend fun getProfile(): BalanceResponse

    // ─── Merchant ─────────────────────────────────────────
    @POST("api/v1/merchant/switch-charge")
    suspend fun cashout(@Body request: CashoutRequest): CashoutResponse

    @GET("api/v1/merchant/transactions")
    suspend fun getMerchantTransactions(@Query("page") page: Int = 1): TransactionsResponse
}
