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
fun ExternalTransferScreen(
    uiState: AppUiState,
    onTransfer: (String, String, Double, String) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LaunchedEffect(uiState.lastExternalTransfer) {
        if (uiState.lastExternalTransfer != null) { phone = ""; name = ""; amount = ""; note = "" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("حوالة خارجية") },
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("حوالة لغير المشتركين", fontWeight = FontWeight.SemiBold, color = Color(0xFF00695C))
                    Spacer(Modifier.height(4.dp))
                    Text("أرسل حوالة لأي شخص لا يملك محفظة. سيحصل على كود سحب عبر SMS يمكنه من استلام المبلغ من أي وكيل.",
                        fontSize = 12.sp, color = Color(0xFF00897B))
                }
            }

            Spacer(Modifier.height(16.dp))

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
                value = phone, onValueChange = { phone = it; onClearError() },
                label = { Text("رقم هاتف المستلم") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("اسم المستلم (اختياري)") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = amount, onValueChange = { amount = it; onClearError() },
                label = { Text("المبلغ (ريال يمني)") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = note, onValueChange = { note = it },
                label = { Text("ملاحظة (اختياري)") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            uiState.error?.let { err ->
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB))) {
                    Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            uiState.lastExternalTransfer?.let { ext ->
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00695C), modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("تم إرسال الحوالة بنجاح", fontWeight = FontWeight.SemiBold, color = Color(0xFF00695C))
                        Spacer(Modifier.height(12.dp))
                        Text("كود السحب للمستلم:", fontSize = 13.sp, color = TextMuted)
                        Text(
                            ext.withdrawalCode,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00695C),
                            letterSpacing = 4.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("المبلغ: %,.0f ﷼".format(ext.amount), fontSize = 14.sp)
                        Text("المستلم: ${ext.recipientPhone}", fontSize = 13.sp, color = TextMuted)
                        Text("صالح حتى: ${ext.expiresAt.take(19).replace('T', ' ')}", fontSize = 11.sp, color = TextMuted)
                        Spacer(Modifier.height(8.dp))
                        Text("⚠️ شارك هذا الكود مع المستلم ليتمكن من السحب", fontSize = 12.sp, color = Color(0xFF00897B), textAlign = TextAlign.Center)
                        TextButton({ onClearSuccess() }) { Text("عملية جديدة") }
                    }
                }
            }

            if (uiState.lastExternalTransfer == null) {
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { onTransfer(phone, name, amount.toDoubleOrNull() ?: 0.0, note.ifBlank { "حوالة خارجية" }) },
                    enabled = !uiState.isLoading && phone.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    else Text("إرسال الحوالة", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
\n@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun ExternalTransferScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        ExternalTransferScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onTransfer = { _, _, _, _ -> },
            onBack = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}\n