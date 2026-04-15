package com.fintech.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.TransactionDto
import com.fintech.app.ui.theme.BgCard
import com.fintech.app.ui.theme.PrimaryDk
import com.fintech.app.ui.theme.TextMuted

@Composable
fun QuickAction(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = color.copy(.12f), modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(24.dp)) }
            }
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun TransactionRow(txn: TransactionDto) {
    TransactionRowContent(txn)
}

@Composable
fun TransactionRowClickable(txn: TransactionDto, onClick: () -> Unit) {
    Box(Modifier.clickable(onClick = onClick)) {
        TransactionRowContent(txn)
    }
}

@Composable
private fun TransactionRowContent(txn: TransactionDto) {
    val isCredit = txn.type == "CREDIT"
    val typeIcon = when (txn.txnType) {
        "BILL_PAYMENT"      -> Icons.Default.Receipt
        "QR_PAYMENT"        -> Icons.Default.QrCode2
        "EXTERNAL_TRANSFER" -> Icons.Default.SendAndArchive
        "CASH_IN"           -> Icons.Default.AccountBalanceWallet
        "CASH_OUT"          -> Icons.Default.MoneyOff
        "CASHOUT"           -> Icons.Default.PointOfSale
        else                -> if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward
    }

    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = CircleShape, color = if (isCredit) Color(0xFFE1F5EE) else Color(0xFFFCEBEB), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(typeIcon,
                        null, tint = if (isCredit) PrimaryDk else Color(0xFFA32D2D), modifier = Modifier.size(18.dp))
                }
            }
            Column {
                Text(txn.counterparty ?: txnTypeShortLabel(txn.txnType), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text(txnTypeShortLabel(txn.txnType), fontSize = 10.sp, color = TextMuted)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${if (isCredit) "+" else "-"}%,.0f ﷼".format(txn.amount),
                color = if (isCredit) PrimaryDk else Color(0xFFA32D2D),
                fontWeight = FontWeight.SemiBold, fontSize = 14.sp
            )
            Text(
                txn.timestamp.take(10),
                fontSize = 9.sp,
                color = TextMuted
            )
        }
    }
}

private fun txnTypeShortLabel(type: String?): String = when (type) {
    "TRANSFER"          -> "تحويل"
    "CASHOUT"           -> "قسيمة"
    "TOPUP"             -> "شحن"
    "BILL_PAYMENT"      -> "فاتورة"
    "EXTERNAL_TRANSFER" -> "حوالة خارجية"
    "CASH_IN"           -> "إيداع"
    "CASH_OUT"          -> "سحب"
    "QR_PAYMENT"        -> "دفع QR"
    else                -> "معاملة"
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(.1f)), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, color = color.copy(.8f))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
