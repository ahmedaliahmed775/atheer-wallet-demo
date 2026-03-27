package com.fintech.app.utils

object Validators {

    fun isValidPhone(phone: String): Boolean {
        val cleaned = phone.trim()
        // التعبير النمطي يقبل الأرقام اليمنية: 9 أرقام تبدأ بـ 7 (مثل: 77, 73, 71, 70, 78, 79)
        return cleaned.matches(Regex("^7[013789][0-9]{7}$"))
    }

    fun isValidPIN(pin: String): Boolean = pin.length == 6 && pin.all { it.isDigit() }

    fun isValidName(name: String): Boolean = name.trim().length >= 3

    fun isValidAmount(amount: String): Boolean {
        val value = amount.toDoubleOrNull() ?: return false
        return value > 0
    }

    fun phoneErrorMessage(phone: String, isArabic: Boolean): String? {
        if (phone.isEmpty()) return null
        return if (!isValidPhone(phone)) {
            if (isArabic) "أدخل رقم هاتف يمني صحيح (مثال: 77XXXXXXX)"
            else "Enter a valid Yemeni phone number (e.g., 77XXXXXXX)"
        } else null
    }

    fun pinErrorMessage(pin: String, isArabic: Boolean): String? {
        if (pin.isEmpty()) return null
        return if (!isValidPIN(pin)) {
            if (isArabic) "الرمز السري يجب أن يكون 6 أرقام"
            else "PIN must be 6 digits"
        } else null
    }
}

fun Double.formatAmount(): String = "%.2f".format(this)

fun String.toSafeDouble(): Double = this.toDoubleOrNull() ?: 0.0