package com.fintech.app.network

import com.fintech.app.model.*
import retrofit2.Response

class WalletRepository(private val service: WalletService) {

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return handleApi { service.login(request) }
    }

    suspend fun walletAuth(request: WalletAuthRequest): Result<WalletAuthResponse> {
        return handleApi { service.walletAuth(request) }
    }

    suspend fun signup(request: SignupRequest): Result<SignupResponse> {
        return handleApi { service.signup(request) }
    }

    suspend fun generateVoucher(amount: Double): Result<VoucherResponse> {
        return handleApi { service.generateVoucher(VoucherRequest(amount)) }
    }

    suspend fun merchantCashout(request: MerchantChargeRequest): Result<MerchantChargeResponse> {
        return handleApi { service.merchantCashout(request) }
    }

    suspend fun inquiry(request: InquiryRequest): Result<InquiryResponse> {
        return handleApi { service.inquiry(request) }
    }

    private suspend fun <T> handleApi(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
