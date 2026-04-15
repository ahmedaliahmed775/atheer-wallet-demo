package com.fintech.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintech.app.data.SessionManager
import com.fintech.app.data.WalletRepository
import com.fintech.app.model.AppUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinTechViewModel @Inject constructor(
    private val repo: WalletRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                session.isLoggedIn,
                session.token,
                session.name,
                session.phone,
                session.role,
                session.balance,
                session.userId
            ) { arr ->
                val loggedIn = arr[0] as Boolean
                val token    = arr[1] as? String ?: ""
                val name     = arr[2] as? String ?: ""
                val phone    = arr[3] as? String ?: ""
                val role     = arr[4] as? String ?: "customer"
                val balance  = arr[5] as? Double ?: 0.0
                val userId   = arr[6] as? Int ?: 0
                AppUiState(
                    isLoggedIn = loggedIn,
                    token      = token,
                    userName   = name,
                    userPhone  = phone,
                    userRole   = role,
                    balance    = balance,
                    userId     = userId
                )
            }.collect { restored ->
                _uiState.update { restored }
                if (restored.isLoggedIn) {
                    refreshBalance()
                    loadServices()
                }
            }
        }
    }

    // ─── Auth ─────────────────────────────────────────────

    fun login(phone: String, password: String) {
        if (phone.isBlank() || password.isBlank()) {
            setError("يرجى إدخال رقم الهاتف وكلمة المرور")
            return
        }
        viewModelScope.launch {
            setLoading(true)
            repo.login(phone.trim(), password.trim())
                .onSuccess { body ->
                    _uiState.update {
                        it.copy(
                            isLoading  = false,
                            isLoggedIn = true,
                            token      = body.accessToken,
                            userId     = body.user.id,
                            userName   = body.user.name,
                            userPhone  = body.user.phone,
                            userRole   = body.user.role,
                            balance    = body.user.balance,
                            error      = null
                        )
                    }
                    loadTransactions()
                    loadServices()
                }
                .onFailure { setError(it.message ?: "فشل تسجيل الدخول") }
        }
    }

    fun signup(name: String, phone: String, password: String, confirmPassword: String, role: String) {
        if (name.isBlank() || phone.isBlank() || password.isBlank()) {
            setError("يرجى تعبئة جميع الحقول"); return
        }
        if (password != confirmPassword) {
            setError("كلمتا المرور غير متطابقتين"); return
        }
        if (password.length < 6) {
            setError("كلمة المرور 6 أحرف على الأقل"); return
        }
        viewModelScope.launch {
            setLoading(true)
            repo.signup(name.trim(), phone.trim(), password, role)
                .onSuccess { body ->
                    _uiState.update {
                        it.copy(
                            isLoading  = false,
                            isLoggedIn = true,
                            token      = body.accessToken,
                            userId     = body.user.id,
                            userName   = body.user.name,
                            userPhone  = body.user.phone,
                            userRole   = body.user.role,
                            balance    = body.user.balance,
                            error      = null
                        )
                    }
                    loadServices()
                }
                .onFailure { setError(it.message ?: "فشل إنشاء الحساب") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _uiState.value = AppUiState()
        }
    }

    // ─── Wallet ───────────────────────────────────────────

    fun refreshBalance() {
        viewModelScope.launch {
            repo.getBalance()
                .onSuccess { body ->
                    _uiState.update { it.copy(balance = body.balance, userName = body.name) }
                }
                .onFailure { /* silent refresh failure */ }
        }
    }

    fun transfer(receiverPhone: String, amount: Double, note: String) {
        if (receiverPhone.isBlank()) { setError("يرجى إدخال رقم المستلم"); return }
        if (amount <= 0)             { setError("يرجى إدخال مبلغ صحيح");    return }
        if (amount > _uiState.value.balance) { setError("رصيدك غير كافٍ"); return }

        viewModelScope.launch {
            setLoading(true)
            repo.transfer(receiverPhone.trim(), amount, note)
                .onSuccess { body ->
                    _uiState.update {
                        it.copy(
                            isLoading      = false,
                            balance        = body.newBalance,
                            lastTransfer   = body,
                            successMessage = "تم تحويل ${body.amount.toLong()} ﷼ بنجاح",
                            error          = null
                        )
                    }
                    loadTransactions()
                }
                .onFailure { setError(it.message ?: "فشل التحويل") }
        }
    }

    fun transferExternal(recipientPhone: String, recipientName: String, amount: Double, note: String) {
        if (recipientPhone.isBlank()) { setError("يرجى إدخال رقم المستلم"); return }
        if (amount <= 0)              { setError("يرجى إدخال مبلغ صحيح");    return }
        if (amount > _uiState.value.balance) { setError("رصيدك غير كافٍ"); return }

        viewModelScope.launch {
            setLoading(true)
            repo.transferExternal(recipientPhone.trim(), recipientName, amount, note)
                .onSuccess { body ->
                    _uiState.update {
                        it.copy(
                            isLoading           = false,
                            balance             = body.newBalance,
                            lastExternalTransfer = body,
                            successMessage      = "تم إرسال ${body.amount.toLong()} ﷼ — كود السحب: ${body.withdrawalCode}",
                            error               = null
                        )
                    }
                    loadTransactions()
                }
                .onFailure { setError(it.message ?: "فشل إرسال الحوالة") }
        }
    }
                .onFailure { setError(it.message ?: "فشل إنشاء القسيمة") }
        }
    }

    fun payBill(category: String, provider: String, accountNumber: String, amount: Double) {
        if (accountNumber.isBlank()) { setError("يرجى إدخال رقم الحساب"); return }
        if (amount <= 0)             { setError("يرجى إدخال مبلغ صحيح");  return }
        if (amount > _uiState.value.balance) { setError("رصيدك غير كافٍ"); return }

        viewModelScope.launch {
            setLoading(true)
            repo.payBill(category, provider, accountNumber, amount)
                .onSuccess { body ->
                    _uiState.update {
                        it.copy(
                            isLoading       = false,
                            balance         = body.newBalance,
                            lastBillPayment = body,
                            successMessage  = "تم سداد ${body.amount.toLong()} ﷼ بنجاح",
                            error           = null
                        )
                    }
                    loadTransactions()
                }
                .onFailure { setError(it.message ?: "فشل سداد الفاتورة") }
        }
    }

    fun qrPay(posNumber: String, amount: Double, note: String = "") {
        if (merchantPhone.isBlank()) { setError("يرجى إدخال رقم التاجر"); return }
        if (amount <= 0)             { setError("يرجى إدخال مبلغ صحيح");  return }
        if (amount > _uiState.value.balance) { setError("رصيدك غير كافٍ"); return }

        viewModelScope.launch {
            setLoading(true)
            repo.qrPay(posNumber.trim(), amount, note)
                .onSuccess { body ->
                    _uiState.update {
                        it.copy(
                            isLoading      = false,
                            balance        = body.newBalance,
                            lastQrPay      = body,
                            successMessage = "تم الدفع ${body.amount.toLong()} ﷼ لـ ${body.merchantName}",
                            error          = null
                        )
                    }
                    loadTransactions()
                }
                .onFailure { setError(it.message ?: "فشل الدفع") }
        }
    }

    fun generateCashout(amount: Double) {
        if (amount <= 0)                     { setError("يرجى إدخال مبلغ صحيح"); return }
        if (amount > _uiState.value.balance) { setError("رصيدك غير كافٍ");       return }

        viewModelScope.launch {
            setLoading(true)
            repo.generateCashout(amount)
                .onSuccess { body ->
                    _uiState.update {
                        it.copy(
                            isLoading      = false,
                            balance        = body.newBalance,
                            lastCashOut    = body,
                            successMessage = "كود السحب: ${body.code}",
                            error          = null
                        )
                    }
                }
                .onFailure { setError(it.message ?: "فشل إنشاء كود السحب") }
        }
    }

    fun cashIn(amount: Double) {
        if (amount <= 0) { setError("يرجى إدخال مبلغ صحيح"); return }

        viewModelScope.launch {
            setLoading(true)
            repo.cashIn(amount)
                .onSuccess { body ->
                    _uiState.update {
                        it.copy(
                            isLoading      = false,
                            balance        = body.newBalance,
                            lastCashIn     = body,
                            successMessage = "تم إيداع ${body.amount.toLong()} ﷼ بنجاح",
                            error          = null
                        )
                    }
                    loadTransactions()
                }
                .onFailure { setError(it.message ?: "فشل الإيداع") }
        }
    }

    // ─── Transactions ─────────────────────────────────────

    fun loadTransactions(page: Int = 1) {
        viewModelScope.launch {
            val result = if (_uiState.value.userRole == "merchant")
                repo.getMerchantTransactions(page)
            else
                repo.getTransactions(page)

            result.onSuccess { body ->
                val txns = if (page == 1) body.transactions
                           else _uiState.value.transactions + body.transactions
                _uiState.update { it.copy(transactions = txns) }
            }
        }
    }

    fun loadTransactionDetail(id: String) {
        viewModelScope.launch {
            repo.getTransactionDetail(id)
                .onSuccess { detail ->
                    _uiState.update { it.copy(transactionDetail = detail) }
                }
        }
    }

    // ─── Services ─────────────────────────────────────────

    fun loadServices() {
        viewModelScope.launch {
            repo.getServices()
                .onSuccess { services ->
                    _uiState.update { it.copy(services = services) }
                }
        }
    }

    // ─── Merchant ─────────────────────────────────────────

    // ─── UI Helpers ───────────────────────────────────────

    fun clearError()   = _uiState.update { it.copy(error = null) }
    fun clearSuccess() = _uiState.update {
        it.copy(
            successMessage       = null,
            lastVoucher          = null,
            lastTransfer         = null,
            lastCashout          = null,
            lastBillPayment      = null,
            lastQrPay            = null,
            lastCashOut          = null,
            lastCashIn           = null,
            lastExternalTransfer = null,
            transactionDetail    = null
        )
    }

    private fun setLoading(v: Boolean) = _uiState.update { it.copy(isLoading = v, error = null) }
    private fun setError(msg: String)  = _uiState.update { it.copy(isLoading = false, error = msg) }
}
