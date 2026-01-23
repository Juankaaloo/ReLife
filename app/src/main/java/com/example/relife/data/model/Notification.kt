package com.relife.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Notification(
    val id: String,
    val type: NotificationType,
    val fromUser: UserPreview,
    val message: String,
    val relatedImageUrl: String? = null,
    val relatedPostId: String? = null,
    val relatedProductId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class NotificationType(val displayName: String) {
    LIKE("Me gusta"),
    COMMENT("Comentario"),
    FOLLOW("Seguidor"),
    SHARE("Compartido"),
    SALE("Venta"),
    REVIEW("Reseña"),
    MENTION("Mención"),
    SHIPPING("Envío")
}
