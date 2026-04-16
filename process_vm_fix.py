import re

path = r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\viewmodel\FinTechViewModel.kt'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Fix the stranded onFailure block
text = re.sub(r'\s*\.onFailure \{ setError\(it\.message \?: "[^"]*"\) \}\s*\}\s*\}', '', text)

# 2. Fix merchantPhone in qrPay
text = text.replace('if (merchantPhone.isBlank())', 'if (posNumber.isBlank())')

# 3. Remove lastVoucher and lastCashout from clearSuccess
text = re.sub(r'\s*lastVoucher\s*=\s*null,', '', text)
text = re.sub(r'\s*lastCashout\s*=\s*null,', '', text)

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)

print("Fixed ViewModel")
