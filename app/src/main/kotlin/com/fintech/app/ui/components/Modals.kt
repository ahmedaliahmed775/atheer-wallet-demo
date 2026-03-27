package com.fintech.app.ui.components

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.fintech.app.model.BillService
import com.fintech.app.ui.theme.*
import com.fintech.app.utils.Validators
import com.fintech.app.utils.formatAmount

// ─── Transfer Modal ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferModal(
    currentBalance: Double,
    onConfirm: (recipient: String, amount: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var recipientPhone by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val amountValue = amount.toDoubleOrNull() ?: 0.0
    val isValid = Validators.isValidPhone(recipientPhone) &&
            amountValue > 0 && amountValue <= currentBalance

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("حوالة مالية", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = DarkBlue)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = Gray600) }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = recipientPhone,
                onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) recipientPhone = it },
                label = { Text("رقم هاتف المستقبل") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) amount = it },
                label = { Text("المبلغ (ريال)") },
                leadingIcon = { Icon(Icons.Default.CurrencyExchange, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("ريال") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = amount.isNotEmpty() && (amountValue <= 0 || amountValue > currentBalance),
                supportingText = {
                    if (amount.isNotEmpty() && amountValue > currentBalance)
                        Text("المبلغ أكبر من رصيدك (${currentBalance.formatAmount()} ريال)", color = Error)
                }
            )

            if (isValid) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LightBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("ملخص العملية", style = MaterialTheme.typography.labelLarge, color = RoyalBlue, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("المستقبل", color = Gray600, style = MaterialTheme.typography.bodyMedium)
                            Text(recipientPhone, fontWeight = FontWeight.Medium)
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("المبلغ", color = Gray600, style = MaterialTheme.typography.bodyMedium)
                            Text("${amountValue.formatAmount()} ريال", color = RoyalBlue, fontWeight = FontWeight.Bold)
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("الرصيد بعد التحويل", color = Gray600, style = MaterialTheme.typography.bodyMedium)
                            Text("${(currentBalance - amountValue).formatAmount()} ريال", color = BrightGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onConfirm(recipientPhone, amountValue) },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Icon(Icons.Default.Send, null)
                Spacer(Modifier.width(8.dp))
                Text("تأكيد التحويل", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Bill Payment Modal ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillPaymentModal(
    currentBalance: Double,
    onConfirm: (service: BillService, amount: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedService by remember { mutableStateOf<BillService?>(null) }
    var amount by remember { mutableStateOf("") }
    val amountValue = amount.toDoubleOrNull() ?: 0.0
    val isValid = selectedService != null && amountValue > 0 && amountValue <= currentBalance

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("سداد الفواتير", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = DarkBlue)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = Gray600) }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(20.dp))

            Text("اختر الخدمة", style = MaterialTheme.typography.titleMedium, color = Gray600)
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BillServiceCard(
                    service = BillService.PHONE,
                    icon = Icons.Default.PhoneAndroid,
                    color = PhoneColor,
                    isSelected = selectedService == BillService.PHONE,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedService = BillService.PHONE }
                )
                BillServiceCard(
                    service = BillService.ELECTRICITY,
                    icon = Icons.Default.Bolt,
                    color = ElectricityColor,
                    isSelected = selectedService == BillService.ELECTRICITY,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedService = BillService.ELECTRICITY }
                )
                BillServiceCard(
                    service = BillService.INTERNET,
                    icon = Icons.Default.Wifi,
                    color = InternetColor,
                    isSelected = selectedService == BillService.INTERNET,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedService = BillService.INTERNET }
                )
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) amount = it },
                label = { Text("المبلغ (ريال)") },
                leadingIcon = { Icon(Icons.Default.CurrencyExchange, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("ريال") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onConfirm(selectedService!!, amountValue) },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Icon(Icons.Default.Receipt, null)
                Spacer(Modifier.width(8.dp))
                Text("سداد الفاتورة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BillServiceCard(
    service: BillService,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) BorderStroke(2.dp, color) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.1f) else Gray100
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                text = service.labelAr,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) color else Gray600,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Success Screen ───────────────────────────────────────────────────────────

@Composable
fun SuccessScreen(
    title: String,
    amount: Double,
    recipient: String,
    referenceNumber: String,
    onDone: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounce"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
                    .background(SuccessLight, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = Success, modifier = Modifier.size(60.dp))
            }

            Spacer(Modifier.height(24.dp))

            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = DarkBlue)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${amount.formatAmount()} ريال",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Success
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Gray100)
            ) {
                Column(Modifier.padding(20.dp)) {
                    DetailRow("المستقبل", recipient)
                    Spacer(Modifier.height(8.dp))
                    DetailRow("رقم المرجع", referenceNumber)
                    Spacer(Modifier.height(8.dp))
                    DetailRow("الحالة", "✅ تمت بنجاح")
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text("العودة للرئيسية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, color = Gray600, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}
