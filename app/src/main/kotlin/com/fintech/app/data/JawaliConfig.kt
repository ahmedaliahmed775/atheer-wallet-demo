package com.fintech.app.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * إعدادات بوابة جوالي — مطابقة لـ JawaliConfig في @alsharie/jawalijs + config/jawali.php
 *
 * مرجع JS SDK:
 *   export interface JawaliConfig {
 *     auth: { phone, username, password, org_id, user_id, external_user, wallet, wallet_identifier, wallet_password },
 *     url: { base, disableSslVerification }
 *   }
 *
 * مرجع PHP SDK (.env):
 *   JAWALI_BASE_URL, JAWALI_MERCHANT_USERNAME, JAWALI_MERCHANT_PASSWORD,
 *   JAWALI_MERCHANT_WALLET, JAWALI_MERCHANT_WALLET_PASSWORD,
 *   JAWALI_MERCHANT_ORG_ID, JAWALI_MERCHANT_USER_ID, JAWALI_MERCHANT_EXTERNAL_USER
 */
private val Context.jawaliDataStore by preferencesDataStore(name = "jawali_config")

@Singleton
class JawaliConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // ─── مفاتيح التخزين ───────────────────────────────
        private val KEY_BASE_URL          = stringPreferencesKey("base_url")
        private val KEY_DISABLE_SSL       = booleanPreferencesKey("disable_ssl")
        private val KEY_PHONE             = stringPreferencesKey("phone")
        private val KEY_USERNAME          = stringPreferencesKey("username")
        private val KEY_PASSWORD          = stringPreferencesKey("password")
        private val KEY_ORG_ID            = stringPreferencesKey("org_id")
        private val KEY_USER_ID           = stringPreferencesKey("user_id")
        private val KEY_EXTERNAL_USER     = stringPreferencesKey("external_user")
        private val KEY_WALLET            = stringPreferencesKey("wallet")
        private val KEY_WALLET_IDENTIFIER = stringPreferencesKey("wallet_identifier")
        private val KEY_WALLET_PASSWORD   = stringPreferencesKey("wallet_password")
        private val KEY_TIMEOUT           = intPreferencesKey("timeout")
        private val KEY_RETRY_ENABLED     = booleanPreferencesKey("retry_enabled")
        private val KEY_MAX_RETRY         = intPreferencesKey("max_retry")

        // ─── قيم افتراضية — مطابقة لـ .env.example في السيرفر ──
        const val DEFAULT_BASE_URL          = "https://82.114.179.89:9493/paygate"
        const val DEFAULT_DISABLE_SSL       = true  // للتطوير فقط
        const val DEFAULT_PHONE             = ""
        const val DEFAULT_USERNAME          = "atheer_merchant"
        const val DEFAULT_PASSWORD          = "atheer_pass_123"
        const val DEFAULT_ORG_ID            = "atheer-org-001"
        const val DEFAULT_USER_ID           = "atheer.api.user"
        const val DEFAULT_EXTERNAL_USER     = "atheer_ext_1"
        const val DEFAULT_WALLET            = "777000001"       // auth.wallet — مطابق لـ JS SDK
        const val DEFAULT_WALLET_IDENTIFIER = "777000001"       // auth.wallet_identifier — مطابق لـ PHP SDK
        const val DEFAULT_WALLET_PASSWORD   = "wallet_pass_123"
        const val DEFAULT_TIMEOUT           = 30
        const val DEFAULT_RETRY_ENABLED     = true
        const val DEFAULT_MAX_RETRY         = 2

        // ─── ثوابت Jawali — مطابقة لـ JawaliService.php constants ──
        const val LOGIN_ENDPOINT          = "/oauth/token"
        const val CALLWS_ENDPOINT         = "/v1/ws/callWS"
        const val LOGIN_CLIENT_ID         = "restapp"
        const val LOGIN_CLIENT_SECRET     = "restapp"
        const val LOGIN_GRANT_TYPE        = "password"
        const val LOGIN_SCOPE             = "read"
        const val COMMON_SIGNON_CLIENT_ID = "WeCash"
        const val COMMON_BODY_TYPE        = "Clear"

        const val WALLET_AUTH_SERVICE = "PAYWA.WALLETAUTHENTICATION"
        const val WALLET_AUTH_DOMAIN  = "WalletDomain"
        const val INQUIRY_SERVICE     = "PAYAG.ECOMMERCEINQUIRY"
        const val INQUIRY_DOMAIN      = "MerchantDomain"
        const val CASHOUT_SERVICE     = "PAYAG.ECOMMCASHOUT"
        const val CASHOUT_DOMAIN      = "MerchantDomain"

        // Token expiry buffer — مطابق لـ TokenManager.php ($tokenExpiryBuffer = 300)
        const val TOKEN_EXPIRY_BUFFER_SECONDS = 300 // 5 دقائق
    }

    // ─── قراءة الإعدادات ────────────────────────────────────

    val baseUrl: Flow<String> = context.jawaliDataStore.data.map {
        it[KEY_BASE_URL] ?: DEFAULT_BASE_URL
    }

    val disableSsl: Flow<Boolean> = context.jawaliDataStore.data.map {
        it[KEY_DISABLE_SSL] ?: DEFAULT_DISABLE_SSL
    }

    val phone: Flow<String> = context.jawaliDataStore.data.map {
        it[KEY_PHONE] ?: DEFAULT_PHONE
    }

    val username: Flow<String> = context.jawaliDataStore.data.map {
        it[KEY_USERNAME] ?: DEFAULT_USERNAME
    }

    val password: Flow<String> = context.jawaliDataStore.data.map {
        it[KEY_PASSWORD] ?: DEFAULT_PASSWORD
    }

    val orgId: Flow<String> = context.jawaliDataStore.data.map {
        it[KEY_ORG_ID] ?: DEFAULT_ORG_ID
    }

    val userId: Flow<String> = context.jawaliDataStore.data.map {
        it[KEY_USER_ID] ?: DEFAULT_USER_ID
    }

    val externalUser: Flow<String> = context.jawaliDataStore.data.map {
        it[KEY_EXTERNAL_USER] ?: DEFAULT_EXTERNAL_USER
    }

    val wallet: Flow<String> = context.jawaliDataStore.data.map {
        it[KEY_WALLET] ?: DEFAULT_WALLET
    }

    val walletIdentifier: Flow<String> = context.jawaliDataStore.data.map {
        it[KEY_WALLET_IDENTIFIER] ?: DEFAULT_WALLET_IDENTIFIER
    }

    val walletPassword: Flow<String> = context.jawaliDataStore.data.map {
        it[KEY_WALLET_PASSWORD] ?: DEFAULT_WALLET_PASSWORD
    }

    val timeout: Flow<Int> = context.jawaliDataStore.data.map {
        it[KEY_TIMEOUT] ?: DEFAULT_TIMEOUT
    }

    val retryEnabled: Flow<Boolean> = context.jawaliDataStore.data.map {
        it[KEY_RETRY_ENABLED] ?: DEFAULT_RETRY_ENABLED
    }

    val maxRetry: Flow<Int> = context.jawaliDataStore.data.map {
        it[KEY_MAX_RETRY] ?: DEFAULT_MAX_RETRY
    }

    // ─── حفظ الإعدادات ──────────────────────────────────────

    suspend fun save(
        baseUrl: String          = DEFAULT_BASE_URL,
        disableSsl: Boolean      = DEFAULT_DISABLE_SSL,
        phone: String            = DEFAULT_PHONE,
        username: String         = DEFAULT_USERNAME,
        password: String         = DEFAULT_PASSWORD,
        orgId: String            = DEFAULT_ORG_ID,
        userId: String           = DEFAULT_USER_ID,
        externalUser: String     = DEFAULT_EXTERNAL_USER,
        wallet: String           = DEFAULT_WALLET,
        walletIdentifier: String = DEFAULT_WALLET_IDENTIFIER,
        walletPassword: String   = DEFAULT_WALLET_PASSWORD,
        timeout: Int             = DEFAULT_TIMEOUT,
        retryEnabled: Boolean    = DEFAULT_RETRY_ENABLED,
        maxRetry: Int            = DEFAULT_MAX_RETRY,
    ) {
        context.jawaliDataStore.edit { prefs ->
            prefs[KEY_BASE_URL]          = baseUrl
            prefs[KEY_DISABLE_SSL]       = disableSsl
            prefs[KEY_PHONE]             = phone
            prefs[KEY_USERNAME]          = username
            prefs[KEY_PASSWORD]          = password
            prefs[KEY_ORG_ID]            = orgId
            prefs[KEY_USER_ID]           = userId
            prefs[KEY_EXTERNAL_USER]     = externalUser
            prefs[KEY_WALLET]            = wallet
            prefs[KEY_WALLET_IDENTIFIER] = walletIdentifier
            prefs[KEY_WALLET_PASSWORD]   = walletPassword
            prefs[KEY_TIMEOUT]           = timeout
            prefs[KEY_RETRY_ENABLED]     = retryEnabled
            prefs[KEY_MAX_RETRY]         = maxRetry
        }
    }

    suspend fun resetToDefaults() {
        context.jawaliDataStore.edit { it.clear() }
    }
}
