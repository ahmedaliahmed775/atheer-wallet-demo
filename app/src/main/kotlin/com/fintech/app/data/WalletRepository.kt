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
        val response = api.login(LoginRequest(phone, password))
        if (response.responseCode != 0) throw Exception(response.responseMessage)
        val body = response.body ?: throw Exception(response.responseMessage ?: "فشل تسجيل الدخول")
        session.save(
            token     = body.accessToken,
            userId    = body.user.id,
            name      = body.user.name,
            phone     = body.user.phone,
            role      = body.user.role,
            balance   = body.user.balance,
            posNumber = body.user.posNumber
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
        if (response.responseCode != 0) throw Exception(response.responseMessage)
        val body = response.body ?: throw Exception(response.responseMessage ?: "فشل إنشاء الحساب")
        session.save(
            token     = body.accessToken,
            userId    = body.user.id,
            name      = body.user.name,
            phone     = body.user.phone,
            role      = body.user.role,
            balance   = body.user.balance,
            posNumber = body.user.posNumber
        )
        body
    }

    suspend fun logout() {
        session.clear()
    }

    // ─── Wallet ───────────────────────────────────────────

    suspend fun getBalance(): Result<BalanceBody> = runCatching {
        val response = api.getBalance()
        val body = response.body ?: throw Exception(response.responseMessage ?: "فشل جلب الرصيد")
        session.updateBalance(body.balance)
        body
    }

    suspend fun transfer(
        receiverPhone: String,
        amount: Double,
        note: String
    ): Result<TransferBody> = runCatching {
        val response = api.transfer(TransferRequest(receiverPhone, amount, note))
        if (response.responseCode != 0) throw Exception(response.responseMessage ?: "فشل التحويل")
        val body = response.body ?: throw Exception(response.responseMessage ?: "فشل التحويل")
        session.updateBalance(body.newBalance)
        body
    }

    suspend fun transferExternal(
        recipientPhone: String,
        recipientName: String,
        amount: Double,
        note: String
    ): Result<ExternalTransferBody> = runCatching {
        val response = api.transferExternal(ExternalTransferRequest(recipientPhone, recipientName, amount, note))
        if (response.responseCode != 0) throw Exception(response.responseMessage ?: "فشل إرسال الحوالة")
        val body = response.body ?: throw Exception(response.responseMessage ?: "فشل إرسال الحوالة")
        session.updateBalance(body.newBalance)
        body
    }


    suspend fun payBill(
        category: String,
        provider: String,
        accountNumber: String,
        amount: Double
    ): Result<BillPaymentBody> = runCatching {
        val response = api.payBill(BillPaymentRequest(category, provider, accountNumber, amount))
        if (response.responseCode != 0) throw Exception(response.responseMessage ?: "فشل سداد الفاتورة")
        val body = response.body ?: throw Exception(response.responseMessage ?: "فشل سداد الفاتورة")
        session.updateBalance(body.newBalance)
        body
    }

    suspend fun qrPay(
        posNumber: String,
        amount: Double,
        note: String
    ): Result<QrPayBody> = runCatching {
        val response = api.qrPay(QrPayRequest(posNumber, amount, note))
        if (response.responseCode != 0) throw Exception(response.responseMessage ?: "فشل الدفع")
        val body = response.body ?: throw Exception(response.responseMessage ?: "فشل الدفع")
        session.updateBalance(body.newBalance)
        body
    }

    suspend fun generateCashout(amount: Double): Result<CashOutBody> = runCatching {
        val response = api.generateCashout(CashOutRequest(amount))
        if (response.responseCode != 0) throw Exception(response.responseMessage ?: "فشل إنشاء كود السحب")
        val body = response.body ?: throw Exception(response.responseMessage ?: "فشل إنشاء كود السحب")
        session.updateBalance(body.newBalance)
        body
    }

    suspend fun cashIn(amount: Double, agentCode: String = "DEMO"): Result<CashInBody> = runCatching {
        val response = api.cashIn(CashInRequest(amount, agentCode))
        if (response.responseCode != 0) throw Exception(response.responseMessage ?: "فشل الإيداع")
        val body = response.body ?: throw Exception(response.responseMessage ?: "فشل الإيداع")
        session.updateBalance(body.newBalance)
        body
    }

    // ─── Transactions ─────────────────────────────────────

    suspend fun getTransactions(page: Int): Result<TransactionsBody> = runCatching {
        val response = api.getTransactions(page)
        response.body ?: throw Exception(response.responseMessage ?: "فشل جلب المعاملات")
    }

    suspend fun getMerchantTransactions(page: Int): Result<TransactionsBody> = runCatching {
        val response = api.getMerchantTransactions(page)
        response.body ?: throw Exception(response.responseMessage ?: "فشل جلب المعاملات")
    }

    suspend fun getTransactionDetail(id: String): Result<TransactionDetailBody> = runCatching {
        val response = api.getTransactionDetail(id)
        response.body ?: throw Exception("فشل جلب تفاصيل المعاملة")
    }

    // ─── Services ─────────────────────────────────────────

    suspend fun getServices(): Result<List<ServiceCategory>> = runCatching {
        val response = api.getServices()
        response.body?.services ?: emptyList()
    }

    // ─── Merchant ─────────────────────────────────────────


    suspend fun getMerchantQrInfo(): Result<QrInfoBody> = runCatching {
        val response = api.getMerchantQrInfo()
        response.body ?: throw Exception("فشل جلب بيانات QR")
    }
}
