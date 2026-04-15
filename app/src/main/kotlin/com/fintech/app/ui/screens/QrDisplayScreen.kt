package com.fintech.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrDisplayScreen(
    uiState: AppUiState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR التاجر") },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Merchant, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                "اعرض هذا الرمز للعميل",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Merchant
            )
            Text(
                "يمكن للعميل مسح الـ QR أو إدخال رقمك لإتمام الدفع",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // QR Code placeholder (simulated with merchant info)
            Card(
                modifier = Modifier.size(280.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Simulated QR pattern
                    Box(
                        Modifier
                            .size(160.dp)
                            .border(3.dp, Merchant, RoundedCornerShape(12.dp))
                            .background(Color(0xFFEEEDFE), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.QrCode2,
                                null,
                                tint = Merchant,
                                modifier = Modifier.size(80.dp)
                            )
                            Text(
                                "QR",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Merchant
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        uiState.userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        uiState.posNumber ?: "------",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Merchant phone for manual entry
            Card(
                colors = CardDefaults.cardColors(containerColor = BgCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("أو يمكن للعميل مسح الكود أو إدخال رقم نقطة البيع", fontSize = 12.sp, color = TextMuted)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        uiState.posNumber ?: "------",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Merchant,
                        letterSpacing = 3.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Balance
            Card(
                colors = CardDefaults.cardColors(containerColor = Merchant.copy(.1f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("رصيدك الحالي", fontSize = 13.sp, color = Merchant)
                    Text("%,.0f ﷼".format(uiState.balance), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Merchant)
                }
            }
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun QrDisplayScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        QrDisplayScreen(
            uiState = com.fintech.app.model.AppUiState(),
            onBack = {}
        )
    }
}
