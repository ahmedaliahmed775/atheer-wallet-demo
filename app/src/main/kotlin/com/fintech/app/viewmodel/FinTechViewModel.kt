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
                if (restored.isLoggedIn) refreshBalance()
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
                            successMessage = "تم تحويل ${body.amount} ﷼ بنجاح",
                            error          = null
                        )
                    }
                    loadTransactions()
                }
                .onFailure { setError(it.message ?: "فشل التحويل") }
        }
    }

    fun generateVoucher(amount: Double) {
        if (amount <= 0)                     { setError("يرجى إدخال مبلغ صحيح"); return }
        if (amount > _uiState.value.balance) { setError("رصيدك غير كافٍ");       return }

        viewModelScope.launch {
            setLoading(true)
            repo.generateVoucher(amount)
                .onSuccess { body ->
                    _uiState.update {
                        it.copy(
                            isLoading      = false,
                            balance        = body.newBalance,
                            lastVoucher    = body,
                            successMessage = "تم إنشاء القسيمة: ${body.voucherCode}",
                            error          = null
                        )
                    }
                }
                .onFailure { setError(it.message ?: "فشل إنشاء القسيمة") }
        }
    }

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

    fun cashout(agentWallet: String, password: String, voucherCode: String) {
        if (agentWallet.isBlank() || password.isBlank() || voucherCode.isBlank()) {
            setError("يرجى إدخال جميع البيانات"); return
        }
        viewModelScope.launch {
            setLoading(true)
            val token = _uiState.value.token
            repo.cashout(agentWallet, password, token, voucherCode)
                .onSuccess { body ->
                    _uiState.update {
                        it.copy(
                            isLoading      = false,
                            lastCashout    = body,
                            successMessage = "تم استلام ${body.amount} ﷼ بنجاح",
                            error          = null
                        )
                    }
                    loadTransactions()
                }
                .onFailure { setError(it.message ?: "فشل عملية الدفع") }
        }
    }

    // ─── UI Helpers ───────────────────────────────────────

    fun clearError()   = _uiState.update { it.copy(error = null) }
    fun clearSuccess() = _uiState.update { it.copy(successMessage = null, lastVoucher = null, lastTransfer = null, lastCashout = null) }

    private fun setLoading(v: Boolean) = _uiState.update { it.copy(isLoading = v, error = null) }
    private fun setError(msg: String)  = _uiState.update { it.copy(isLoading = false, error = msg) }
}
