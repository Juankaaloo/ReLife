package com.relife.ui.messages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.relife.data.model.Conversation
import com.relife.data.model.Message
import com.relife.data.model.MessageType
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class ConvFilter { ALL, UNREAD, ARCHIVED }
private val REACTIONS = listOf("❤️", "😂", "😮", "😢", "👍", "🔥")

@Composable
fun MessagesScreen(onBack: () -> Unit) {
    var searchQuery          by remember { mutableStateOf("") }
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }
    val conversations        = remember { MockData.conversations }

    AnimatedContent(
        targetState = selectedConversation,
        transitionSpec = {
            if (targetState != null)
                (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it / 3 } + fadeOut())
            else
                (slideInHorizontally { -it / 3 } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
        },
        label = "chatNav"
    ) { conv ->
        if (conv != null) {
            ChatScreen(conversation = conv, onBack = { selectedConversation = null })
        } else {
            ConversationListScreen(
                conversations  = conversations,
                searchQuery    = searchQuery,
                onSearchChange = { searchQuery = it },
                onBack         = onBack,
                onConvClick    = { selectedConversation = it }
            )
        }
    }
}

// ─── Conversation List ────────────────────────────────────────────────────────
@Composable
private fun ConversationListScreen(
    conversations: List<Conversation>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onBack: () -> Unit,
    onConvClick: (Conversation) -> Unit
) {
    var activeFilter by remember { mutableStateOf(ConvFilter.ALL) }
    val totalUnread  = conversations.sumOf { it.unreadCount }

    val filtered = remember(searchQuery, activeFilter, conversations) {
        conversations.filter { conv ->
            val matchSearch = searchQuery.isEmpty() || conv.participant.name.contains(searchQuery, true)
            val matchFilter = when (activeFilter) {
                ConvFilter.ALL      -> true
                ConvFilter.UNREAD   -> conv.unreadCount > 0
                ConvFilter.ARCHIVED -> false
            }
            matchSearch && matchFilter
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
        // Gradient header
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Emerald600, Teal500, Cyan400)))) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier         = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(0.2f)).clickable(onClick = onBack),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Mensajes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
                            Text(if (totalUnread > 0) "$totalUnread sin leer" else "Sin mensajes nuevos", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.8f))
                        }
                    }
                    Box(
                        modifier         = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(0.2f)).clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Edit, "Nuevo", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                // Search
                Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = Stone400, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        androidx.compose.foundation.text.BasicTextField(
                            value         = searchQuery,
                            onValueChange = onSearchChange,
                            modifier      = Modifier.weight(1f).padding(vertical = 9.dp),
                            textStyle     = MaterialTheme.typography.bodyMedium.copy(color = Stone800),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) Text("Buscar chats...", style = MaterialTheme.typography.bodyMedium, color = Stone400)
                                inner()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            Icon(Icons.Default.Close, null, tint = Stone400, modifier = Modifier.size(16.dp).clickable { onSearchChange("") })
                        }
                    }
                }
            }
        }

        // Filter chips
        Surface(color = Color.White, shadowElevation = 2.dp) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(ConvFilter.ALL to "Todos", ConvFilter.UNREAD to "Sin leer", ConvFilter.ARCHIVED to "Archivados")
                    .forEach { (filter, label) ->
                        val sel      = activeFilter == filter
                        val badge    = if (filter == ConvFilter.UNREAD) totalUnread else 0
                        val bg by animateColorAsState(if (sel) Emerald500 else Stone100, tween(200), label = "fBg$filter")
                        Surface(modifier = Modifier.clickable { activeFilter = filter }, shape = RoundedCornerShape(20.dp), color = bg) {
                            Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(label, style = MaterialTheme.typography.labelMedium, color = if (sel) Color.White else Stone600, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                if (badge > 0 && !sel) {
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Rose500), contentAlignment = Alignment.Center) {
                                        Text("$badge", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Stone300, modifier = Modifier.size(52.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Sin conversaciones", style = MaterialTheme.typography.titleMedium, color = Stone500)
                }
            }
        } else {
            LazyColumn {
                items(filtered, key = { it.id }) { conv ->
                    ConversationItem(conversation = conv, onClick = { onConvClick(conv) })
                    HorizontalDivider(color = Stone100, modifier = Modifier.padding(start = 84.dp))
                }
            }
        }
    }
}

// ─── Conversation Item ────────────────────────────────────────────────────────
@Composable
private fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    val isUnread = conversation.unreadCount > 0
    Row(
        modifier          = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .background(if (isUnread) Emerald50.copy(0.5f) else Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(imageUrl = conversation.participant.avatarUrl, size = 54.dp, showOnlineBadge = true, isOnline = conversation.isOnline)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(conversation.participant.name, style = MaterialTheme.typography.titleSmall, fontWeight = if (isUnread) FontWeight.ExtraBold else FontWeight.SemiBold)
                Text(getTimeAgo(conversation.lastMessageTime), style = MaterialTheme.typography.labelSmall, color = if (isUnread) Emerald600 else Stone400, fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    conversation.lastMessage,
                    style      = MaterialTheme.typography.bodySmall,
                    color      = if (isUnread) Stone700 else Stone400,
                    fontWeight = if (isUnread) FontWeight.Medium else FontWeight.Normal,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.weight(1f)
                )
                if (isUnread) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Emerald500), contentAlignment = Alignment.Center) {
                        Text("${conversation.unreadCount}", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

// ─── Chat Screen ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreen(conversation: Conversation, onBack: () -> Unit) {
    var messageText   by remember { mutableStateOf("") }
    var isTyping      by remember { mutableStateOf(false) }
    val listState     = rememberLazyListState()
    val scope         = rememberCoroutineScope()
    val reactions     = remember { mutableStateMapOf<String, String>() }
    var reactionTarget by remember { mutableStateOf<String?>(null) }

    val messages = remember {
        mutableStateListOf(
            Message("1", conversation.id, conversation.participant.id, "¡Hola! Vi tu publicación y me encanta 😍", createdAt = System.currentTimeMillis() - 3600000),
            Message("2", conversation.id, MockData.currentUser.id, "¡Muchas gracias! Me alegra que te guste 🙌", createdAt = System.currentTimeMillis() - 3500000),
            Message("3", conversation.id, conversation.participant.id, "¿Sigue disponible? ¿Cuánto pides por ello?", createdAt = System.currentTimeMillis() - 3400000),
            Message("4", conversation.id, MockData.currentUser.id, "Sí, está disponible. Son 45€ con envío incluido 📦", createdAt = System.currentTimeMillis() - 3300000),
            Message("5", conversation.id, conversation.participant.id, conversation.lastMessage, createdAt = conversation.lastMessageTime, isRead = true)
        )
    }

    LaunchedEffect(messageText) {
        if (messageText.isNotEmpty()) {
            delay(1800)
            isTyping = true
            delay(2200)
            isTyping = false
        }
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserAvatar(imageUrl = conversation.participant.avatarUrl, size = 38.dp, showOnlineBadge = true, isOnline = conversation.isOnline)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(conversation.participant.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    if (conversation.participant.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, null, tint = Emerald500, modifier = Modifier.size(14.dp))
                                    }
                                }
                                AnimatedContent(targetState = isTyping, label = "typing") { typing ->
                                    if (typing) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            TypingDots()
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text("escribiendo...", style = MaterialTheme.typography.labelSmall, color = Emerald500)
                                        }
                                    } else {
                                        Text(if (conversation.isOnline) "En línea" else "Desconectado", style = MaterialTheme.typography.labelSmall, color = if (conversation.isOnline) Emerald500 else Stone400)
                                    }
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                    },
                    actions = {
                        IconButton(onClick = { }) { Icon(Icons.Outlined.Phone, "Llamar", tint = Emerald600) }
                        IconButton(onClick = { }) { Icon(Icons.Default.MoreVert, "Más") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        },
        bottomBar = {
            ChatInput(
                value         = messageText,
                onValueChange = { messageText = it },
                onSend        = {
                    if (messageText.isNotBlank()) {
                        messages.add(Message(
                            id             = "${messages.size + 1}",
                            conversationId = conversation.id,
                            senderId       = MockData.currentUser.id,
                            content        = messageText,
                            isSent         = true
                        ))
                        messageText = ""
                        scope.launch { listState.animateScrollToItem(0) }
                    }
                }
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                state               = listState,
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                reverseLayout       = true
            ) {
                item {
                    AnimatedVisibility(visible = isTyping, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                        TypingBubble(avatarUrl = conversation.participant.avatarUrl)
                    }
                }
                items(messages.reversed(), key = { it.id }) { message ->
                    MessageBubble(
                        message      = message,
                        isOwn        = message.senderId == MockData.currentUser.id,
                        reaction     = reactions[message.id],
                        onLongPress  = { reactionTarget = message.id }
                    )
                }
            }

            // Reaction picker
            if (reactionTarget != null) {
                Box(
                    modifier         = Modifier.fillMaxSize().background(Color.Black.copy(0.35f)).clickable { reactionTarget = null },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(shape = RoundedCornerShape(32.dp), color = Color.White, shadowElevation = 16.dp) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            REACTIONS.forEach { emoji ->
                                Text(
                                    emoji, fontSize = 28.sp,
                                    modifier = Modifier.clip(CircleShape).clickable {
                                        reactionTarget?.let { id ->
                                            if (reactions[id] == emoji) reactions.remove(id) else reactions[id] = emoji
                                        }
                                        reactionTarget = null
                                    }.padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Message Bubble ───────────────────────────────────────────────────────────
@Composable
private fun MessageBubble(message: Message, isOwn: Boolean, reaction: String?, onLongPress: () -> Unit) {
    val bubbleShape = if (isOwn)
        RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
    else
        RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .then(
                    if (isOwn) Modifier.background(Brush.linearGradient(listOf(Emerald500, Teal500)))
                    else Modifier.background(Color.White)
                )
                .pointerInput(Unit) { detectTapGestures(onLongPress = { onLongPress() }) }
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                when (message.type) {
                    MessageType.IMAGE -> {
                        message.attachmentUrl?.let { url ->
                            AsyncImage(model = url, contentDescription = null, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                            if (message.content.isNotEmpty()) Spacer(modifier = Modifier.height(6.dp))
                        }
                        if (message.content.isNotEmpty()) Text(message.content, style = MaterialTheme.typography.bodyMedium, color = if (isOwn) Color.White else Stone800)
                    }
                    else -> Text(message.content, style = MaterialTheme.typography.bodyMedium, color = if (isOwn) Color.White else Stone800)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                    Text(getMessageTime(message.createdAt), style = MaterialTheme.typography.labelSmall, color = if (isOwn) Color.White.copy(0.75f) else Stone400, fontSize = 10.sp)
                    if (isOwn && message.isSent) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done, null, tint = if (message.isRead) Cyan300 else Color.White.copy(0.7f), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
        // Reaction badge
        if (reaction != null) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.offset(x = if (isOwn) (-8).dp else 8.dp, y = (-4).dp)) {
                Text(reaction, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}

// ─── Typing Bubble ────────────────────────────────────────────────────────────
@Composable
private fun TypingBubble(avatarUrl: String?) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Bottom) {
        UserAvatar(imageUrl = avatarUrl, size = 28.dp)
        Spacer(modifier = Modifier.width(6.dp))
        Surface(shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp), color = Color.White, shadowElevation = 1.dp) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                TypingDots()
            }
        }
    }
}

@Composable
private fun TypingDots() {
    val inf = rememberInfiniteTransition(label = "dots")
    (0..2).forEach { i ->
        val alpha by inf.animateFloat(
            initialValue  = 0.3f,
            targetValue   = 1f,
            animationSpec = infiniteRepeatable(
                animation  = keyframes { durationMillis = 900; 0.3f at 0; 1f at 200 + i * 120; 0.3f at 600 },
                repeatMode = RepeatMode.Restart
            ),
            label = "dot$i"
        )
        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Stone400.copy(alpha)))
    }
}

// ─── Chat Input ───────────────────────────────────────────────────────────────
@Composable
private fun ChatInput(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    var showEmojiBar by remember { mutableStateOf(false) }

    Surface(color = Color.White, shadowElevation = 8.dp) {
        Column {
            AnimatedVisibility(visible = showEmojiBar, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                LazyRow(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(listOf("😀","😂","❤️","👍","🔥","🎉","😍","🙌","✨","💬","🌿","🪴")) { emoji ->
                        Text(emoji, fontSize = 22.sp, modifier = Modifier.clip(CircleShape).clickable { onValueChange(value + emoji) }.padding(4.dp))
                    }
                }
            }
            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp).fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                IconButton(onClick = { showEmojiBar = !showEmojiBar }, modifier = Modifier.size(42.dp)) {
                    Icon(if (showEmojiBar) Icons.Default.KeyboardAlt else Icons.Outlined.EmojiEmotions, null, tint = if (showEmojiBar) Emerald500 else Stone500)
                }
                IconButton(onClick = { }, modifier = Modifier.size(42.dp)) { Icon(Icons.Outlined.AttachFile, "Adjuntar", tint = Stone500) }
                IconButton(onClick = { }, modifier = Modifier.size(42.dp)) { Icon(Icons.Outlined.CameraAlt, "Cámara", tint = Stone500) }
                Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), color = Stone50) {
                    androidx.compose.foundation.text.BasicTextField(
                        value         = value,
                        onValueChange = onValueChange,
                        modifier      = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                        textStyle     = MaterialTheme.typography.bodyMedium.copy(color = Stone800),
                        maxLines      = 4,
                        decorationBox = { inner ->
                            if (value.isEmpty()) Text("Escribe un mensaje...", style = MaterialTheme.typography.bodyMedium, color = Stone400)
                            inner()
                        }
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                val canSend = value.isNotBlank()
                val btnBg by animateColorAsState(if (canSend) Emerald500 else Stone300, tween(200), label = "sendBg")
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(btnBg).clickable(enabled = canSend, onClick = onSend), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

private fun getTimeAgo(ts: Long): String {
    val d = System.currentTimeMillis() - ts
    return when {
        d < 60000    -> "Ahora"
        d < 3600000  -> "${d / 60000}m"
        d < 86400000 -> "${d / 3600000}h"
        else         -> "${d / 86400000}d"
    }
}

private fun getMessageTime(ts: Long): String {
    val c = java.util.Calendar.getInstance().apply { timeInMillis = ts }
    return String.format("%02d:%02d", c.get(java.util.Calendar.HOUR_OF_DAY), c.get(java.util.Calendar.MINUTE))
}