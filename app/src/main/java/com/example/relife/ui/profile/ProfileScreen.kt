package com.relife.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val user = remember { MockData.currentUser }
    val posts = remember { MockData.posts }
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header with cover
        Box(modifier = Modifier.fillMaxWidth()) {
            // Cover image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Emerald500, Teal500, Cyan500)
                        )
                    )
            ) {
                user.coverUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onNavigateToStats,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(Icons.Outlined.Analytics, "Estadísticas", tint = Stone700)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(Icons.Outlined.Settings, "Ajustes", tint = Stone700)
                }
            }
            
            // Avatar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 50.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp)
                ) {
                    UserAvatar(imageUrl = user.avatarUrl, size = 100.dp)
                }
                
                // Edit button
                IconButton(
                    onClick = { /* Edit avatar */ },
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .background(Emerald500, CircleShape)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        "Editar foto",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(60.dp))
        
        // User info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (user.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Filled.Verified,
                        "Verificado",
                        tint = Emerald500,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.bodyLarge,
                color = Stone500
            )
            
            user.bio?.let { bio ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Stone600,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(value = "${user.postsCount}", label = "Posts")
                ProfileStat(value = "${user.followersCount}", label = "Seguidores")
                ProfileStat(value = "${user.followingCount}", label = "Siguiendo")
                ProfileStat(value = "${user.totalLikes}", label = "Likes")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Emerald600,
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp)
                        .padding(horizontal = 60.dp)
                        .background(Emerald500, CircleShape)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Publicaciones", fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal) },
                icon = { Icon(if (selectedTab == 0) Icons.Filled.GridOn else Icons.Outlined.GridOn, null) },
                selectedContentColor = Emerald600,
                unselectedContentColor = Stone400
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Guardados", fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal) },
                icon = { Icon(if (selectedTab == 1) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder, null) },
                selectedContentColor = Emerald600,
                unselectedContentColor = Stone400
            )
        }
        
        // Posts grid
        if (selectedTab == 0) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(posts) { post ->
                    ProfilePostItem(imageUrl = post.imageUrl, likes = post.likesCount, comments = post.commentsCount)
                }
            }
        } else {
            val savedPosts = posts.filter { it.isSaved }
            if (savedPosts.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.BookmarkBorder,
                    title = "No hay guardados",
                    description = "Guarda tus publicaciones favoritas para verlas aquí",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(savedPosts) { post ->
                        ProfilePostItem(imageUrl = post.imageUrl, likes = post.likesCount, comments = post.commentsCount)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Stone500)
    }
}

@Composable
private fun ProfilePostItem(imageUrl: String, likes: Int, comments: Int) {
    Box(modifier = Modifier.aspectRatio(1f)) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Overlay on hover/press
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Favorite, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("$likes", color = Color.White, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Filled.ChatBubble, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("$comments", color = Color.White, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
