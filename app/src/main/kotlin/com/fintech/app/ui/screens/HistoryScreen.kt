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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.BgCard
import com.fintech.app.ui.theme.Primary
import com.fintech.app.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: AppUiState,
    onLoadMore: (Int) -> Unit,
    onBack: () -> Unit,
    onTransactionClick: (String) -> Unit = {}
) {
    var currentPage by remember { mutableIntStateOf(1) }
    val totalCredit = uiState.transactions.filter { it.type == "CREDIT" }.sumOf { it.amount }
    val totalDebit  = uiState.transactions.filter { it.type == "DEBIT" }.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سجل المعاملات") },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
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
                            Text("%,.0f".format(totalCredit), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Primary)
                            Text("وارد ﷼", fontSize = 12.sp, color = TextMuted)
                        }
                        VerticalDivider(Modifier.height(36.dp).width(1.dp), color = Color(0xFFD3D1C7))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("%,.0f".format(totalDebit), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA32D2D))
                            Text("صادر ﷼", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            }
            items(uiState.transactions) { txn ->
                Column(Modifier.padding(horizontal = 16.dp)) {
                    TransactionRowClickable(txn) { onTransactionClick(txn.id) }
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
\n@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun HistoryScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        HistoryScreen(
            uiState = com.fintech.app.model.AppUiState(),
            onLoadMore = { _ -> },
            onBack = {},
            onTransactionClick = { _ -> }
        )
    }
}\n