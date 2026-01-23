package com.relife.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.relife.data.model.Post
import com.relife.data.model.Product
import com.relife.ui.theme.*

@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(post.isLiked) }
    val likeScale by animateFloatAsState(
        targetValue = if (isLiked) 1.2f else 1f,
        animationSpec = tween(150),
        label = "likeScale"
    )
    val likeColor by animateColorAsState(
        targetValue = if (isLiked) Rose500 else Stone400,
        animationSpec = tween(150),
        label = "likeColor"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image
            Box(modifier = Modifier.aspectRatio(1f)) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = post.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)),
                                startY = 200f
                            )
                        )
                )
                
                // Like button overlay
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        onLikeClick()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = likeColor,
                        modifier = Modifier.scale(likeScale)
                    )
                }
            }
            
            // Content
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Author info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(
                            imageUrl = post.author.avatarUrl,
                            size = 24.dp,
                            showVerifiedBadge = post.author.isVerified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = post.author.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone600
                        )
                    }
                    
                    // Stats
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Stone400,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.likesCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone500
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Icon(
                            imageVector = Icons.Filled.ChatBubble,
                            contentDescription = null,
                            tint = Stone400,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.commentsCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone500
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(product.isFavorite) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image
            Box(modifier = Modifier.aspectRatio(1f)) {
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Shipping badge
                if (product.shippingAvailable) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = ChipShape,
                        color = Emerald500
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Envío",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
                
                // Favorite button
                IconButton(
                    onClick = {
                        isFavorite = !isFavorite
                        onFavoriteClick()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Rose500 else Stone400
                    )
                }
            }
            
            // Content
            Column(modifier = Modifier.padding(12.dp)) {
                // Price
                Text(
                    text = "€${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Emerald600
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Seller info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(
                        imageUrl = product.seller.avatarUrl,
                        size = 20.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = product.seller.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Rating
                    if (product.rating > 0) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Amber500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format("%.1f", product.rating),
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone600
                        )
                    }
                }
                
                // Location
                product.location?.let { location ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Stone400,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone500
                        )
                    }
                }
            }
        }
    }
}
