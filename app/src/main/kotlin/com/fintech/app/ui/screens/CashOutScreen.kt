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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashOutScreen(
    uiState: AppUiState,
    onGenerateCashout: (Double) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    var amount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سحب نقدي") },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Instructions
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3F2)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("كيف يعمل السحب النقدي؟", fontWeight = FontWeight.SemiBold, color = Color(0xFFB91C1C))
                    Spacer(Modifier.height(6.dp))
                    Text("1. أدخل المبلغ المطلوب سحبه", fontSize = 13.sp, color = Color(0xFFDC2626))
                    Text("2. سيُخصم المبلغ من رصيدك فوراً", fontSize = 13.sp, color = Color(0xFFDC2626))
                    Text("3. ستحصل على كود سحب (صالح 30 دقيقة)", fontSize = 13.sp, color = Color(0xFFDC2626))
                    Text("4. أعطِ الكود لأي وكيل ليسلمك المبلغ نقداً", fontSize = 13.sp, color = Color(0xFFDC2626))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Balance
            Card(colors = CardDefaults.cardColors(containerColor = Primary), shape = RoundedCornerShape(14.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("رصيدك المتاح", color = Color.White.copy(.8f), fontSize = 13.sp)
                    Text("%,.0f ﷼".format(uiState.balance), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it; onClearError() },
                label = { Text("مبلغ السحب (ريال يمني)") },
                leadingIcon = { Icon(Icons.Default.Money, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            uiState.error?.let { err ->
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB))) {
                    Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            // Show cashout code
            uiState.lastCashOut?.let { cashOut ->
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3F2)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Key, null, tint = Color(0xFFEF4444), modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("كود السحب", fontSize = 14.sp, color = Color(0xFFB91C1C))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            cashOut.code,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFB91C1C),
                            letterSpacing = 6.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "المبلغ: %,.0f ﷼".format(cashOut.amount),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "صالح حتى: ${cashOut.expiresAt.take(19).replace('T', ' ')}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "⚠️ أعطِ هذا الكود للوكيل فقط",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        TextButton({ onClearSuccess(); amount = "" }) {
                            Text("عملية جديدة", color = Color(0xFFB91C1C))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            if (uiState.lastCashOut == null) {
                Button(
                    onClick = { onGenerateCashout(amount.toDoubleOrNull() ?: 0.0) },
                    enabled = !uiState.isLoading && (amount.toDoubleOrNull() ?: 0.0) > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    else Text("إنشاء كود السحب", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
\n@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun CashOutScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        CashOutScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onGenerateCashout = { _ -> },
            onBack = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}\n