package com.relife.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.relife.data.model.Post
import com.relife.data.model.PostCategory
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

@Composable
fun ExploreScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PostCategory.ALL) }
    val posts = remember { MockData.posts }
    val featuredCreators = remember { MockData.users }
    val popularTags = remember { MockData.trendingTags }
    
    Row(modifier = Modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(BackgroundLight)
        ) {
            // Header with search
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Explorar",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Buscar inspiración...",
                        leadingIcon = Icons.Default.Search
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Category filters
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryChip(
                            category = PostCategory.ALL,
                            icon = Icons.Default.Dashboard,
                            selected = selectedCategory == PostCategory.ALL,
                            onClick = { selectedCategory = PostCategory.ALL }
                        )
                        CategoryChip(
                            category = PostCategory.FURNITURE,
                            icon = Icons.Default.Chair,
                            selected = selectedCategory == PostCategory.FURNITURE,
                            onClick = { selectedCategory = PostCategory.FURNITURE }
                        )
                        CategoryChip(
                            category = PostCategory.LIGHTING,
                            icon = Icons.Default.Lightbulb,
                            selected = selectedCategory == PostCategory.LIGHTING,
                            onClick = { selectedCategory = PostCategory.LIGHTING }
                        )
                        CategoryChip(
                            category = PostCategory.DECORATION,
                            icon = Icons.Default.Palette,
                            selected = selectedCategory == PostCategory.DECORATION,
                            onClick = { selectedCategory = PostCategory.DECORATION }
                        )
                        CategoryChip(
                            category = PostCategory.FASHION,
                            icon = Icons.Default.Checkroom,
                            selected = selectedCategory == PostCategory.FASHION,
                            onClick = { selectedCategory = PostCategory.FASHION }
                        )
                        CategoryChip(
                            category = PostCategory.GARDEN,
                            icon = Icons.Default.Yard,
                            selected = selectedCategory == PostCategory.GARDEN,
                            onClick = { selectedCategory = PostCategory.GARDEN }
                        )
                        CategoryChip(
                            category = PostCategory.TECH,
                            icon = Icons.Default.PhoneAndroid,
                            selected = selectedCategory == PostCategory.TECH,
                            onClick = { selectedCategory = PostCategory.TECH }
                        )
                    }
                }
            }
            
            // Masonry grid
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                items(
                    posts.filter { 
                        selectedCategory == PostCategory.ALL || it.category == selectedCategory 
                    }
                ) { post ->
                    MasonryPostCard(post = post)
                }
            }
        }
        
        // Sidebar
        ExploreSidebar(
            creators = featuredCreators,
            tags = popularTags
        )
    }
}

@Composable
private fun CategoryChip(
    category: PostCategory,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    ReLifeChip(
        text = category.displayName,
        selected = selected,
        onClick = onClick,
        icon = icon
    )
}

@Composable
private fun MasonryPostCard(post: Post) {
    val heights = listOf(180.dp, 220.dp, 260.dp, 200.dp)
    val randomHeight = remember { heights.random() }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(randomHeight)
            ) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = post.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay with stats
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                ChipShape
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.likesCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(
                        imageUrl = post.author.avatarUrl,
                        size = 20.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = post.author.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone500
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreSidebar(
    creators: List<com.relife.data.model.UserPreview>,
    tags: List<Pair<String, Int>>
) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        ) {
            // Featured creators
            SectionHeader(
                title = "Creadores destacados",
                action = "Ver todos",
                onActionClick = { }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            creators.take(4).forEach { creator ->
                CreatorItem(creator = creator)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Stone200)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Popular tags
            SectionHeader(title = "Tags populares")
            
            Spacer(modifier = Modifier.height(12.dp))
            
            tags.take(6).forEach { (tag, count) ->
                TagChip(
                    tag = tag,
                    onClick = { },
                    count = count,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // CTA to create post
            PrimaryButton(
                text = "Crear publicación",
                onClick = { },
                icon = Icons.Default.Add,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CreatorItem(creator: com.relife.data.model.UserPreview) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            imageUrl = creator.avatarUrl,
            size = 44.dp,
            showVerifiedBadge = creator.isVerified,
            showOnlineBadge = true,
            isOnline = creator.isOnline
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = creator.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                if (creator.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = "Verificado",
                        tint = Emerald500,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                text = "${creator.followersCount} seguidores",
                style = MaterialTheme.typography.bodySmall,
                color = Stone500
            )
        }
        
        OutlinedButton(
            onClick = { },
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            shape = ChipShape
        ) {
            Text(
                text = "Seguir",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
