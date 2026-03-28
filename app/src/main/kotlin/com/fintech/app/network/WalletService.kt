package com.fintech.app.network

import com.fintech.app.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WalletService {

    @POST("wallet/generate-voucher")
    suspend fun generateVoucher(
        @Body request: VoucherRequest
    ): Response<BaseResponse<VoucherResponse>>

    @POST("merchant/switch-charge")
    suspend fun merchantSwitchCharge(
        @Body request: MerchantChargeRequest
    ): Response<BaseResponse<MerchantChargeResponse>>
}
