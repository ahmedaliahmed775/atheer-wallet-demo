package com.fintech.app.network

import com.fintech.app.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WalletRepository(private val service: WalletService) {

    suspend fun generateVoucher(amount: Double): Result<VoucherResponse> {
        return try {
            val response = service.generateVoucher(VoucherRequest(amount))
            if (response.isSuccessful && response.body()?.body != null) {
                Result.success(response.body()!!.body!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Unknown Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun merchantSwitchCharge(request: MerchantChargeRequest): Result<MerchantChargeResponse> {
        return try {
            val response = service.merchantSwitchCharge(request)
            if (response.isSuccessful && response.body()?.body != null) {
                Result.success(response.body()!!.body!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Unknown Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
