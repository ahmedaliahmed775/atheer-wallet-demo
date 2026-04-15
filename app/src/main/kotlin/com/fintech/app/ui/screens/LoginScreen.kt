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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.FinTechTheme
import com.fintech.app.ui.theme.Primary
import com.fintech.app.ui.theme.TextMuted

@Composable
fun LoginScreen(
    uiState: AppUiState,
    onLogin: (String, String) -> Unit,
    onGoSignup: () -> Unit,
    onSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onSuccess()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))
        Surface(shape = RoundedCornerShape(16.dp), color = Primary, modifier = Modifier.size(64.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("تسجيل الدخول", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("أدخل بيانات حسابك للمتابعة", fontSize = 14.sp, color = TextMuted, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(36.dp))

        OutlinedTextField(
            value = phone, onValueChange = { phone = it; onClearError() },
            label = { Text("رقم الهاتف") },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it; onClearError() },
            label = { Text("كلمة المرور") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton({ passVisible = !passVisible }) {
                    Icon(if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )

        uiState.error?.let { err ->
            Spacer(Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB)), modifier = Modifier.fillMaxWidth()) {
                Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onLogin(phone, password) },
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else Text("دخول", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onGoSignup) {
            Text("ليس لديك حساب؟ ", color = TextMuted)
            Text("إنشاء حساب", color = Primary, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FinTechTheme {
        LoginScreen(
            uiState = AppUiState(),
            onLogin = { _, _ -> },
            onGoSignup = {},
            onSuccess = {},
            onClearError = {}
        )
    }
}
