package com.fintech.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.model.TransactionDto
import com.fintech.app.ui.theme.BgCard
import com.fintech.app.ui.theme.FinTechTheme
import com.fintech.app.ui.theme.Primary
import com.fintech.app.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(uiState: AppUiState, onLoadMore: (Int) -> Unit, onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(1) }
    val total = uiState.transactions.sumOf { if (it.type == "CREDIT") it.amount else 0.0 }

    Scaffold(
        topBar = { TopAppBar(title = { Text("سجل المعاملات") }, navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } }) }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${uiState.transactions.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
                            Text("عملية", fontSize = 12.sp, color = TextMuted)
                        }
                        VerticalDivider(Modifier.height(36.dp).width(1.dp), color = Color(0xFFD3D1C7))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("%.0f ﷼".format(total), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
                            Text("إجمالي الاستلام", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            }
            items(uiState.transactions) { txn ->
                Column(Modifier.padding(horizontal = 16.dp)) {
                    TransactionRow(txn)
                    HorizontalDivider(color = Color(0xFFF1EFE8))
                }
            }
            if (uiState.transactions.isNotEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        OutlinedButton(onClick = { currentPage++; onLoadMore(currentPage) }) {
                            Text("تحميل المزيد")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    FinTechTheme {
        HistoryScreen(
            uiState = AppUiState(
                transactions = listOf(
                    TransactionDto(
                        id = "1",
                        refId = "TXN1001",
                        type = "CREDIT",
                        txnType = "TRANSFER",
                        amount = 12500.0,
                        counterparty = "صالح عبدالله",
                        counterPhone = "771000222",
                        note = "مستحقات سابقة",
                        status = "completed",
                        timestamp = "2023-10-27T09:00:00"
                    ),
                    TransactionDto(
                        id = "2",
                        refId = "TXN1002",
                        type = "DEBIT",
                        txnType = "TRANSFER",
                        amount = 3000.0,
                        counterparty = "بقالة الأمل",
                        counterPhone = "770555666",
                        note = "مشتريات",
                        status = "completed",
                        timestamp = "2023-10-26T18:45:00"
                    ),
                    TransactionDto(
                        id = "3",
                        refId = "TXN1003",
                        type = "CREDIT",
                        txnType = "CASHOUT",
                        amount = 50000.0,
                        counterparty = "شركة المحضار للصرافة",
                        counterPhone = "777888999",
                        note = "إيداع نقدي",
                        status = "completed",
                        timestamp = "2023-10-25T11:20:00"
                    )
                )
            ),
            onLoadMore = {},
            onBack = {}
        )
    }
}
