package com.relife.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val coverUrl: String? = null,
    val bio: String? = null,
    val website: String? = null,
    val isVerified: Boolean = false,
    val isOnline: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val totalLikes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class UserPreview(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String? = null,
    val isVerified: Boolean = false,
    val isOnline: Boolean = false,
    val followersCount: Int = 0
)
