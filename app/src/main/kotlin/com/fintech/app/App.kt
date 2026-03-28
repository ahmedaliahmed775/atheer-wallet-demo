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
        securityManager = SecurityManager(this)
        walletRepository = WalletRepository(RetrofitClient.walletService)
        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
