package com.fintech.app.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.FinTechTheme
import com.fintech.app.ui.theme.Primary
import com.fintech.app.ui.theme.TextMuted

@Composable
fun SignupScreen(
    uiState: AppUiState,
    onSignup: (String, String, String, String, String) -> Unit,
    onGoLogin: () -> Unit,
    onSuccess: (String) -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("customer") }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onSuccess(uiState.userRole)
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Text("إنشاء حساب جديد", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("أدخل بياناتك للتسجيل", fontSize = 14.sp, color = TextMuted, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم الكامل") },
            leadingIcon = { Icon(Icons.Default.Person, null) }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("رقم الهاتف") },
            leadingIcon = { Icon(Icons.Default.Phone, null) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("كلمة المرور") },
            leadingIcon = { Icon(Icons.Default.Lock, null) }, visualTransformation = PasswordVisualTransformation(),
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = confirm, onValueChange = { confirm = it }, label = { Text("تأكيد كلمة المرور") },
            leadingIcon = { Icon(Icons.Default.Lock, null) }, visualTransformation = PasswordVisualTransformation(),
            singleLine = true, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("customer" to "عميل", "merchant" to "تاجر").forEach { (v, label) ->
                val selected = role == v
                OutlinedButton(
                    onClick = { role = v },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selected) Primary else Color.Transparent,
                        contentColor   = if (selected) Color.White else TextMuted
                    ),
                    border = BorderStroke(1.dp, if (selected) Primary else Color(0xFFD3D1C7)),
                    modifier = Modifier.weight(1f)
                ) { Text(label) }
            }
        }

        uiState.error?.let { err ->
            Spacer(Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB)), modifier = Modifier.fillMaxWidth()) {
                Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onSignup(name, phone, password, confirm, role) },
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else Text("إنشاء الحساب", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onGoLogin) {
            Text("لديك حساب؟ ", color = TextMuted)
            Text("تسجيل الدخول", color = Primary, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    FinTechTheme {
        SignupScreen(
            uiState = AppUiState(),
            onSignup = { _, _, _, _, _ -> },
            onGoLogin = {},
            onSuccess = {},
            onClearError = {}
        )
    }
}
