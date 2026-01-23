package com.relife.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.relife.data.model.Notification
import com.relife.data.model.NotificationType
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val notifications = remember { MockData.notifications }
    var selectedFilter by remember { mutableStateOf("Todas") }
    val filters = listOf("Todas", "Sin leer", "Likes", "Comentarios", "Seguidores", "Ventas")
    
    val filteredNotifications = when (selectedFilter) {
        "Sin leer" -> notifications.filter { !it.isRead }
        "Likes" -> notifications.filter { it.type == NotificationType.LIKE }
        "Comentarios" -> notifications.filter { it.type == NotificationType.COMMENT }
        "Seguidores" -> notifications.filter { it.type == NotificationType.FOLLOW }
        "Ventas" -> notifications.filter { it.type == NotificationType.SALE }
        else -> notifications
    }
    
    val todayNotifications = filteredNotifications.filter { 
        System.currentTimeMillis() - it.createdAt < 86400000 
    }
    val earlierNotifications = filteredNotifications.filter { 
        System.currentTimeMillis() - it.createdAt >= 86400000 
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Mark all as read */ }) {
                        Icon(Icons.Outlined.DoneAll, "Marcar todo como leído")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    ReLifeChip(
                        text = filter,
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }
            
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Today section
                if (todayNotifications.isNotEmpty()) {
                    item {
                        Text(
                            text = "Hoy",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Stone600,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(todayNotifications) { notification ->
                        NotificationItem(notification = notification)
                    }
                }
                
                // Earlier section
                if (earlierNotifications.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Anteriores",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Stone600,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(earlierNotifications) { notification ->
                        NotificationItem(notification = notification)
                    }
                }
                
                if (filteredNotifications.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Outlined.Notifications,
                            title = "Sin notificaciones",
                            description = "No tienes notificaciones en esta categoría",
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(notification: Notification) {
    val (icon, iconColor) = getNotificationIconAndColor(notification.type)
    val timeAgo = getTimeAgo(notification.createdAt)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Emerald50
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with notification type badge
            Box {
                UserAvatar(imageUrl = notification.fromUser.avatarUrl, size = 48.dp)
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(iconColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(
                        text = notification.fromUser.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = " ${notification.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone500
                )
            }
            
            // Related image
            notification.relatedImageUrl?.let { imageUrl ->
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CardShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Unread indicator
            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Emerald500)
                )
            }
        }
    }
}

private fun getNotificationIconAndColor(type: NotificationType): Pair<ImageVector, Color> {
    return when (type) {
        NotificationType.LIKE -> Icons.Filled.Favorite to Rose500
        NotificationType.COMMENT -> Icons.Filled.ChatBubble to Blue500
        NotificationType.FOLLOW -> Icons.Filled.PersonAdd to Violet500
        NotificationType.SHARE -> Icons.Filled.Share to Emerald500
        NotificationType.SALE -> Icons.Filled.ShoppingBag to Amber500
        NotificationType.REVIEW -> Icons.Filled.Star to Amber400
        NotificationType.MENTION -> Icons.Filled.AlternateEmail to Pink500
        NotificationType.SHIPPING -> Icons.Filled.LocalShipping to Cyan500
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / 60000
    val hours = diff / 3600000
    val days = diff / 86400000
    
    return when {
        minutes < 1 -> "Ahora"
        minutes < 60 -> "Hace ${minutes}m"
        hours < 24 -> "Hace ${hours}h"
        days < 7 -> "Hace ${days}d"
        else -> "Hace ${days / 7}sem"
    }
}
