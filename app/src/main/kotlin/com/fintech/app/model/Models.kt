package com.fintech.app.model

import java.time.LocalDateTime
import java.util.UUID

// ─── User Models ─────────────────────────────────────────────────────────────

enum class UserRole { CUSTOMER, MERCHANT }

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val pinHash: String,
    val role: UserRole,
    val balance: Double = 0.0,
    val biometricEnabled: Boolean = false,
    val qrCode: String = id,
    val storeName: String? = null
)

data class UserData(
    val name: String,
    val phone: String,
    val pin: String,
    val role: UserRole,
    val storeName: String? = null
)

// ─── Transaction Models ───────────────────────────────────────────────────────

enum class TransactionType {
    TRANSFER, BILL_PAYMENT, QR_PAYMENT, RECEIVED
}

enum class TransactionStatus {
    PENDING, SUCCESS, FAILED
}

enum class BillService(val labelAr: String, val labelEn: String) {
    PHONE("سداد الهاتف", "Phone Bill"),
    ELECTRICITY("سداد الكهرباء", "Electricity"),
    INTERNET("سداد الإنترنت", "Internet")
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType,
    val amount: Double,
    val recipient: String,
    val recipientName: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val status: TransactionStatus = TransactionStatus.SUCCESS,
    val description: String = "",
    val referenceNumber: String = "TXN-${System.currentTimeMillis()}"
)

// ─── App State ────────────────────────────────────────────────────────────────

enum class Language { ARABIC, ENGLISH }

data class AppState(
    val currentUser: User? = null,
    val transactions: List<Transaction> = emptyList(),
    val language: Language = Language.ARABIC,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
