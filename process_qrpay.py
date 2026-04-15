import re

path = r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\ui\screens\QrPayScreen.kt'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace('var merchantPhone', 'var posNumber')
text = text.replace('merchantPhone = ""', 'posNumber = ""')
text = text.replace('merchantPhone = it', 'posNumber = it')
text = text.replace('value = merchantPhone,', 'value = posNumber,')
text = text.replace('merchantPhone.isNotBlank()', 'posNumber.isNotBlank()')
text = text.replace('onQrPay(merchantPhone', 'onQrPay(posNumber')
text = text.replace('رقم هاتف التاجر', 'رقم نقطة البيع (6 أرقام)')
text = text.replace('أدخل رقم التاجر أو امسح الكود', 'أدخل رقم نقطة البيع (6 أرقام) للتاجر أو امسح الكود')

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)

print("QrPayScreen updated!")
