# Atheer Wallet — تطبيق Android

تطبيق محفظة إلكترونية مبني بـ Kotlin + Jetpack Compose + Hilt

## المتطلبات

- Android Studio Hedgehog أو أحدث
- JDK 17
- Android SDK 35
- Min SDK: 26 (Android 8.0)

## الإعداد

### عنوان السيرفر

يتم تحديده في `app/build.gradle.kts`:

```kotlin
// Debug — يشير إلى localhost عبر المحاكي
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:3000/\"")

// Release — ضع عنوان الإنتاج
buildConfigField("String", "BASE_URL", "\"https://your-server.com/\"")
```

### البناء

```bash
./gradlew assembleDebug
```

APK الناتج: `app/build/outputs/apk/debug/app-debug.apk`

## البنية

```
app/src/main/kotlin/com/fintech/app/
├── App.kt                          # Application class (Hilt)
├── MainActivity.kt                 # نقطة الدخول
├── data/
│   ├── SessionManager.kt           # تخزين الجلسة (DataStore)
│   └── WalletRepository.kt         # طبقة البيانات
├── model/
│   └── Models.kt                   # Data classes + API DTOs
├── network/
│   ├── NetworkModule.kt            # Hilt DI (OkHttp + Retrofit)
│   └── WalletApiService.kt         # واجهات Retrofit
├── navigation/
│   └── AppNavigation.kt            # Navigation graph
├── ui/
│   ├── screens/
│   │   ├── SplashScreen.kt         # شاشة البداية
│   │   ├── LoginScreen.kt          # تسجيل الدخول
│   │   ├── SignupScreen.kt         # إنشاء حساب
│   │   ├── HomeScreen.kt           # الرئيسية (customer + merchant)
│   │   ├── TransferScreen.kt       # تحويل P2P
│   │   ├── ExternalTransferScreen.kt # حوالة خارجية
│   │   ├── BillPaymentScreen.kt    # سداد فواتير
│   │   ├── QrPayScreen.kt          # دفع لتاجر (POS number)
│   │   ├── QrDisplayScreen.kt      # عرض QR التاجر
│   │   ├── CashOutScreen.kt        # سحب نقدي (كود)
│   │   ├── CashInScreen.kt         # إيداع نقدي
│   │   ├── HistoryScreen.kt        # سجل المعاملات
│   │   ├── TransactionDetailScreen.kt # تفاصيل معاملة
│   │   ├── MerchantHomeScreen.kt   # لوحة التاجر
│   │   ├── SettingsScreen.kt       # الإعدادات
│   │   └── Components.kt           # مكونات مشتركة
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Typography.kt
├── utils/
│   ├── Constants.kt                # ثوابت (مرجعي فقط)
│   ├── NetworkInterceptor.kt
│   └── Validators.kt
└── viewmodel/
    └── FinTechViewModel.kt         # ViewModel الرئيسي
```

## الشاشات والتدفق

```
Splash → Login/Signup → Home
                          ├── تحويل P2P
                          ├── حوالة خارجية
                          ├── سداد فواتير
                          ├── دفع QR (إدخال POS number)
                          ├── سحب نقدي
                          ├── إيداع نقدي
                          ├── سجل المعاملات → تفاصيل معاملة
                          ├── QR التاجر (للتجار)
                          ├── استلام دفعة (للتجار)
                          └── الإعدادات → تسجيل خروج
```

## التكامل مع السيرفر

| شاشة التطبيق | API Endpoint |
|---|---|
| Login | `POST /api/v1/auth/login` |
| Signup | `POST /api/v1/auth/signup` |
| Home (رصيد) | `GET /api/v1/wallet/balance` |
| تحويل | `POST /api/v1/wallet/transfer` |
| حوالة خارجية | `POST /api/v1/wallet/transfer-external` |
| سداد فواتير | `POST /api/v1/wallet/pay-bill` |
| دفع QR | `POST /api/v1/wallet/qr-pay` |
| سحب نقدي | `POST /api/v1/wallet/generate-cashout` |
| إيداع | `POST /api/v1/wallet/cash-in` |
| سجل المعاملات | `GET /api/v1/wallet/transactions` |
| تفاصيل معاملة | `GET /api/v1/wallet/transactions/:id` |
| خدمات الفواتير | `GET /api/v1/wallet/services` |
| QR التاجر | `GET /api/v1/merchant/qr-info` |
| معاملات التاجر | `GET /api/v1/merchant/transactions` |

## التقنيات

- **UI**: Jetpack Compose + Material 3
- **Navigation**: Navigation Compose
- **DI**: Hilt (Dagger)
- **Network**: Retrofit 2 + OkHttp + Gson
- **Session**: DataStore Preferences
- **Architecture**: MVVM (ViewModel + Repository)
