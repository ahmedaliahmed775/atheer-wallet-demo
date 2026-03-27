package com.fintech.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fintech.app.model.*
import com.fintech.app.ui.theme.*
import com.fintech.app.utils.Validators
import com.fintech.app.viewmodel.FinTechViewModel

@Composable
fun SignupRoleScreen(
    onRoleSelected: (UserRole) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
            .padding(24.dp)
    ) {
        // Header
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, null, tint = DarkBlue)
        }

        Spacer(Modifier.height(24.dp))

        // Progress
        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = RoyalBlue,
            trackColor = Gray200
        )
        Text(
            text = "الخطوة 1 من 2",
            style = MaterialTheme.typography.bodySmall,
            color = Gray600,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "اختر نوع الحساب",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBlue
        )

        Text(
            text = "حدد نوع الحساب المناسب لك",
            style = MaterialTheme.typography.bodyMedium,
            color = Gray600
        )

        Spacer(Modifier.height(32.dp))

        // Customer Card
        RoleCard(
            icon = Icons.Default.Person,
            titleAr = "عميل",
            descAr = "أرسل واستقبل الأموال بسهولة",
            color = RoyalBlue,
            isSelected = selectedRole == UserRole.CUSTOMER,
            onClick = { selectedRole = UserRole.CUSTOMER }
        )

        Spacer(Modifier.height(16.dp))

        // Merchant Card
        RoleCard(
            icon = Icons.Default.Store,
            titleAr = "تاجر",
            descAr = "استقبل المدفوعات وأدر متجرك",
            color = BrightGreen,
            isSelected = selectedRole == UserRole.MERCHANT,
            onClick = { selectedRole = UserRole.MERCHANT }
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { selectedRole?.let { onRoleSelected(it) } },
            enabled = selectedRole != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
        ) {
            Text("التالي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, null)
        }
    }
}

@Composable
private fun RoleCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titleAr: String,
    descAr: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) BorderStroke(2.dp, color) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.08f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(titleAr, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = DarkBlue)
                Text(descAr, style = MaterialTheme.typography.bodyMedium, color = Gray600)
            }

            if (isSelected) {
                Icon(Icons.Default.CheckCircle, null, tint = color)
            }
        }
    }
}

@Composable
fun SignupDataScreen(
    role: UserRole,
    viewModel: FinTechViewModel,
    onSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.appState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var storeName by remember { mutableStateOf("") }
    var showPin by remember { mutableStateOf(false) }
    var acceptedTerms by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentUser) {
        if (state.currentUser != null) onSuccess()
    }

    val isValid = Validators.isValidName(name) &&
            Validators.isValidPhone(phone) &&
            Validators.isValidPIN(pin) &&
            pin == confirmPin &&
            acceptedTerms &&
            (role == UserRole.CUSTOMER || storeName.isNotBlank())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, null, tint = DarkBlue)
        }

        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = RoyalBlue,
            trackColor = Gray200
        )
        Text("الخطوة 2 من 2", style = MaterialTheme.typography.bodySmall, color = Gray600, modifier = Modifier.padding(top = 4.dp))

        Spacer(Modifier.height(24.dp))

        Text(
            text = if (role == UserRole.CUSTOMER) "بيانات العميل" else "بيانات التاجر",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBlue
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("الاسم الكامل") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(12.dp))

                if (role == UserRole.MERCHANT) {
                    OutlinedTextField(
                        value = storeName,
                        onValueChange = { storeName = it },
                        label = { Text("اسم المتجر") },
                        leadingIcon = { Icon(Icons.Default.Store, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it },
                    label = { Text("رقم الهاتف") },
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) pin = it },
                    label = { Text("الرمز السري (6 أرقام)") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { showPin = !showPin }) {
                            Icon(if (showPin) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    },
                    visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) confirmPin = it },
                    label = { Text("تأكيد الرمز السري") },
                    leadingIcon = { Icon(Icons.Default.LockOpen, null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = confirmPin.isNotEmpty() && pin != confirmPin,
                    supportingText = {
                        if (confirmPin.isNotEmpty() && pin != confirmPin)
                            Text("الرمز السري غير متطابق", color = Error)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = acceptedTerms,
                        onCheckedChange = { acceptedTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = RoyalBlue)
                    )
                    Text(
                        text = "أوافق على الشروط والأحكام",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray600
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.createAccount(
                    UserData(
                        name = name.trim(),
                        phone = phone,
                        pin = pin,
                        role = role,
                        storeName = if (role == UserRole.MERCHANT) storeName.trim() else null
                    )
                )
            },
            enabled = isValid && !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrightGreen)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("إنشاء الحساب", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
