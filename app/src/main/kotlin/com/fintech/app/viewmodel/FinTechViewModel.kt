package com.fintech.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintech.app.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class FinTechViewModel : ViewModel() {

    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    // Mock database of users
    private val mockUsers = mutableListOf(
        User(
            id = "user-001",
            name = "أحمد محمد",
            phone = "0501234567",
            pinHash = "1234",
            role = UserRole.CUSTOMER,
            balance = 5000.0,
            biometricEnabled = false
        ),
        User(
            id = "merchant-001",
            name = "متجر النور",
            phone = "0559876543",
            pinHash = "1234",
            role = UserRole.MERCHANT,
            balance = 15000.0,
            storeName = "متجر النور للإلكترونيات"
        )
    )

    private val mockTransactions = mutableListOf(
        Transaction(
            id = "txn-001",
            type = TransactionType.TRANSFER,
            amount = 250.0,
            recipient = "0509876543",
            recipientName = "سارة علي",
            date = LocalDateTime.now().minusHours(2),
            status = TransactionStatus.SUCCESS,
            description = "تحويل مالي"
        ),
        Transaction(
            id = "txn-002",
            type = TransactionType.BILL_PAYMENT,
            amount = 120.0,
            recipient = "STC",
            recipientName = "STC",
            date = LocalDateTime.now().minusDays(1),
            status = TransactionStatus.SUCCESS,
            description = "سداد فاتورة هاتف"
        ),
        Transaction(
            id = "txn-003",
            type = TransactionType.RECEIVED,
            amount = 500.0,
            recipient = "0501112233",
            recipientName = "محمد خالد",
            date = LocalDateTime.now().minusDays(2),
            status = TransactionStatus.SUCCESS,
            description = "استلام تحويل"
        )
    )

    // ─── Auth ─────────────────────────────────────────────────────────────────

    fun login(phone: String, pin: String) {
        viewModelScope.launch {
            _appState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1200) // Simulate network call
            val user = mockUsers.find { it.phone == phone && it.pinHash == pin }
            if (user != null) {
                val userTransactions = mockTransactions.filter {
                    it.recipient == phone || it.recipientName.contains(user.name)
                }
                _appState.update {
                    it.copy(
                        currentUser = user,
                        transactions = mockTransactions.toList(),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                _appState.update {
                    it.copy(isLoading = false, errorMessage = "رقم الهاتف أو الرمز السري غير صحيح")
                }
            }
        }
    }

    fun createAccount(userData: UserData) {
        viewModelScope.launch {
            _appState.update { it.copy(isLoading = true) }
            delay(1500)
            val newUser = User(
                name = userData.name,
                phone = userData.phone,
                pinHash = userData.pin,
                role = userData.role,
                balance = 0.0,
                storeName = userData.storeName
            )
            mockUsers.add(newUser)
            _appState.update {
                it.copy(
                    currentUser = newUser,
                    transactions = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun logout() {
        _appState.update { AppState() }
    }

    // ─── Transactions ─────────────────────────────────────────────────────────

    fun transferMoney(recipientPhone: String, amount: Double) {
        viewModelScope.launch {
            _appState.update { it.copy(isLoading = true) }
            delay(1500)
            val currentUser = _appState.value.currentUser ?: return@launch
            if (currentUser.balance < amount) {
                _appState.update {
                    it.copy(isLoading = false, errorMessage = "الرصيد غير كافٍ")
                }
                return@launch
            }
            val recipientUser = mockUsers.find { it.phone == recipientPhone }
            val newTxn = Transaction(
                type = TransactionType.TRANSFER,
                amount = amount,
                recipient = recipientPhone,
                recipientName = recipientUser?.name ?: recipientPhone,
                description = "تحويل مالي"
            )
            val updatedUser = currentUser.copy(balance = currentUser.balance - amount)
            mockTransactions.add(0, newTxn)
            _appState.update {
                it.copy(
                    currentUser = updatedUser,
                    transactions = mockTransactions.toList(),
                    isLoading = false
                )
            }
        }
    }

    fun payBill(service: BillService, amount: Double) {
        viewModelScope.launch {
            _appState.update { it.copy(isLoading = true) }
            delay(1500)
            val currentUser = _appState.value.currentUser ?: return@launch
            if (currentUser.balance < amount) {
                _appState.update {
                    it.copy(isLoading = false, errorMessage = "الرصيد غير كافٍ")
                }
                return@launch
            }
            val newTxn = Transaction(
                type = TransactionType.BILL_PAYMENT,
                amount = amount,
                recipient = service.name,
                recipientName = service.labelAr,
                description = "سداد ${service.labelAr}"
            )
            val updatedUser = currentUser.copy(balance = currentUser.balance - amount)
            mockTransactions.add(0, newTxn)
            _appState.update {
                it.copy(
                    currentUser = updatedUser,
                    transactions = mockTransactions.toList(),
                    isLoading = false
                )
            }
        }
    }

    fun collectPayment(amount: Double) {
        viewModelScope.launch {
            _appState.update { it.copy(isLoading = true) }
            delay(1000)
            val currentUser = _appState.value.currentUser ?: return@launch
            val newTxn = Transaction(
                type = TransactionType.QR_PAYMENT,
                amount = amount,
                recipient = "QR",
                recipientName = "تحصيل QR",
                description = "تحصيل عبر QR"
            )
            val updatedUser = currentUser.copy(balance = currentUser.balance + amount)
            mockTransactions.add(0, newTxn)
            _appState.update {
                it.copy(
                    currentUser = updatedUser,
                    transactions = mockTransactions.toList(),
                    isLoading = false
                )
            }
        }
    }

    // ─── Settings ─────────────────────────────────────────────────────────────

    fun toggleBiometric(enabled: Boolean) {
        _appState.update {
            it.copy(currentUser = it.currentUser?.copy(biometricEnabled = enabled))
        }
    }

    fun changePIN(newPin: String) {
        _appState.update {
            it.copy(currentUser = it.currentUser?.copy(pinHash = newPin))
        }
    }

    fun setLanguage(language: Language) {
        _appState.update { it.copy(language = language) }
    }

    fun clearError() {
        _appState.update { it.copy(errorMessage = null) }
    }
}
