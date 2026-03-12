package com.relife.ui.explore

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.relife.data.model.Post
import com.relife.data.model.PostCategory
import com.relife.data.model.UserPreview
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

// View modes
private enum class ExploreView { GRID, LIST }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    isGuest: Boolean = false,
    onRequestLogin: () -> Unit = {}
) {
    var searchQuery      by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PostCategory.ALL) }
    var viewMode         by remember { mutableStateOf(ExploreView.GRID) }
    var activeSection    by remember { mutableStateOf(0) } // 0=Posts 1=Creadores 2=Tags
    var showGuestDialog  by remember { mutableStateOf(false) }
    val posts            = remember {
        if (isGuest) MockData.posts.map { it.copy(isLiked = false, isSaved = false) }
        else MockData.posts
    }
    val creators         = remember { MockData.users }
    val tags             = remember { MockData.trendingTags }

    val filteredPosts = remember(searchQuery, selectedCategory) {
        posts.filter { post ->
            val matchCat    = selectedCategory == PostCategory.ALL || post.category == selectedCategory
            val matchSearch = searchQuery.isEmpty() ||
                    post.title.contains(searchQuery, ignoreCase = true) ||
                    (post.description?.contains(searchQuery, ignoreCase = true) == true) ||
                    post.tags.any { it.contains(searchQuery, ignoreCase = true) }
            matchCat && matchSearch
        }
    }

    // Guest dialog
    GuestLoginRequiredDialog(
        show         = showGuestDialog,
        onDismiss    = { showGuestDialog = false },
        onLoginClick = {
            showGuestDialog = false
            onRequestLogin()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        ExploreHeader(
            searchQuery      = searchQuery,
            onSearchChange   = { searchQuery = it },
            viewMode         = viewMode,
            onViewModeToggle = { viewMode = if (viewMode == ExploreView.GRID) ExploreView.LIST else ExploreView.GRID }
        )

        // ── Section tabs ──────────────────────────────────────────────────────
        SectionTabs(activeSection = activeSection, onSectionChange = { activeSection = it })

        // ── Content ───────────────────────────────────────────────────────────
        when (activeSection) {
            0 -> PostsSection(
                posts        = filteredPosts,
                viewMode     = viewMode,
                selectedCat  = selectedCategory,
                onCatSelect  = { selectedCategory = it }
            )
            1 -> CreatorsSection(creators = creators)
            2 -> TagsSection(tags = tags)
        }
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────
@Composable
private fun ExploreHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    viewMode: ExploreView,
    onViewModeToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(Emerald600, Teal500, Cyan400)))
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 20.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text       = "Explorar",
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color      = Color.White
                    )
                    Text(
                        text  = "Descubre creaciones increíbles",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                IconButton(
                    onClick  = onViewModeToggle,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = if (viewMode == ExploreView.GRID) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = "Cambiar vista",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search bar with white background
            Surface(
                shape  = RoundedCornerShape(16.dp),
                color  = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier          = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, null, tint = Stone400, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value         = searchQuery,
                        onValueChange = onSearchChange,
                        modifier      = Modifier.weight(1f).padding(vertical = 10.dp),
                        textStyle     = MaterialTheme.typography.bodyMedium.copy(color = Stone800),
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) {
                                Text("Buscar inspiración...", style = MaterialTheme.typography.bodyMedium, color = Stone400)
                            }
                            inner()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, null, tint = Stone400, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─── Section Tabs ─────────────────────────────────────────────────────────────
@Composable
private fun SectionTabs(activeSection: Int, onSectionChange: (Int) -> Unit) {
    val tabs = listOf(
        "Posts"     to Icons.Default.GridOn,
        "Creadores" to Icons.Default.People,
        "Tags"      to Icons.Default.Tag
    )
    Surface(color = Color.White, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, (label, icon) ->
                val selected = activeSection == index
                val bg by animateColorAsState(
                    if (selected) Emerald500 else Stone100, tween(200), label = "tabBg$index"
                )
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSectionChange(index) },
                    shape = RoundedCornerShape(12.dp),
                    color = bg
                ) {
                    Row(
                        modifier              = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Icon(
                            icon, null,
                            tint     = if (selected) Color.White else Stone500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            label,
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color      = if (selected) Color.White else Stone500
                        )
                    }
                }
            }
        }
    }
}

// ─── Posts Section ────────────────────────────────────────────────────────────
@Composable
private fun PostsSection(
    posts: List<Post>,
    viewMode: ExploreView,
    selectedCat: PostCategory,
    onCatSelect: (PostCategory) -> Unit
) {
    val categories = listOf(
        PostCategory.ALL        to Icons.Default.Dashboard,
        PostCategory.FURNITURE  to Icons.Default.Chair,
        PostCategory.LIGHTING   to Icons.Default.Lightbulb,
        PostCategory.DECORATION to Icons.Default.Palette,
        PostCategory.FASHION    to Icons.Default.Checkroom,
        PostCategory.GARDEN     to Icons.Default.Yard,
        PostCategory.TECH       to Icons.Default.PhoneAndroid
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Category chips horizontal scroll
        LazyRow(
            contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { (cat, icon) ->
                val selected = selectedCat == cat
                val bg by animateColorAsState(
                    if (selected) Emerald500 else Color.White, tween(200), label = "catBg"
                )
                Surface(
                    modifier        = Modifier.clickable { onCatSelect(cat) },
                    shape           = RoundedCornerShape(20.dp),
                    color           = bg,
                    shadowElevation = if (selected) 0.dp else 2.dp
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            icon, null,
                            tint     = if (selected) Color.White else Emerald600,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            cat.displayName,
                            style      = MaterialTheme.typography.labelMedium,
                            color      = if (selected) Color.White else Stone700,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        if (posts.isEmpty()) {
            EmptyState(
                icon        = Icons.Outlined.SearchOff,
                title       = "Sin resultados",
                description = "No encontramos posts en esta categoría",
                modifier    = Modifier.fillMaxSize()
            )
        } else {
            AnimatedContent(
                targetState = viewMode,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "viewMode"
            ) { mode ->
                if (mode == ExploreView.GRID) {
                    LazyVerticalStaggeredGrid(
                        columns              = StaggeredGridCells.Fixed(2),
                        contentPadding       = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalItemSpacing  = 10.dp
                    ) {
                        items(posts, key = { it.id }) { post ->
                            MasonryCard(post = post)
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement   = Arrangement.spacedBy(10.dp)
                    ) {
                        items(posts, key = { it.id }) { post ->
                            ListPostCard(post = post)
                        }
                    }
                }
            }
        }
    }
}

// ─── Masonry Card ─────────────────────────────────────────────────────────────
@Composable
private fun MasonryCard(post: Post) {
    val heights = listOf(160.dp, 200.dp, 240.dp, 180.dp, 220.dp)
    val h       = remember { heights.random() }
    var isLiked by remember { mutableStateOf(post.isLiked) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = CardShape,
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(h)) {
            AsyncImage(
                model              = post.imageUrl,
                contentDescription = post.title,
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop
            )
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(0.55f)),
                            startY = h.value * 0.4f
                        )
                    )
            )
            // Category badge
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(6.dp),
                shape    = RoundedCornerShape(8.dp),
                color    = Emerald500.copy(alpha = 0.9f)
            ) {
                Text(
                    post.category.displayName,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            // Like button
            IconButton(
                onClick  = { isLiked = !isLiked },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(0.35f))
            ) {
                Icon(
                    if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    null,
                    tint     = if (isLiked) Rose400 else Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            // Bottom info
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
            ) {
                Text(
                    post.title,
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(imageUrl = post.author.avatarUrl, size = 18.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        post.author.name.split(" ").first(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.9f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.Favorite, null, tint = Rose300, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        "${post.likesCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.9f)
                    )
                }
            }
        }
    }
}

// ─── List Post Card ───────────────────────────────────────────────────────────
@Composable
private fun ListPostCard(post: Post) {
    var isLiked by remember { mutableStateOf(post.isLiked) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = CardShape,
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(110.dp)) {
            AsyncImage(
                model              = post.imageUrl,
                contentDescription = post.title,
                modifier           = Modifier.width(110.dp).fillMaxHeight(),
                contentScale       = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Surface(shape = RoundedCornerShape(6.dp), color = Emerald50) {
                        Text(
                            post.category.displayName,
                            style    = MaterialTheme.typography.labelSmall,
                            color    = Emerald700,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        post.title,
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines   = 2,
                        overflow   = TextOverflow.Ellipsis
                    )
                }
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(imageUrl = post.author.avatarUrl, size = 18.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            post.author.name.split(" ").first(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Stone500
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            null,
                            tint     = if (isLiked) Rose500 else Stone400,
                            modifier = Modifier.size(14.dp).clickable { isLiked = !isLiked }
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("${post.likesCount}", style = MaterialTheme.typography.labelSmall, color = Stone500)
                    }
                }
            }
        }
    }
}

// ─── Creators Section ─────────────────────────────────────────────────────────
@Composable
private fun CreatorsSection(creators: List<UserPreview>) {
    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                "Creadores destacados",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(bottom = 4.dp)
            )
        }
        items(creators, key = { it.id }) { creator ->
            CreatorCard(creator = creator)
        }
    }
}

@Composable
private fun CreatorCard(creator: UserPreview) {
    var following by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = CardShape,
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(
                imageUrl         = creator.avatarUrl,
                size             = 52.dp,
                showVerifiedBadge = creator.isVerified,
                showOnlineBadge  = true,
                isOnline         = creator.isOnline
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        creator.name,
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (creator.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.Verified, null, tint = Emerald500, modifier = Modifier.size(14.dp))
                    }
                    if (creator.isOnline) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Success))
                    }
                }
                Text(
                    "@${creator.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone500
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "${creator.followersCount} seguidores",
                    style = MaterialTheme.typography.labelSmall,
                    color = Stone400
                )
            }
            // Follow button
            val btnBg by animateColorAsState(if (following) Stone100 else Emerald500, label = "followBg")
            val btnText by animateColorAsState(if (following) Stone600 else Color.White, label = "followText")
            Surface(
                modifier  = Modifier.clickable { following = !following },
                shape     = RoundedCornerShape(20.dp),
                color     = btnBg
            ) {
                Text(
                    if (following) "Siguiendo" else "Seguir",
                    style      = MaterialTheme.typography.labelMedium,
                    color      = btnText,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// ─── Tags Section ─────────────────────────────────────────────────────────────
private val tagGradients = listOf(
    listOf(Emerald400, Teal500),
    listOf(Teal400, Cyan500),
    listOf(Cyan400, Blue500),
    listOf(Violet400, Pink500),
    listOf(Amber400, Rose400),
    listOf(Pink400, Violet400),
    listOf(Rose400, Amber500),
    listOf(Blue400, Violet500)
)

@Composable
private fun TagsSection(tags: List<Pair<String, Int>>) {
    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                "Tags populares 🔥",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(bottom = 4.dp)
            )
        }
        items(tags, key = { it.first }) { (tag, count) ->
            TagCard(tag = tag, count = count, rank = tags.indexOf(tag to count) + 1)
        }
    }
}

@Composable
private fun TagCard(tag: String, count: Int, rank: Int) {
    val gradient = tagGradients[Math.abs(tag.hashCode()) % tagGradients.size]
    val maxCount = 2345 // approx max for bar width

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = CardShape,
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "#$rank",
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color      = Color.White
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    tag,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Stone100)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(count.toFloat() / maxCount)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(Brush.horizontalGradient(gradient))
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    "$count publicaciones",
                    style = MaterialTheme.typography.labelSmall,
                    color = Stone400
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.TrendingUp, null, tint = Emerald500, modifier = Modifier.size(18.dp))
        }
    }
}