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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantHomeScreen(
    uiState: AppUiState,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Storefront, null, modifier = Modifier.size(80.dp), tint = Merchant)
        Spacer(Modifier.height(16.dp))
        Text("بيانات نقطة البيع الخاصة بك", fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = Color(0xFF3C3489))
        
        Spacer(Modifier.height(32.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = BgCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("رقم نقطة البيع (POS)", fontSize = 14.sp, color = TextMuted)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = uiState.posNumber ?: "------",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Merchant,
                    letterSpacing = 4.sp
                )
                Spacer(Modifier.height(8.dp))
                Text("شارك هذا الرقم مع العملاء لاستقبال المدفوعات", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun MerchantHomeScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        MerchantHomeScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0, posNumber = "123456"),
            onHistory = {},
            onSettings = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}
