package com.relife.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Conversation(
    val id: String,
    val participant: UserPreview,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false
)

@JsonClass(generateAdapter = true)
data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val type: MessageType = MessageType.TEXT,
    val attachmentUrl: String? = null,
    val isRead: Boolean = false,
    val isSent: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MessageType {
    TEXT,
    IMAGE,
    AUDIO,
    FILE
}
