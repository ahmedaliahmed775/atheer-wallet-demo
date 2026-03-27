# 💳 FinTech Pay — تطبيق التحويلات المالية

<div align="center">

![Android](https://img.shields.io/badge/Android-API%2026+-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024-orange)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-purple)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

**تطبيق محفظة رقمية احترافي مبني بـ Kotlin + Jetpack Compose**

</div>

---

## 📱 نظرة عامة

تطبيق **FinTech Pay** هو محفظة رقمية متكاملة تحاكي تطبيقات مثل STC Pay وجوالي، يدعم:
- ✅ تسجيل دخول عميل / تاجر
- ✅ تحويل الأموال بين المستخدمين
- ✅ سداد الفواتير (هاتف، كهرباء، إنترنت)
- ✅ تحصيل المدفوعات عبر QR (للتجار)
- ✅ سجل العمليات الكامل
- ✅ دعم اللغتين العربية والإنجليزية (RTL/LTR)
- ✅ رسوم متحركة احترافية

---

## 🏗️ التقنيات المستخدمة

| التقنية | الاستخدام |
|---|---|
| **Kotlin** | لغة البرمجة الأساسية |
| **Jetpack Compose** | واجهة المستخدم |
| **MVVM + StateFlow** | إدارة الحالة |
| **Navigation Compose** | التنقل بين الشاشات |
| **Material Design 3** | نظام التصميم |
| **Coroutines** | العمليات غير المتزامنة |
| **GitHub Actions** | CI/CD |

---

## 📂 هيكل المشروع

```
FinTechApp/
├── app/src/main/kotlin/com/fintech/app/
│   ├── MainActivity.kt
│   ├── App.kt
│   ├── model/
│   │   └── Models.kt          # User, Transaction, AppState
│   ├── viewmodel/
│   │   └── FinTechViewModel.kt # MVVM ViewModel
│   ├── navigation/
│   │   └── AppNavigation.kt   # Navigation Graph
│   ├── ui/
│   │   ├── theme/
│   │   │   ├── Color.kt
│   │   │   ├── Typography.kt
│   │   │   └── Theme.kt
│   │   ├── screens/
│   │   │   ├── SplashScreen.kt         # تسجيل الدخول
│   │   │   ├── SignupScreens.kt        # إنشاء الحساب
│   │   │   ├── CustomerHomeScreen.kt   # الرئيسية (عميل)
│   │   │   ├── MerchantHomeScreen.kt   # المحطة (تاجر)
│   │   │   └── OtherScreens.kt        # السجل + الإعدادات
│   │   └── components/
│   │       └── Modals.kt              # TransferModal, BillModal, Success
│   └── utils/
│       └── Validators.kt
└── .github/workflows/android.yml     # CI/CD
```

---

## 🚀 خطوات التشغيل

### المتطلبات:
- Android Studio Hedgehog أو أحدث
- JDK 17
- Android SDK API 26+

### خطوات التثبيت:

```bash
# 1. استنساخ المشروع
git clone https://github.com/YOUR_USERNAME/FinTechApp.git

# 2. فتح المشروع في Android Studio
cd FinTechApp

# 3. مزامنة Gradle
./gradlew build

# 4. تشغيل على المحاكي أو الجهاز
./gradlew installDebug
```

---

## 🔐 بيانات الدخول التجريبية

| النوع | رقم الهاتف | الرمز السري |
|---|---|---|
| عميل | `0501234567` | `123456` |
| تاجر | `0559876543` | `123456` |

---

## 🎨 لقطات الشاشة

| تسجيل الدخول | الرئيسية (عميل) | المحطة (تاجر) |
|:---:|:---:|:---:|
| شاشة تسجيل الدخول | الرصيد والخدمات | لوحة أرقام التحصيل |

---

## 🔄 سير العمل (Flows)

```
تسجيل الدخول → الرئيسية
                    ↓
              ┌─────┴──────┐
          حوالة         فواتير
              ↓             ↓
          تأكيد         اختيار خدمة
              ↓             ↓
          نجاح ✅        نجاح ✅
```

---

## 🤝 المساهمة

```bash
git checkout -b feature/اسم-الميزة
git commit -m "feat: وصف التغيير"
git push origin feature/اسم-الميزة
# افتح Pull Request
```

---

## 📄 الترخيص

MIT License — استخدم حر مع الإشارة للمصدر.

---

<div align="center">
صُنع بـ ❤️ باستخدام Kotlin + Jetpack Compose
</div>
