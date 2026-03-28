package com.fintech.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintech.app.App
import com.fintech.app.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class VoucherState(
    val code: String = "",
    val timeLeft: Long = 0, // in seconds
    val isExpired: Boolean = false,
    val isLoading: Boolean = false
)

class FinTechViewModel : ViewModel() {

    private val repository = App.instance.walletRepository
    private val securityManager = App.instance.securityManager

    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val _voucherState = MutableStateFlow(VoucherState())
    val voucherState: StateFlow<VoucherState> = _voucherState.asStateFlow()

    private var timerJob: Job? = null

    // ─── Auth Flow ────────────────────────────────────────────────────────────

    fun setLanguage(language: Language) {
        _appState.update { it.copy(language = language) }
    }

    fun login(username: String, pin: String) {
        viewModelScope.launch {
            _appState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val loginRequest = LoginRequest(
                username = username,
                password = pin
            )

            repository.login(loginRequest).onSuccess { response ->
                securityManager.saveToken(response.access_token)
                performWalletAuth(username, pin)
            }.onFailure { error ->
                _appState.update { it.copy(isLoading = false, errorMessage = "فشل تسجيل الدخول: ${error.message}") }
            }
        }
    }

    private fun performWalletAuth(identifier: String, pin: String) {
        viewModelScope.launch {
            val walletRequest = WalletAuthRequest(identifier = identifier, password = pin)
            
            repository.walletAuth(walletRequest).onSuccess { response ->
                securityManager.saveToken(response.access_token)
                securityManager.savePassword(pin)
                
                _appState.update {
                    it.copy(
                        currentUser = User(name = response.org_name ?: identifier, phone = identifier, role = UserRole.MERCHANT, pinHash = ""),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _appState.update { it.copy(isLoading = false, errorMessage = "فشل مصادقة المحفظة: ${error.message}") }
            }
        }
    }

    fun createAccount(userData: UserData) {
        // Since signup is not supported by the new server specs, 
        // we can either show a message or just log it for now.
        _appState.update { it.copy(errorMessage = "عملية إنشاء الحساب غير مدعومة حالياً من قبل السيرفر.") }
    }

    fun logout() {
        securityManager.clear()
        _appState.update { AppState() }
    }

    // ─── Voucher System ───────────────────────────────────────────────────────

    fun generateVoucher(amount: Double) {
        viewModelScope.launch {
            _voucherState.update { it.copy(isLoading = true) }
            repository.generateVoucher(amount).onSuccess { response ->
                _voucherState.update { 
                    it.copy(
                        code = response.voucherCode,
                        timeLeft = 300,
                        isExpired = false,
                        isLoading = false
                    )
                }
                startVoucherTimer()
            }.onFailure { error ->
                _voucherState.update { it.copy(isLoading = false) }
                _appState.update { it.copy(errorMessage = "فشل توليد القسيمة: ${error.message}") }
            }
        }
    }

    private fun startVoucherTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_voucherState.value.timeLeft > 0) {
                delay(1000)
                _voucherState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            _voucherState.update { it.copy(isExpired = true) }
        }
    }

    // ─── Merchant Operations ──────────────────────────────────────────────────

    fun processMerchantCashout(voucherCode: String, receiverMobile: String) {
        viewModelScope.launch {
            _appState.update { it.copy(isLoading = true) }
            val currentUser = _appState.value.currentUser ?: return@launch
            val password = securityManager.getPassword() ?: ""
            val token = securityManager.getToken() ?: ""

            val request = MerchantChargeRequest(
                agentWallet = currentUser.phone,
                voucher = voucherCode,
                receiverMobile = receiverMobile,
                password = password,
                accessToken = token,
                refId = UUID.randomUUID().toString(),
                purpose = "Cashout via Android App"
            )

            repository.merchantCashout(request).onSuccess { response ->
                _appState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "تمت العملية بنجاح. المرجع: ${response.refId ?: "N/A"}"
                    ) 
                }
            }.onFailure { error ->
                _appState.update { 
                    it.copy(isLoading = false, errorMessage = "فشلت العملية: ${error.message}")
                }
            }
        }
    }

    fun inquiryTransaction(refId: String) {
        viewModelScope.launch {
            _appState.update { it.copy(isLoading = true) }
            val currentUser = _appState.value.currentUser ?: return@launch
            val password = securityManager.getPassword() ?: ""
            val token = securityManager.getToken() ?: ""

            val request = InquiryRequest(
                agentWallet = currentUser.phone,
                password = password,
                accessToken = token,
                refId = refId
            )

            repository.inquiry(request).onSuccess { response ->
                _appState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "حالة المعاملة: ${response.state ?: "غير معروفة"}"
                    )
                }
            }.onFailure { error ->
                _appState.update { 
                    it.copy(isLoading = false, errorMessage = "فشل الاستعلام: ${error.message}")
                }
            }
        }
    }

    fun clearError() {
        _appState.update { it.copy(errorMessage = null) }
    }
}
