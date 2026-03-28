package com.fintech.app.network

import com.fintech.app.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WalletService {

    @POST("auth/register")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<BaseResponse<SignupResponseData>>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/wallet-auth")
    suspend fun walletAuth(
        @Body request: WalletAuthRequest
    ): Response<BaseResponse<WalletAuthResponse>>

    @POST("wallet/generate-voucher")
    suspend fun generateVoucher(
        @Body request: VoucherRequest
    ): Response<BaseResponse<VoucherResponse>>

    @POST("merchant/switch-charge")
    suspend fun merchantCashout(
        @Body request: MerchantChargeRequest
    ): Response<BaseResponse<MerchantChargeResponse>>

    @POST("merchant/inquiry")
    suspend fun inquiry(
        @Body request: InquiryRequest
    ): Response<BaseResponse<InquiryResponse>>
}
