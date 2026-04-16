package com.fintech.app.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "wallet_session")

@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val KEY_TOKEN      = stringPreferencesKey("token")
        private val KEY_USER_ID    = intPreferencesKey("user_id")
        private val KEY_NAME       = stringPreferencesKey("name")
        private val KEY_PHONE      = stringPreferencesKey("phone")
        private val KEY_ROLE       = stringPreferencesKey("role")
        private val KEY_BALANCE    = doublePreferencesKey("balance")
        private val KEY_POS_NUMBER = stringPreferencesKey("pos_number")
    }

    val token:     Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val userId:    Flow<Int>     = context.dataStore.data.map { it[KEY_USER_ID] ?: 0 }
    val name:      Flow<String>  = context.dataStore.data.map { it[KEY_NAME] ?: "" }
    val phone:     Flow<String>  = context.dataStore.data.map { it[KEY_PHONE] ?: "" }
    val role:      Flow<String>  = context.dataStore.data.map { it[KEY_ROLE] ?: "customer" }
    val balance:   Flow<Double>  = context.dataStore.data.map { it[KEY_BALANCE] ?: 0.0 }
    val posNumber: Flow<String?> = context.dataStore.data.map { it[KEY_POS_NUMBER] }

    val isLoggedIn: Flow<Boolean> = token.map { !it.isNullOrBlank() }

    suspend fun save(
        token: String,
        userId: Int,
        name: String,
        phone: String,
        role: String,
        balance: Double,
        posNumber: String? = null
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN]   = token
            prefs[KEY_USER_ID] = userId
            prefs[KEY_NAME]    = name
            prefs[KEY_PHONE]   = phone
            prefs[KEY_ROLE]    = role
            prefs[KEY_BALANCE] = balance
            if (posNumber != null) prefs[KEY_POS_NUMBER] = posNumber
            else prefs.remove(KEY_POS_NUMBER)
        }
    }

    suspend fun updateBalance(newBalance: Double) {
        context.dataStore.edit { it[KEY_BALANCE] = newBalance }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
