package com.fintech.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.FinTechTheme
import com.fintech.app.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(
    uiState: AppUiState,
    onGenerate: (Double) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    var amount by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("إنشاء قسيمة دفع") }, navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState())) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEDFE)), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("كيف تعمل القسيمة؟", fontWeight = FontWeight.SemiBold, color = Color(0xFF3C3489))
                    Spacer(Modifier.height(6.dp))
                    Text("1. أدخل المبلغ وأنشئ القسيمة", fontSize = 13.sp, color = Color(0xFF534AB7))
                    Text("2. أعطِ الرمز للتاجر ليُدخله في جهازه", fontSize = 13.sp, color = Color(0xFF534AB7))
                    Text("3. تُستهلك القسيمة تلقائياً ← صالحة 5 دقائق", fontSize = 13.sp, color = Color(0xFF534AB7))
                }
            }

            Spacer(Modifier.height(20.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Primary), shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("رصيدك المتاح", color = Color.White.copy(.8f), fontSize = 13.sp)
                    Text("%.0f ﷼".format(uiState.balance), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = amount, onValueChange = { amount = it; onClearError() },
                label = { Text("مبلغ القسيمة (ريال يمني)") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            uiState.error?.let { err ->
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB))) {
                    Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            uiState.lastVoucher?.let { v ->
                Spacer(Modifier.height(16.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEDFE)), shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("رمز القسيمة", fontSize = 13.sp, color = Color(0xFF534AB7))
                        Spacer(Modifier.height(8.dp))
                        Text(v.voucherCode, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF26215C), letterSpacing = 4.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("المبلغ: %.0f ﷼".format(v.amount), fontSize = 14.sp, color = Color(0xFF534AB7))
                        Text("صالحة حتى: ${v.expiresAt.take(19).replace('T', ' ')}", fontSize = 11.sp, color = Color(0xFF888780))
                        Spacer(Modifier.height(8.dp))
                        TextButton({ onClearSuccess() }) { Text("إغلاق", color = Color(0xFF534AB7)) }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onGenerate(amount.toDoubleOrNull() ?: 0.0) },
                enabled = !uiState.isLoading && (amount.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF534AB7)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("إنشاء القسيمة", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VoucherScreenPreview() {
    FinTechTheme {
        VoucherScreen(
            uiState = AppUiState(balance = 120000.0),
            onGenerate = {},
            onBack = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}
