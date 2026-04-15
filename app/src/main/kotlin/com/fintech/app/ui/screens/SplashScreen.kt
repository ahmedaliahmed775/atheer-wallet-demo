package com.fintech.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.ui.theme.Primary
import com.fintech.app.ui.theme.TextMuted
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isLoggedIn: Boolean,
    userRole: String,
    onNavigate: (String) -> Unit,
    onLoginRequired: () -> Unit
) {
    LaunchedEffect(isLoggedIn) {
        delay(1200)
        if (isLoggedIn) onNavigate(userRole) else onLoginRequired()
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Primary,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("FinTech Pay", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("محفظتك الرقمية", fontSize = 14.sp, color = TextMuted)
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(color = Primary, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun SplashScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        SplashScreen(
            isLoggedIn = true,
            userRole = "CUSTOMER",
            onNavigate = { _ -> },
            onLoginRequired = {}
        )
    }
}
