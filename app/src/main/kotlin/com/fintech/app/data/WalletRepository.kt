package com.fintech.app.data

import com.fintech.app.model.*
import com.fintech.app.network.WalletApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val api: WalletApiService,
    private val session: SessionManager
) {

    suspend fun login(phone: String, password: String): Result<AuthBody> = runCatching {
        val response = api.login(LoginRequest(phone, password))
        val body = response.body ?: throw Exception(response.responseMessage)
        session.save(
            token   = body.accessToken,
            userId  = body.user.id,
            name    = body.user.name,
            phone   = body.user.phone,
            role    = body.user.role,
            balance = body.user.balance
        )
        body
    }

    suspend fun signup(
        name: String,
        phone: String,
        password: String,
        role: String
    ): Result<AuthBody> = runCatching {
        val response = api.signup(SignupRequest(name, phone, password, role))
        val body = response.body ?: throw Exception(response.responseMessage)
        session.save(
            token   = body.accessToken,
            userId  = body.user.id,
            name    = body.user.name,
            phone   = body.user.phone,
            role    = body.user.role,
            balance = body.user.balance
        )
        body
    }

    suspend fun logout() {
        session.clear()
    }

    suspend fun getBalance(): Result<BalanceBody> = runCatching {
        val response = api.getBalance()
        val body = response.body ?: throw Exception(response.responseMessage)
        session.updateBalance(body.balance)
        body
    }

    suspend fun transfer(
        receiverPhone: String,
        amount: Double,
        note: String
    ): Result<TransferBody> = runCatching {
        val response = api.transfer(TransferRequest(receiverPhone, amount, note))
        val body = response.body ?: throw Exception(response.responseMessage)
        session.updateBalance(body.newBalance)
        body
    }

    suspend fun generateVoucher(amount: Double): Result<VoucherBody> = runCatching {
        val response = api.generateVoucher(GenerateVoucherRequest(amount))
        val body = response.body ?: throw Exception(response.responseMessage)
        session.updateBalance(body.newBalance)
        body
    }

    suspend fun getTransactions(page: Int): Result<TransactionsBody> = runCatching {
        val response = api.getTransactions(page)
        response.body ?: throw Exception(response.responseMessage)
    }

    suspend fun getMerchantTransactions(page: Int): Result<TransactionsBody> = runCatching {
        val response = api.getMerchantTransactions(page)
        response.body ?: throw Exception(response.responseMessage)
    }

    suspend fun cashout(
        agentWallet: String,
        password: String,
        accessToken: String,
        voucherCode: String
    ): Result<CashoutBody> = runCatching {
        val response = api.cashout(CashoutRequest(agentWallet, password, accessToken, voucherCode))
        response.body ?: throw Exception(response.responseMessage)
    }
}
