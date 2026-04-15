package com.fintech.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.fintech.app.ui.theme.BgCard
import com.fintech.app.ui.theme.FinTechTheme
import com.fintech.app.ui.theme.Primary
import com.fintech.app.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(uiState: AppUiState, onLogout: () -> Unit, onBack: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("تأكيد الخروج") },
            text = { Text("هل تريد تسجيل الخروج من حسابك؟") },
            confirmButton = { TextButton(onClick = { showLogoutDialog = false; onLogout() }) { Text("خروج", color = Color(0xFFA32D2D)) } },
            dismissButton = { TextButton({ showLogoutDialog = false }) { Text("إلغاء") } }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("الإعدادات") }, navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            // Profile card
            Card(colors = CardDefaults.cardColors(containerColor = BgCard), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(shape = CircleShape, color = Primary.copy(.15f), modifier = Modifier.size(60.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(uiState.userName.take(2), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(uiState.userName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(uiState.userPhone, fontSize = 13.sp, color = TextMuted)
                    Surface(shape = RoundedCornerShape(8.dp), color = Primary.copy(.1f), modifier = Modifier.padding(top = 6.dp)) {
                        Text(if (uiState.userRole == "merchant") "تاجر" else "عميل",
                            fontSize = 12.sp, color = Primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Settings items
            listOf(
                Triple(Icons.Default.Info, "إصدار التطبيق", "1.0.0"),
                Triple(Icons.Default.Phone, "رقم الهاتف", uiState.userPhone)
            ).forEach { (icon, title, value) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(icon, null, tint = TextMuted, modifier = Modifier.size(20.dp))
                        Text(title, fontSize = 14.sp)
                    }
                    Text(value, fontSize = 13.sp, color = TextMuted)
                }
                HorizontalDivider(color = Color(0xFFF1EFE8))
            }

            Spacer(Modifier.weight(1f))
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA32D2D)),
                border = BorderStroke(1.dp, Color(0xFFA32D2D).copy(.4f)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("تسجيل الخروج", fontSize = 15.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    FinTechTheme {
        SettingsScreen(
            uiState = AppUiState(
                userName = "محمد عبدالله",
                userPhone = "770000000",
                userRole = "customer"
            ),
            onLogout = {},
            onBack = {}
        )
    }
}
