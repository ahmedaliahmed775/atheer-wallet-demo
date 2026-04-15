import re

path = r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\ui\screens\QrDisplayScreen.kt'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace('uiState.userPhone,', 'uiState.posNumber ?: "------",')
text = text.replace('أو يمكن للعميل الدفع باستخدام رقمك', 'أو يمكن للعميل مسح الكود أو إدخال رقم نقطة البيع')

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)

print("QrDisplayScreen updated!")
