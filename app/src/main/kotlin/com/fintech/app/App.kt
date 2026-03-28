package com.fintech.app

import android.app.Application
import com.fintech.app.network.RetrofitClient
import com.fintech.app.network.WalletRepository
import com.fintech.app.utils.SecurityManager

class App : Application() {

    lateinit var securityManager: SecurityManager
    lateinit var walletRepository: WalletRepository

    override fun onCreate() {
        super.onCreate()
        // 1. أولاً: تهيئة الـ instance ليتمكن أي كلاس آخر (مثل Retrofit) من رؤيته
        instance = this

        // 2. ثانياً: تهيئة باقي المكونات بأمان
        securityManager = SecurityManager(this)
        walletRepository = WalletRepository(RetrofitClient.walletService)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}