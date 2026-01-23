package com.relife.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    val id: String,
    val title: String,
    val description: String? = null,
    val imageUrl: String,
    val author: UserPreview,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val tags: List<String> = emptyList(),
    val category: PostCategory = PostCategory.ALL,
    val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class Comment(
    val id: String,
    val postId: String,
    val author: UserPreview,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

enum class PostCategory(val displayName: String) {
    ALL("Todo"),
    FURNITURE("Muebles"),
    LIGHTING("Iluminación"),
    DECORATION("Decoración"),
    FASHION("Moda"),
    GARDEN("Jardín"),
    TECH("Tech")
}
