package com.fintech.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantHomeScreen(
    uiState: AppUiState,
    onCashout: (String, String, String) -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    var voucherCode by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.lastCashout) {
        if (uiState.lastCashout != null) {
            showSuccess = true
            voucherCode = ""
            password    = ""
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("استلام دفعة بقسيمة", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, modifier = Modifier.padding(bottom = 16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = BgCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = voucherCode,
                    onValueChange = { voucherCode = it.uppercase(); onClearError() },
                    label = { Text("رمز القسيمة") },
                    leadingIcon = { Icon(Icons.Default.QrCode2, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; onClearError() },
                    label = { Text("كلمة مرور التاجر") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                uiState.error?.let { err ->
                    Spacer(Modifier.height(8.dp))
                    Text(err, color = Color(0xFFA32D2D), fontSize = 12.sp)
                }

                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { onCashout(uiState.userPhone, password, voucherCode) },
                    enabled = !uiState.isLoading && voucherCode.length >= 6 && password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Merchant),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    else Text("تأكيد الاستلام", fontWeight = FontWeight.Medium)
                }
            }
        }

        if (showSuccess && uiState.lastCashout != null) {
            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5EE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = PrimaryDk)
                        Spacer(Modifier.width(8.dp))
                        Text("تم الاستلام بنجاح", fontWeight = FontWeight.SemiBold, color = PrimaryDk)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("المبلغ: %,.0f ﷼".format(uiState.lastCashout!!.amount), fontSize = 13.sp)
                    Text("المرجع: ${uiState.lastCashout!!.refId}", fontSize = 11.sp, color = TextMuted)
                    TextButton({ showSuccess = false; onClearSuccess() }) { Text("إغلاق") }
                }
            }
        }
    }
}
\n@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun MerchantHomeScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        MerchantHomeScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onCashout = { _, _, _ -> },
            onHistory = {},
            onSettings = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}\n