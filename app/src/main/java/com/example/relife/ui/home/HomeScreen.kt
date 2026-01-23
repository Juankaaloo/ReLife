package com.relife.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.relife.data.model.Post
import com.relife.data.repository.MockData
import com.relife.data.repository.Story
import com.relife.ui.components.*
import com.relife.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToMessages: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val posts = remember { MockData.posts }
    val stories = remember { MockData.stories }
    val trendingTags = remember { MockData.trendingTags }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header
        HomeHeader(
            userName = MockData.currentUser.name.split(" ").first(),
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onNotificationsClick = onNavigateToNotifications,
            onMessagesClick = onNavigateToMessages
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stories section
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                StoriesSection(stories = stories)
            }
            
            // Trending tags
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                TrendingTagsSection(tags = trendingTags)
            }
            
            // Tabs
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Emerald600,
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .padding(horizontal = 40.dp)
                                .background(Emerald500, CircleShape)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Tendencias",
                                fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = Emerald600,
                        unselectedContentColor = Stone400
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Seguidos",
                                fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = Emerald600,
                        unselectedContentColor = Stone400
                    )
                }
            }
            
            // Posts grid
            items(posts) { post ->
                PostCard(
                    post = post,
                    onClick = { /* Navigate to detail */ },
                    onLikeClick = { /* Toggle like */ }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hola, $userName 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¿Qué vas a crear hoy?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Stone500
                    )
                }
                
                Row {
                    IconButton(onClick = onMessagesClick) {
                        BadgedBox(
                            badge = { Badge(count = 3) }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = "Mensajes",
                                tint = Stone600
                            )
                        }
                    }
                    
                    IconButton(onClick = onNotificationsClick) {
                        BadgedBox(
                            badge = { Badge(count = 5) }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Stone600
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SearchBar(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = "Buscar proyectos, creadores...",
                leadingIcon = Icons.Default.Search
            )
        }
    }
}

@Composable
private fun StoriesSection(stories: List<Story>) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Add story button
            item {
                AddStoryButton()
            }
            
            items(stories) { story ->
                StoryItem(story = story)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AddStoryButton() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Stone100)
                .clickable { /* Add story */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir historia",
                tint = Emerald500,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Tu historia",
            style = MaterialTheme.typography.labelSmall,
            color = Stone600
        )
    }
}

@Composable
private fun StoryItem(story: Story) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserAvatar(
            imageUrl = story.user.avatarUrl,
            size = 60.dp,
            showStoryBorder = true,
            hasUnseenStory = !story.isViewed
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = story.user.name.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            color = Stone600,
            maxLines = 1
        )
    }
}

@Composable
private fun TrendingTagsSection(tags: List<Pair<String, Int>>) {
    Column {
        SectionHeader(
            title = "Trending",
            action = "Ver todo",
            onActionClick = { /* Navigate */ }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.take(5).forEach { (tag, count) ->
                TrendingTagCard(tag = tag, count = count)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TrendingTagCard(tag: String, count: Int) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { /* Navigate to tag */ },
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Emerald100, Teal100)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#",
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald600,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = tag,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "$count posts",
                style = MaterialTheme.typography.bodySmall,
                color = Stone500
            )
        }
    }
}
