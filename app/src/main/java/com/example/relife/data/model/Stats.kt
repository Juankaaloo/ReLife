package com.relife.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserStats(
    val views: Int = 0,
    val viewsChange: Float = 0f,
    val followers: Int = 0,
    val followersChange: Float = 0f,
    val totalLikes: Int = 0,
    val likesChange: Float = 0f,
    val comments: Int = 0,
    val commentsChange: Float = 0f,
    val salesStats: SalesStats = SalesStats(),
    val weeklyActivity: List<DayActivity> = emptyList(),
    val engagement: EngagementStats = EngagementStats(),
    val recentTransactions: List<Transaction> = emptyList(),
    val topProducts: List<ProductPreview> = emptyList(),
    val audienceInsights: AudienceInsights = AudienceInsights()
)

@JsonClass(generateAdapter = true)
data class SalesStats(
    val totalIncome: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val productsOnSale: Int = 0,
    val pendingOrders: Int = 0
)

@JsonClass(generateAdapter = true)
data class DayActivity(
    val day: String,
    val value: Int
)

@JsonClass(generateAdapter = true)
data class EngagementStats(
    val likesRate: Float = 0f,
    val commentsRate: Float = 0f,
    val sharesRate: Float = 0f,
    val savesRate: Float = 0f
)

@JsonClass(generateAdapter = true)
data class Transaction(
    val id: String,
    val productTitle: String,
    val amount: Double,
    val type: TransactionType,
    val date: Long
)

enum class TransactionType {
    SALE,
    PURCHASE
}

@JsonClass(generateAdapter = true)
data class ProductPreview(
    val id: String,
    val title: String,
    val imageUrl: String,
    val views: Int
)

@JsonClass(generateAdapter = true)
data class AudienceInsights(
    val topAgeRange: String = "25-34",
    val topCity: String = "Madrid",
    val bestHour: String = "19:00",
    val bestDay: String = "Sábado"
)
