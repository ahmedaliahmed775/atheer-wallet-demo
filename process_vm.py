import os
import re

files = [
    r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\network\WalletApiService.kt',
    r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\data\WalletRepository.kt',
    r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\viewmodel\FinTechViewModel.kt'
]

# 1. Update WalletApiService.kt
with open(files[0], 'r', encoding='utf-8') as f:
    api = f.read()
api = re.sub(r'\s*@POST\("wallet/generate-voucher"\)\s*suspend fun generateVoucher\([^)]*\)\s*:\s*VoucherResponse', '', api)
api = re.sub(r'\s*@POST\("merchant/switch-charge"\)\s*suspend fun cashout\([^)]*\)\s*:\s*CashoutResponse', '', api)
with open(files[0], 'w', encoding='utf-8') as f:
    f.write(api)

# 2. Update WalletRepository.kt
with open(files[1], 'r', encoding='utf-8') as f:
    repo = f.read()
repo = re.sub(r'\s*suspend fun generateVoucher\(.*?\)\s*:\s*Result<VoucherBody>\s*\{[^}]*\}\s*\}', '', repo, flags=re.DOTALL)
repo = re.sub(r'\s*suspend fun cashout\(.*?\)\s*:\s*Result<CashoutBody>\s*\{[^}]*\}\s*\}', '', repo, flags=re.DOTALL)
with open(files[1], 'w', encoding='utf-8') as f:
    f.write(repo)

# 3. Update FinTechViewModel.kt
with open(files[2], 'r', encoding='utf-8') as f:
    vm = f.read()
vm = re.sub(r'\s*fun generateVoucher\(.*?\)\s*\{[^{]*\{[^}]*\}[^{]*\{[^}]*\}[^}]*\}\s*\}', '', vm, flags=re.DOTALL)
vm = re.sub(r'\s*fun cashout\(.*?\)\s*\{[^{]*\{[^}]*\}[^{]*\{[^}]*\}[^}]*\}\s*\}', '', vm, flags=re.DOTALL)

# Update qrPay definition
vm = vm.replace('fun qrPay(merchantPhone: String, amount: Double, note: String = "")', 'fun qrPay(posNumber: String, amount: Double, note: String = "")')
vm = vm.replace('val req = QrPayRequest(merchantPhone, amount, note)', 'val req = QrPayRequest(posNumber, amount, note)')

with open(files[2], 'w', encoding='utf-8') as f:
    f.write(vm)

print("ViewModel & Repo processed.")
