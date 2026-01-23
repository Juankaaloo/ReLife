package com.relife.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.relife.ui.theme.*

@Composable
fun UserAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showOnlineBadge: Boolean = false,
    isOnline: Boolean = false,
    showVerifiedBadge: Boolean = false,
    showStoryBorder: Boolean = false,
    hasUnseenStory: Boolean = false
) {
    Box(modifier = modifier.size(size + if (showStoryBorder) 8.dp else 0.dp)) {
        // Story border gradient
        if (showStoryBorder) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        brush = if (hasUnseenStory) {
                            Brush.linearGradient(StoryGradientColors)
                        } else {
                            Brush.linearGradient(listOf(Stone300, Stone300))
                        }
                    )
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                ) {
                    AvatarImage(imageUrl = imageUrl, size = size)
                }
            }
        } else {
            AvatarImage(imageUrl = imageUrl, size = size)
        }
        
        // Online badge
        if (showOnlineBadge && isOnline) {
            Box(
                modifier = Modifier
                    .size(size / 4)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-2).dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(Success)
            )
        }
        
        // Verified badge
        if (showVerifiedBadge) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verificado",
                tint = Emerald500,
                modifier = Modifier
                    .size(size / 3)
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun AvatarImage(
    imageUrl: String?,
    size: Dp
) {
    if (imageUrl != null) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        listOf(Emerald400, Teal400)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Default avatar placeholder
        }
    }
}

@Composable
fun AvatarGroup(
    imageUrls: List<String?>,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 32.dp,
    maxDisplay: Int = 3,
    overlap: Dp = 12.dp
) {
    Row(modifier = modifier) {
        val displayUrls = imageUrls.take(maxDisplay)
        displayUrls.forEachIndexed { index, url ->
            Box(
                modifier = Modifier
                    .offset(x = (-overlap) * index)
                    .border(2.dp, Color.White, CircleShape)
            ) {
                UserAvatar(imageUrl = url, size = avatarSize)
            }
        }
        
        if (imageUrls.size > maxDisplay) {
            Box(
                modifier = Modifier
                    .offset(x = (-overlap) * maxDisplay)
                    .size(avatarSize)
                    .clip(CircleShape)
                    .background(Stone200)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = "+${imageUrls.size - maxDisplay}",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = Stone600
                )
            }
        }
    }
}
