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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashInScreen(
    uiState: AppUiState,
    onCashIn: (Double) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    var amount by remember { mutableStateOf("") }

    LaunchedEffect(uiState.lastCashIn) {
        if (uiState.lastCashIn != null) { amount = "" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إيداع نقدي") },
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
            // Info
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("إيداع رصيد في محفظتك", fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32))
                    Spacer(Modifier.height(4.dp))
                    Text("قم بزيارة أي وكيل معتمد وأعطه المبلغ النقدي ليتم إضافته لمحفظتك فوراً.",
                        fontSize = 12.sp, color = Color(0xFF388E3C))
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Primary), shape = RoundedCornerShape(14.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("رصيدك الحالي", color = Color.White.copy(.8f), fontSize = 13.sp)
                    Text("%,.0f ﷼".format(uiState.balance), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it; onClearError() },
                label = { Text("مبلغ الإيداع (ريال يمني)") },
                leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, null) },
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

            uiState.lastCashIn?.let { ci ->
                Spacer(Modifier.height(12.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32))
                            Spacer(Modifier.width(8.dp))
                            Text("تم الإيداع بنجاح", fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("المبلغ: %,.0f ﷼".format(ci.amount), fontSize = 14.sp)
                        Text("الرصيد الجديد: %,.0f ﷼".format(ci.newBalance), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Primary)
                        Text("المرجع: ${ci.refId}", fontSize = 11.sp, color = TextMuted)
                        TextButton({ onClearSuccess() }) { Text("إغلاق") }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onCashIn(amount.toDoubleOrNull() ?: 0.0) },
                enabled = !uiState.isLoading && (amount.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("إيداع الآن", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun CashInScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        CashInScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onCashIn = { _ -> },
            onBack = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}
