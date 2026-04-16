package com.fintech.app.ui.screens
import com.fintech.app.ui.theme.TextMain
import com.fintech.app.ui.theme.BgMain
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.ui.theme.*

data class NotificationModel(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean,
    val type: NotificationType
)

enum class NotificationType {
    PAYMENT_SUCCESS,
    MONEY_RECEIVED,
    SYSTEM_ALERT,
    QR_REQUEST
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    // بعض الإشعارات التجريبية لإظهار التصميم بشكل احترافي
    val notifications = listOf(
        NotificationModel(
            "1", "دفع ناجح", "تم دفع مبلغ 150 ﷼ لمتجر بنده بنجاح.", "منذ 5 دقائق", false, NotificationType.PAYMENT_SUCCESS
        ),
        NotificationModel(
            "2", "استلام دفعة", "لقد استلمت 500 ﷼ من محمد علي.", "منذ ساعتين", false, NotificationType.MONEY_RECEIVED
        ),
        NotificationModel(
            "3", "طلب سحب نقدي", "تم طلب تأكيد عبر رمزك السري لعملية سحب.", "أمس", true, NotificationType.QR_REQUEST
        ),
        NotificationModel(
            "4", "تحديث النظام", "مرحباً! لقد قمنا بتحديث التطبيق لتجربة أفضل.", "أمس", true, NotificationType.SYSTEM_ALERT
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("الإشعارات", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextMain,
                    navigationIconContentColor = TextMain
                )
            )
        },
        containerColor = BgMain
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        null,
                        tint = TextMuted.copy(.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("لا توجد إشعارات حالياً", color = TextMuted, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notif ->
                    NotificationItemBox(notif)
                }
            }
        }
    }
}

@Composable
fun NotificationItemBox(notification: NotificationModel) {
    val (icon: ImageVector, color: Color, bgColor: Color) = when (notification.type) {
        NotificationType.PAYMENT_SUCCESS -> Triple(Icons.Default.CheckCircle, Color(0xFF22C55E), Color(0xFFDCFCE7))
        NotificationType.MONEY_RECEIVED -> Triple(Icons.Default.AccountBalanceWallet, Primary, Primary.copy(alpha = 0.12f))
        NotificationType.SYSTEM_ALERT -> Triple(Icons.Default.Info, Color(0xFF3B82F6), Color(0xFFDBEAFE))
        NotificationType.QR_REQUEST -> Triple(Icons.Default.QrCodeScanner, Color(0xFFF59E0B), Color(0xFFFEF3C7))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = if (notification.isRead) Color.White else Color(0xFFF9FAFB)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = bgColor,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = TextMain,
                        fontSize = 15.sp
                    )
                    Text(
                        text = notification.time,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    color = TextMuted.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
