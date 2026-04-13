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
    // ─── Auth ─────────────────────────────────────────────

    suspend fun login(phone: String, password: String): Result<AuthBody> = runCatching {
        val res = api.login(LoginRequest(phone, password))
        if (res.responseCode != 0) error(res.responseMessage)
        val body = res.body ?: error("لا يوجد بيانات في الاستجابة")
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

    suspend fun signup(name: String, phone: String, password: String, role: String): Result<AuthBody> = runCatching {
        val res = api.signup(SignupRequest(name, phone, password, role))
        if (res.responseCode != 0) error(res.responseMessage)
        val body = res.body ?: error("لا يوجد بيانات في الاستجابة")
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

    suspend fun logout() = session.clear()

    // ─── Wallet ───────────────────────────────────────────

    suspend fun getBalance(): Result<BalanceBody> = runCatching {
        val res = api.getBalance()
        if (res.responseCode != 0) error(res.responseMessage)
        val body = res.body ?: error("فشل جلب الرصيد")
        session.updateBalance(body.balance)
        body
    }

    suspend fun transfer(receiverPhone: String, amount: Double, note: String): Result<TransferBody> = runCatching {
        val res = api.transfer(TransferRequest(receiverPhone, amount, note))
        if (res.responseCode != 0) error(res.responseMessage)
        val body = res.body ?: error("فشل التحويل")
        session.updateBalance(body.newBalance)
        body
    }

    suspend fun generateVoucher(amount: Double): Result<VoucherBody> = runCatching {
        val res = api.generateVoucher(GenerateVoucherRequest(amount))
        if (res.responseCode != 0) error(res.responseMessage)
        val body = res.body ?: error("فشل إنشاء القسيمة")
        session.updateBalance(body.newBalance)
        body
    }

    suspend fun getTransactions(page: Int = 1): Result<TransactionsBody> = runCatching {
        val res = api.getTransactions(page)
        if (res.responseCode != 0) error(res.responseMessage)
        res.body ?: error("فشل جلب المعاملات")
    }

    // ─── Merchant ─────────────────────────────────────────

    suspend fun cashout(
        agentWallet: String,
        password: String,
        token: String,
        voucher: String
    ): Result<CashoutBody> = runCatching {
        val res = api.cashout(CashoutRequest(agentWallet, password, token, voucher))
        if (res.responseCode != 0) error(res.responseMessage)
        res.body ?: error("فشلت عملية الدفع")
    }

    suspend fun getMerchantTransactions(page: Int = 1): Result<TransactionsBody> = runCatching {
        val res = api.getMerchantTransactions(page)
        if (res.responseCode != 0) error(res.responseMessage)
        res.body ?: error("فشل جلب معاملات التاجر")
    }
}
