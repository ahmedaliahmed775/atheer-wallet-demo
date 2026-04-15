package com.fintech.app.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    uiState: AppUiState,
    transactionId: String,
    onLoadDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(transactionId) {
        onLoadDetail(transactionId)
    }

    val detail = uiState.transactionDetail

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إيصال المعاملة") },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        if (detail == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status icon
                val isCredit = detail.type == "CREDIT"
                val statusColor = if (isCredit) PrimaryDk else Color(0xFFEF4444)

                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor.copy(.12f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            null,
                            tint = statusColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    if (isCredit) "استلام أموال" else "إرسال أموال",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    "${if (isCredit) "+" else "-"}%,.0f ﷼".format(detail.amount),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE1F5EE)
                ) {
                    Text(
                        "مكتمل ✓",
                        fontSize = 12.sp,
                        color = PrimaryDk,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Receipt card
                Card(
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("تفاصيل العملية", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E3DC))

                        ReceiptRow("نوع العملية", txnTypeLabel(detail.txnType))
                        ReceiptRow("المرسل", detail.senderName ?: "—")
                        ReceiptRow("رقم المرسل", detail.senderPhone ?: "—")
                        ReceiptRow("المستلم", detail.receiverName ?: "—")
                        ReceiptRow("رقم المستلم", detail.receiverPhone ?: "—")
                        ReceiptRow("الملاحظة", detail.note ?: "—")
                        ReceiptRow("رقم المرجع", detail.refId ?: "—")
                        ReceiptRow("التاريخ", detail.timestamp.take(19).replace('T', ' '))
                        ReceiptRow("الحالة", "مكتمل")
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Share button
                Button(
                    onClick = { shareReceipt(context, detail.let { d ->
                        buildString {
                            appendLine("═══════════════════")
                            appendLine("   إيصال معاملة — FinTech Pay")
                            appendLine("═══════════════════")
                            appendLine("النوع: ${txnTypeLabel(d.txnType)}")
                            appendLine("المبلغ: %,.0f ﷼".format(d.amount))
                            appendLine("المرسل: ${d.senderName ?: "—"}")
                            appendLine("المستلم: ${d.receiverName ?: "—"}")
                            appendLine("الملاحظة: ${d.note ?: "—"}")
                            appendLine("المرجع: ${d.refId ?: "—"}")
                            appendLine("التاريخ: ${d.timestamp.take(19).replace('T', ' ')}")
                            appendLine("الحالة: مكتمل ✓")
                            appendLine("═══════════════════")
                        }
                    }) },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("مشاركة الإيصال", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "يمكنك مشاركة الإيصال عبر واتساب أو أي تطبيق آخر",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextMuted)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

private fun txnTypeLabel(type: String?): String = when (type) {
    "TRANSFER"          -> "تحويل"
    "CASHOUT"           -> "دفع قسيمة"
    "TOPUP"             -> "شحن"
    "BILL_PAYMENT"      -> "سداد فاتورة"
    "EXTERNAL_TRANSFER" -> "حوالة خارجية"
    "CASH_IN"           -> "إيداع نقدي"
    "CASH_OUT"          -> "سحب نقدي"
    "QR_PAYMENT"        -> "دفع QR"
    else                -> type ?: "معاملة"
}

private fun shareReceipt(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        this.type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "مشاركة الإيصال عبر"))
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun TransactionDetailScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        TransactionDetailScreen(
            uiState = com.fintech.app.model.AppUiState(),
            transactionId = "TX-123456",
            onLoadDetail = { _ -> },
            onBack = {}
        )
    }
}
