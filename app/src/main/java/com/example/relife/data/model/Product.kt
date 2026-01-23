package com.relife.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String = "EUR",
    val images: List<String>,
    val seller: UserPreview,
    val category: PostCategory = PostCategory.ALL,
    val condition: ProductCondition = ProductCondition.RESTORED,
    val shippingAvailable: Boolean = false,
    val location: String? = null,
    val rating: Float = 0f,
    val reviewsCount: Int = 0,
    val isFavorite: Boolean = false,
    val isSold: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ProductCondition(val displayName: String) {
    NEW("Nuevo"),
    RESTORED("Restaurado"),
    USED("Usado")
}
