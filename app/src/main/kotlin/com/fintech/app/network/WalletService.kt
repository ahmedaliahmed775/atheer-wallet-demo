package com.fintech.app.network

import com.fintech.app.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WalletService {

    @POST("auth/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<SignupResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/wallet-auth")
    suspend fun walletAuth(
        @Body request: WalletAuthRequest
    ): Response<WalletAuthResponse>

    @POST("wallet/generate-voucher")
    suspend fun generateVoucher(
        @Body request: VoucherRequest
    ): Response<VoucherResponse>

    @POST("merchant/switch-charge")
    suspend fun merchantCashout(
        @Body request: MerchantChargeRequest
    ): Response<MerchantChargeResponse>

    @POST("merchant/inquiry")
    suspend fun inquiry(
        @Body request: InquiryRequest
    ): Response<InquiryResponse>
}
