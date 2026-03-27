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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fintech.app.model.Language
import com.fintech.app.model.User
import com.fintech.app.ui.theme.*
import com.fintech.app.utils.Validators
import com.fintech.app.viewmodel.FinTechViewModel

@Composable
fun SplashScreen(
    viewModel: FinTechViewModel,
    onLoginSuccess: (User) -> Unit,
    onSignupClick: () -> Unit
) {
    val state by viewModel.appState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val isArabic = state.language == Language.ARABIC

    var phone by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var showPin by remember { mutableStateOf(false) }

    // Fade-in animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Navigate on successful login
    LaunchedEffect(state.currentUser) {
        state.currentUser?.let { onLoginSuccess(it) }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
                .clickable(indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }) {
                    focusManager.clearFocus()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(60.dp))

                // Language toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isArabic) Arrangement.End else Arrangement.Start
                ) {
                    TextButton(
                        onClick = {
                            viewModel.setLanguage(
                                if (isArabic) Language.ENGLISH else Language.ARABIC
                            )
                        }
                    ) {
                        Text(
                            text = if (isArabic) "EN" else "عربي",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Logo
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = if (isArabic) "تطبيق التحويلات" else "FinTech Pay",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isArabic) "آمن • سريع • موثوق" else "Secure • Fast • Trusted",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(Modifier.height(40.dp))

                // Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isArabic) "تسجيل الدخول" else "Login",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue
                        )

                        Spacer(Modifier.height(24.dp))

                        // Phone field
                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it
                                viewModel.clearError()
                            },
                            label = { Text(if (isArabic) "رقم الهاتف" else "Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = phone.isNotEmpty() && !Validators.isValidPhone(phone),
                            supportingText = {
                                Validators.phoneErrorMessage(phone, isArabic)?.let {
                                    Text(it, color = Error)
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        // PIN field
                        OutlinedTextField(
                            value = pin,
                            onValueChange = {
                                if (it.length <= 6 && it.all { c -> c.isDigit() }) pin = it
                                viewModel.clearError()
                            },
                            label = { Text(if (isArabic) "الرمز السري" else "PIN") },
                            leadingIcon = { Icon(Icons.Default.Lock, null) },
                            trailingIcon = {
                                IconButton(onClick = { showPin = !showPin }) {
                                    Icon(
                                        if (showPin) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        null
                                    )
                                }
                            },
                            visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Error message
                        AnimatedVisibility(visible = state.errorMessage != null) {
                            state.errorMessage?.let { msg ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = ErrorLight),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Error, null, tint = Error, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(msg, color = Error, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // Login Button
                        val isFormValid = Validators.isValidPhone(phone) && pin.length == 6

                        Button(
                            onClick = { viewModel.login(phone, pin) },
                            enabled = isFormValid && !state.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrightGreen)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (isArabic) "تسجيل الدخول" else "Login",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Signup Button
                        OutlinedButton(
                            onClick = onSignupClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(2.dp, RoyalBlue)
                        ) {
                            Text(
                                text = if (isArabic) "إنشاء حساب جديد" else "Create Account",
                                color = RoyalBlue,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = if (isArabic) "تجريبي: 0501234567 | رمز: 123456" 
                                   else "Demo: 0501234567 | PIN: 123456",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray400,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
