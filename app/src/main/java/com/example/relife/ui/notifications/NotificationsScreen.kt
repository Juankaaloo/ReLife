package com.relife.ui.notifications

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.relife.data.model.Notification
import com.relife.data.model.NotificationType
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*
import kotlin.math.roundToInt

private data class FilterTab(
    val label: String,
    val icon: ImageVector,
    val types: List<NotificationType>?   // null = todas
)

private val FILTER_TABS = listOf(
    FilterTab("Todas",       Icons.Default.Notifications,   null),
    FilterTab("Likes",       Icons.Default.Favorite,        listOf(NotificationType.LIKE)),
    FilterTab("Comentarios", Icons.Default.ChatBubble,      listOf(NotificationType.COMMENT)),
    FilterTab("Seguidores",  Icons.Default.PersonAdd,       listOf(NotificationType.FOLLOW)),
    FilterTab("Ventas",      Icons.Default.ShoppingBag,     listOf(NotificationType.SALE)),
    FilterTab("Sin leer",    Icons.Default.MarkEmailUnread, null)   // special case
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    // Mutable state so we can mark read / delete
    var notifications by remember { mutableStateOf(MockData.notifications.toMutableList().toList()) }
    var selectedFilter by remember { mutableStateOf(0) }

    // Derived filtered list
    val filtered = remember(notifications, selectedFilter) {
        val tab = FILTER_TABS[selectedFilter]
        when {
            tab.label == "Sin leer"  -> notifications.filter { !it.isRead }
            tab.types != null        -> notifications.filter { it.type in tab.types }
            else                     -> notifications
        }
    }

    // Unread counts per filter tab
    val unreadCounts = remember(notifications) {
        FILTER_TABS.map { tab ->
            when {
                tab.label == "Sin leer" -> 0
                tab.types != null       -> notifications.count { it.type in tab.types && !it.isRead }
                else                    -> notifications.count { !it.isRead }
            }
        }
    }

    val todayNotifs    = filtered.filter { System.currentTimeMillis() - it.createdAt < 86400000L }
    val earlierNotifs  = filtered.filter { System.currentTimeMillis() - it.createdAt >= 86400000L }

    // Collapsed sections state
    var todayExpanded   by remember { mutableStateOf(true) }
    var earlierExpanded by remember { mutableStateOf(true) }

    // Mark all read
    fun markAllRead() {
        notifications = notifications.map { it.copy(isRead = true) }
    }
    // Mark single read
    fun markRead(id: String) {
        notifications = notifications.map { if (it.id == id) it.copy(isRead = true) else it }
    }
    // Delete
    fun delete(id: String) {
        notifications = notifications.filter { it.id != id }
    }
    // Toggle follow (simulated via notification state - no real follow model here)

    Column(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {

        // ── Gradient Header ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Emerald600, Teal500, Cyan400)))
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick  = onBack,
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(0.2f))
                        ) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Notificaciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White)
                            val unread = notifications.count { !it.isRead }
                            if (unread > 0) Text("$unread sin leer", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.85f))
                        }
                    }
                    // Mark all read button
                    Surface(
                        modifier  = Modifier.clickable { markAllRead() },
                        shape     = RoundedCornerShape(12.dp),
                        color     = Color.White.copy(0.2f)
                    ) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.DoneAll, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Leer todo", style = MaterialTheme.typography.labelMedium, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Filter chips row ─────────────────────────────────────────
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding        = PaddingValues(end = 8.dp)
                ) {
                    items(FILTER_TABS.indices.toList()) { i ->
                        val tab   = FILTER_TABS[i]
                        val sel   = selectedFilter == i
                        val count = unreadCounts[i]
                        val bg by animateColorAsState(
                            if (sel) Color.White else Color.White.copy(0.2f), tween(200), label = "filterBg$i"
                        )
                        Box {
                            Surface(
                                modifier  = Modifier.clickable { selectedFilter = i },
                                shape     = RoundedCornerShape(20.dp),
                                color     = bg
                            ) {
                                Row(
                                    modifier          = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        tab.icon, null,
                                        tint     = if (sel) Emerald600 else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        tab.label,
                                        style      = MaterialTheme.typography.labelMedium,
                                        color      = if (sel) Emerald700 else Color.White,
                                        fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                            // Unread badge on chip
                            if (count > 0 && tab.label != "Sin leer") {
                                Box(
                                    modifier         = Modifier
                                        .size(16.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp)
                                        .clip(CircleShape)
                                        .background(Rose500),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$count", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Notifications list ────────────────────────────────────────────────
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.NotificationsOff, null, tint = Stone300, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Sin notificaciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Stone500)
                    Text("No hay nada en esta categoría", style = MaterialTheme.typography.bodySmall, color = Stone400)
                }
            }
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // ── Today section ─────────────────────────────────────────────
                if (todayNotifs.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title    = "Hoy",
                            count    = todayNotifs.size,
                            expanded = todayExpanded,
                            onToggle = { todayExpanded = !todayExpanded }
                        )
                    }
                    if (todayExpanded) {
                        items(todayNotifs, key = { it.id }) { notif ->
                            SwipeableNotificationItem(
                                notification = notif,
                                onRead       = { markRead(notif.id) },
                                onDelete     = { delete(notif.id) }
                            )
                        }
                    }
                }

                // ── Earlier section ───────────────────────────────────────────
                if (earlierNotifs.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title    = "Anteriores",
                            count    = earlierNotifs.size,
                            expanded = earlierExpanded,
                            onToggle = { earlierExpanded = !earlierExpanded }
                        )
                    }
                    if (earlierExpanded) {
                        items(earlierNotifs, key = { it.id }) { notif ->
                            SwipeableNotificationItem(
                                notification = notif,
                                onRead       = { markRead(notif.id) },
                                onDelete     = { delete(notif.id) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// ─── Collapsible Section Header ───────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, count: Int, expanded: Boolean, onToggle: () -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Stone700)
            Spacer(modifier = Modifier.width(8.dp))
            Surface(shape = RoundedCornerShape(8.dp), color = Stone200) {
                Text("$count", style = MaterialTheme.typography.labelSmall, color = Stone600, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
        val rotation by animateFloatAsState(if (expanded) 0f else -90f, label = "arrow")
        Icon(
            Icons.Default.KeyboardArrowDown, null,
            tint     = Stone400,
            modifier = Modifier.size(18.dp).scale(1f)
        )
    }
}

// ─── Swipeable wrapper ────────────────────────────────────────────────────────
@Composable
private fun SwipeableNotificationItem(
    notification: Notification,
    onRead: () -> Unit,
    onDelete: () -> Unit
) {
    var offsetX     by remember { mutableFloatStateOf(0f) }
    var isDismissed by remember { mutableStateOf(false) }
    val threshold   = -200f

    AnimatedVisibility(
        visible = !isDismissed,
        exit    = shrinkVertically(tween(300)) + fadeOut(tween(300))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Background action (shown when swiping left)
            Row(
                modifier              = Modifier
                    .fillMaxSize()
                    .background(if (offsetX < -60f) Rose500 else Stone100)
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Delete, null, tint = if (offsetX < -60f) Color.White else Stone400, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Eliminar", style = MaterialTheme.typography.labelMedium, color = if (offsetX < -60f) Color.White else Stone400)
            }

            // Foreground notification card
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (offsetX < threshold) {
                                    isDismissed = true
                                    onDelete()
                                } else {
                                    offsetX = 0f
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                offsetX = (offsetX + dragAmount).coerceIn(-300f, 0f)
                            }
                        )
                    }
            ) {
                NotificationItem(
                    notification = notification,
                    onTap        = onRead
                )
            }
        }
    }
}

// ─── Notification Item ────────────────────────────────────────────────────────
@Composable
private fun NotificationItem(notification: Notification, onTap: () -> Unit) {
    val (icon, iconColor, iconBg) = getNotifStyle(notification.type)
    var isFollowing by remember { mutableStateOf(false) }

    Surface(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap),
        color     = if (notification.isRead) Color.White else Emerald50
    ) {
        Column {
            Row(
                modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Big icon badge + avatar
                Box(modifier = Modifier.size(52.dp)) {
                    UserAvatar(imageUrl = notification.fromUser.avatarUrl, size = 48.dp)
                    // Icon badge
                    Box(
                        modifier         = Modifier
                            .size(22.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(iconColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = Color.White, modifier = Modifier.size(13.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Text content
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            notification.fromUser.name,
                            style      = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.weight(1f, fill = false)
                        )
                        Text(
                            getTimeAgo(notification.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (!notification.isRead) Emerald600 else Stone400
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        notification.message,
                        style    = MaterialTheme.typography.bodySmall,
                        color    = Stone600,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Follow button for FOLLOW type
                    if (notification.type == NotificationType.FOLLOW) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val btnBg by animateColorAsState(if (isFollowing) Stone100 else Emerald500, label = "followBg")
                        val btnTx by animateColorAsState(if (isFollowing) Stone700 else Color.White, label = "followTx")
                        Surface(
                            modifier  = Modifier.clickable { isFollowing = !isFollowing },
                            shape     = RoundedCornerShape(10.dp),
                            color     = btnBg
                        ) {
                            Text(
                                if (isFollowing) "Siguiendo ✓" else "Seguir",
                                style      = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color      = btnTx,
                                modifier   = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Related image or unread dot
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    notification.relatedImageUrl?.let { url ->
                        Spacer(modifier = Modifier.width(10.dp))
                        AsyncImage(
                            model              = url,
                            contentDescription = null,
                            modifier           = Modifier.size(46.dp).clip(RoundedCornerShape(10.dp)),
                            contentScale       = ContentScale.Crop
                        )
                    }
                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Emerald500))
                    }
                }
            }
            HorizontalDivider(color = Stone100, modifier = Modifier.padding(start = 80.dp))
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
private data class NotifStyle(val icon: ImageVector, val color: Color, val bg: Color)

private fun getNotifStyle(type: NotificationType): NotifStyle = when (type) {
    NotificationType.LIKE     -> NotifStyle(Icons.Filled.Favorite,      Rose500,   Rose500.copy(0.12f))
    NotificationType.COMMENT  -> NotifStyle(Icons.Filled.ChatBubble,    Blue500,   Blue500.copy(0.12f))
    NotificationType.FOLLOW   -> NotifStyle(Icons.Filled.PersonAdd,     Violet500, Violet500.copy(0.12f))
    NotificationType.SHARE    -> NotifStyle(Icons.Filled.Share,         Emerald500,Emerald500.copy(0.12f))
    NotificationType.SALE     -> NotifStyle(Icons.Filled.ShoppingBag,   Amber500,  Amber500.copy(0.12f))
    NotificationType.REVIEW   -> NotifStyle(Icons.Filled.Star,          Amber400,  Amber400.copy(0.12f))
    NotificationType.MENTION  -> NotifStyle(Icons.Filled.AlternateEmail,Pink500,   Pink500.copy(0.12f))
    NotificationType.SHIPPING -> NotifStyle(Icons.Filled.LocalShipping, Cyan500,   Cyan500.copy(0.12f))
}

private fun getTimeAgo(timestamp: Long): String {
    val diff    = System.currentTimeMillis() - timestamp
    val minutes = diff / 60000
    val hours   = diff / 3600000
    val days    = diff / 86400000
    return when {
        minutes < 1  -> "Ahora"
        minutes < 60 -> "${minutes}m"
        hours < 24   -> "${hours}h"
        days < 7     -> "${days}d"
        else         -> "${days / 7}sem"
    }
}