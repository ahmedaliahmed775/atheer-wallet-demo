import re

path1 = r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\network\WalletApiService.kt'
with open(path1, 'r', encoding='utf-8') as f:
    api = f.read()

# Delete generateVoucher route completely
api = re.sub(r'\s*@POST\("/wallet/generate-voucher"\)\s*suspend fun generateVoucher\(\s*@Header\("Authorization"\) token: String,\s*@Body request: GenerateVoucherRequest\s*\): Response<VoucherResponse>', '', api)

# Delete switch-charge completely
api = re.sub(r'\s*@POST\("/merchant/switch-charge"\)\s*suspend fun cashout\(\s*@Header\("Authorization"\) token: String,\s*@Body request: CashoutRequest\s*\): Response<CashoutResponse>', '', api)

with open(path1, 'w', encoding='utf-8') as f:
    f.write(api)

path2 = r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\data\WalletRepository.kt'
with open(path2, 'r', encoding='utf-8') as f:
    repo = f.read()

# Using a simpler string replacement for generateVoucher and cashout to prevent regex mistakes
# I'll just check if it contains those strings
lines = repo.split('\n')
repo_new = []
in_generate_voucher = False
in_cashout = False
in_generate_cashout = False

for line in lines:
    if 'suspend fun generateVoucher(' in line:
        in_generate_voucher = True
    if 'suspend fun cashout(' in line:
        in_cashout = True
    
    if in_generate_voucher:
        if line.strip() == '}':
            in_generate_voucher = False
        continue
        
    if in_cashout:
        if line.strip() == '}':
            in_cashout = False
        continue
        
    repo_new.append(line)

with open(path2, 'w', encoding='utf-8') as f:
    f.write('\n'.join(repo_new))

print("Fixed API and Repo")
