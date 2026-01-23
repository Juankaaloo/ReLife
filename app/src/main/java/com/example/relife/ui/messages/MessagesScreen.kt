package com.relife.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.relife.data.model.Conversation
import com.relife.data.model.Message
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }
    val conversations = remember { MockData.conversations }
    
    if (selectedConversation != null) {
        ChatScreen(
            conversation = selectedConversation!!,
            onBack = { selectedConversation = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mensajes", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* New message */ }) {
                            Icon(Icons.Outlined.Edit, "Nuevo mensaje")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = BackgroundLight
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                // Search
                SearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Buscar chats...",
                    leadingIcon = Icons.Default.Search,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                
                // Conversations list
                LazyColumn {
                    items(conversations.filter { 
                        searchQuery.isEmpty() || it.participant.name.contains(searchQuery, true) 
                    }) { conversation ->
                        ConversationItem(
                            conversation = conversation,
                            onClick = { selectedConversation = conversation }
                        )
                        HorizontalDivider(color = Stone100)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online indicator
        UserAvatar(
            imageUrl = conversation.participant.avatarUrl,
            size = 52.dp,
            showOnlineBadge = true,
            isOnline = conversation.isOnline
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = conversation.participant.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold
                )
                Text(
                    text = getTimeAgo(conversation.lastMessageTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (conversation.unreadCount > 0) Emerald600 else Stone500
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (conversation.unreadCount > 0) Stone800 else Stone500,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(count = conversation.unreadCount)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreen(
    conversation: Conversation,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        listOf(
            Message("1", conversation.id, conversation.participant.id, "¡Hola! Vi tu mesa de centro y me encanta", createdAt = System.currentTimeMillis() - 3600000),
            Message("2", conversation.id, MockData.currentUser.id, "¡Gracias! Sí, sigue disponible", createdAt = System.currentTimeMillis() - 3500000),
            Message("3", conversation.id, conversation.participant.id, "¿Podrías hacer envío a Barcelona?", createdAt = System.currentTimeMillis() - 3400000),
            Message("4", conversation.id, MockData.currentUser.id, "Claro, el envío serían 15€ adicionales", createdAt = System.currentTimeMillis() - 3300000),
            Message("5", conversation.id, conversation.participant.id, conversation.lastMessage, createdAt = conversation.lastMessageTime)
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(
                            imageUrl = conversation.participant.avatarUrl,
                            size = 36.dp,
                            showOnlineBadge = true,
                            isOnline = conversation.isOnline
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = conversation.participant.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (conversation.isOnline) "En línea" else "Desconectado",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (conversation.isOnline) Success else Stone500
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, "Más opciones")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            ChatInput(
                value = messageText,
                onValueChange = { messageText = it },
                onSend = { messageText = "" }
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(
                    message = message,
                    isOwn = message.senderId == MockData.currentUser.id
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isOwn: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = CardShape,
            colors = CardDefaults.cardColors(
                containerColor = if (isOwn) Emerald500 else Color.White
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isOwn) Color.White else Stone800
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getMessageTime(message.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOwn) Color.White.copy(alpha = 0.8f) else Stone400
                    )
                    if (isOwn && message.isSent) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                            contentDescription = null,
                            tint = if (isOwn) Color.White.copy(alpha = 0.8f) else Stone400,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Attach */ }) {
                Icon(Icons.Outlined.AttachFile, "Adjuntar", tint = Stone500)
            }
            IconButton(onClick = { /* Camera */ }) {
                Icon(Icons.Outlined.CameraAlt, "Cámara", tint = Stone500)
            }
            
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...", color = Stone400) },
                shape = SearchBarShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Emerald500,
                    unfocusedBorderColor = Stone200
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(48.dp)
                    .background(Emerald500, CircleShape)
            ) {
                Icon(Icons.Default.Send, "Enviar", tint = Color.White)
            }
        }
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / 60000
    val hours = diff / 3600000
    val days = diff / 86400000
    return when {
        minutes < 1 -> "Ahora"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        else -> "${days}d"
    }
}

private fun getMessageTime(timestamp: Long): String {
    val calendar = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
    return String.format("%02d:%02d", calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE))
}
