import os
import re

path = r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\navigation\AppNavigation.kt'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Remove Screen.Voucher
text = re.sub(r'\s*object Voucher\s*:\s*Screen\("voucher"\)', '', text)

# 2. Remove onVoucher from HomeScreen
text = re.sub(r'\s*onVoucher\s*=\s*\{[^}]*\},\n', '\n', text)

# 3. Remove composable(Screen.Voucher.route)
text = re.sub(r'\s*composable\(Screen\.Voucher\.route\)\s*\{[^}]*VoucherScreen\([^}]*\)[^}]*\}', '', text, flags=re.DOTALL)

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)

print("AppNavigation.kt processed!")
