import os
import re

path = r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\model\Models.kt'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Add posNumber to UserDto
text = text.replace('val role: String,', 'val role: String,\n    val posNumber: String? = null,')

# 2. Add posNumber to AppUiState
text = text.replace('val userRole: String = "customer",', 'val userRole: String = "customer",\n    val posNumber: String? = null,')

# 3. Add posNumber to QrInfoBody
text = text.replace('val merchantPhone: String,\n    val qrData: String', 'val merchantPhone: String,\n    val merchantPosNumber: String? = null,\n    val qrData: String')

# 4. Change QrPayRequest to use posNumber
text = text.replace('val merchantPhone: String', 'val posNumber: String')

# 5. Remove Voucher models completely
text = re.sub(r'// ─── Voucher ───.*?// ─── Bill Payment ───', '// ─── Bill Payment ───', text, flags=re.DOTALL)

# 6. Remove merchant Cashout (switch-charge) related classes
to_remove = '''
data class CashoutRequest(
    val agentWallet: String,
    val password: String,
    val accessToken: String,
    val voucher: String
)

data class CashoutResponse(
    @SerializedName("ResponseCode") val responseCode: Int,
    @SerializedName("ResponseMessage") val responseMessage: String?,
    val body: CashoutBody?
)

data class CashoutBody(
    val transactionId: String,
    val refId: String,
    val voucherCode: String,
    val amount: Double,
    val merchantWallet: String,
    val merchantName: String,
    val timestamp: String
)
'''
text = text.replace(to_remove.strip(), '')

# remove lastVoucher and lastCashout from AppUiState
text = text.replace('    val lastVoucher: VoucherBody? = null,\n', '')
text = text.replace('    val lastCashout: CashoutBody? = null,\n', '')

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)

print("Models.kt processed!")
