package com.relife.ui.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.relife.data.model.Post
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

// ── Data ──────────────────────────────────────────────────────────────────────
private data class Badge(val icon: String, val label: String, val color: Color, val unlocked: Boolean)
private data class Skill(val name: String)

private val profileSkills = listOf(
    Skill("Upcycling"), Skill("Carpintería"), Skill("Pintura"),
    Skill("Costura"), Skill("Diseño"), Skill("Iluminación"), Skill("Jardín")
)

private val profileBadges = listOf(
    Badge("🌱", "Pionero",       Emerald500, true),
    Badge("🔥", "Tendencia",     Rose500,    true),
    Badge("⭐", "Top Creador",   Amber500,   true),
    Badge("💬", "Comunidad",     Blue500,    true),
    Badge("🏆", "100 Posts",     Violet500,  false),
    Badge("💎", "Pro",           Teal500,    false)
)

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit,
    isGuest: Boolean = false,
    onRequestLogin: () -> Unit = {}
) {
    // If guest, show a generic guest profile screen
    if (isGuest) {
        GuestProfileScreen(
            onLoginClick         = onRequestLogin,
            onNavigateToSettings = onNavigateToSettings
        )
        return
    }

    val user        = remember { MockData.currentUser }
    val posts       = remember { MockData.posts }
    val likedPosts  = remember { posts.filter { it.isLiked } }
    val savedPosts  = remember { posts.filter { it.isSaved } }

    var selectedTab    by remember { mutableIntStateOf(0) }
    var isFollowing    by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    val uriHandler     = LocalUriHandler.current

    // Grid posts — toggle overlay on press
    val tabs = listOf(
        Triple("Posts",    Icons.Filled.GridOn,           Icons.Outlined.GridOn),
        Triple("Guardados",Icons.Filled.Bookmark,         Icons.Outlined.BookmarkBorder),
        Triple("Likes",    Icons.Filled.Favorite,         Icons.Outlined.FavoriteBorder),
        Triple("Logros",   Icons.Filled.EmojiEvents,      Icons.Outlined.EmojiEvents)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundLight)
    ) {
        // ── Cover + Avatar header ────────────────────────────────────────────
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Cover
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Brush.linearGradient(listOf(Emerald600, Teal500, Cyan400)))
                ) {
                    user.coverUrl?.let {
                        AsyncImage(
                            model              = it,
                            contentDescription = "Cover",
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Crop
                        )
                    }
                    // Subtle grid pattern overlay for visual depth
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.25f))))
                    )
                    // Cover edit button
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(0.45f))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                // Top action buttons
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    ActionCircleButton(Icons.Outlined.Analytics, "Stats",    onClick = onNavigateToStats)
                    Spacer(modifier = Modifier.width(8.dp))
                    ActionCircleButton(Icons.Default.Share,      "Compartir",onClick = { showShareSheet = true })
                    Spacer(modifier = Modifier.width(8.dp))
                    ActionCircleButton(Icons.Outlined.Settings,  "Ajustes",  onClick = onNavigateToSettings)
                }

                // Avatar ring
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 54.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Emerald400, Teal400, Cyan400)))
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(3.dp)
                    ) {
                        UserAvatar(imageUrl = user.avatarUrl, size = 100.dp)
                    }
                    // Camera edit overlay
                    Box(
                        modifier         = Modifier
                            .size(30.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Emerald500)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // ── User info block ──────────────────────────────────────────────────
        item {
            Column(
                modifier            = Modifier.fillMaxWidth().padding(top = 62.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Name + verified
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    if (user.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Filled.Verified, null, tint = Emerald500, modifier = Modifier.size(22.dp))
                    }
                }
                Text("@${user.username}", style = MaterialTheme.typography.bodyMedium, color = Stone500)

                // Website link
                user.website?.let { url ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier          = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { uriHandler.openUri(if (url.startsWith("http")) url else "https://$url") }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Language, null, tint = Emerald600, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(url.removePrefix("https://").removePrefix("http://"), style = MaterialTheme.typography.labelMedium, color = Emerald600, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Bio
                user.bio?.let { bio ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        bio,
                        style       = MaterialTheme.typography.bodyMedium,
                        color       = Stone600,
                        textAlign   = TextAlign.Center,
                        modifier    = Modifier.padding(horizontal = 28.dp)
                    )
                }

                // Skills chips
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(profileSkills) { skill ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Emerald50
                        ) {
                            Text(
                                skill.name,
                                style    = MaterialTheme.typography.labelSmall,
                                color    = Emerald700,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Stats row
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat("${user.postsCount}",     "Posts")
                    StatDivider()
                    ProfileStat("${user.followersCount}", "Seguidores")
                    StatDivider()
                    ProfileStat("${user.followingCount}", "Siguiendo")
                    StatDivider()
                    ProfileStat("${user.totalLikes}",     "Likes")
                }

                // Action buttons row
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier              = Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Follow button
                    val followBg by animateColorAsState(if (isFollowing) Stone100 else Emerald500, tween(250), label = "followBg")
                    val followTx by animateColorAsState(if (isFollowing) Stone700 else Color.White, tween(250), label = "followTx")
                    Surface(
                        modifier  = Modifier.weight(1f).clickable { isFollowing = !isFollowing },
                        shape     = RoundedCornerShape(14.dp),
                        color     = followBg
                    ) {
                        Row(
                            modifier              = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isFollowing) Icons.Default.PersonRemove else Icons.Default.PersonAdd,
                                null, tint = followTx, modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isFollowing) "Siguiendo" else "Seguir",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = followTx
                            )
                        }
                    }
                    // Message button
                    Surface(
                        modifier  = Modifier.weight(1f).clickable { },
                        shape     = RoundedCornerShape(14.dp),
                        color     = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier              = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Stone600, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mensaje", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Stone700)
                        }
                    }
                }
            }
        }

        // ── Tabs ─────────────────────────────────────────────────────────────
        item {
            Surface(color = Color.White, shadowElevation = 2.dp) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor   = Color.White,
                    contentColor     = Emerald600,
                    edgePadding      = 8.dp,
                    indicator        = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .padding(horizontal = 24.dp)
                                .background(Emerald500, CircleShape)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, (label, filledIcon, outlinedIcon) ->
                        val sel = selectedTab == index
                        Tab(
                            selected             = sel,
                            onClick              = { selectedTab = index },
                            selectedContentColor = Emerald600,
                            unselectedContentColor = Stone400
                        ) {
                            Row(
                                modifier          = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(if (sel) filledIcon else outlinedIcon, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
        }

        // ── Tab content ───────────────────────────────────────────────────────
        item {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "tabContent"
            ) { tab ->
                when (tab) {
                    0 -> PostsGrid(posts = posts)
                    1 -> if (savedPosts.isEmpty()) EmptyTabState(Icons.Outlined.BookmarkBorder, "Sin guardados", "Guarda publicaciones para verlas aquí")
                    else PostsGrid(posts = savedPosts)
                    2 -> if (likedPosts.isEmpty()) EmptyTabState(Icons.Outlined.FavoriteBorder, "Sin likes", "Las publicaciones que te gusten aparecerán aquí")
                    else PostsGrid(posts = likedPosts)
                    3 -> BadgesSection()
                    else -> PostsGrid(posts = posts)
                }
            }
        }
    }

    // ── Share sheet ───────────────────────────────────────────────────────────
    if (showShareSheet) {
        ShareProfileSheet(username = user.username, onDismiss = { showShareSheet = false })
    }
}

// ─── Posts Grid ───────────────────────────────────────────────────────────────
@Composable
private fun PostsGrid(posts: List<Post>) {
    // Fixed height since inside LazyColumn
    val itemSize = 120.dp
    val rows     = (posts.size + 2) / 3
    val gridHeight = (rows * (itemSize.value + 2)).dp

    Box(modifier = Modifier.fillMaxWidth().height(gridHeight)) {
        LazyVerticalGrid(
            columns               = GridCells.Fixed(3),
            contentPadding        = PaddingValues(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement   = Arrangement.spacedBy(2.dp),
            userScrollEnabled     = false
        ) {
            items(posts, key = { it.id }) { post ->
                ProfilePostItem(post = post)
            }
        }
    }
}

@Composable
private fun ProfilePostItem(post: Post) {
    var showOverlay by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { showOverlay = !showOverlay }
    ) {
        AsyncImage(
            model              = post.imageUrl,
            contentDescription = post.title,
            modifier           = Modifier.fillMaxSize(),
            contentScale       = ContentScale.Crop
        )
        AnimatedVisibility(
            visible = showOverlay,
            enter   = fadeIn(tween(180)),
            exit    = fadeOut(tween(180))
        ) {
            Box(
                modifier         = Modifier.fillMaxSize().background(Color.Black.copy(0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Favorite, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${post.likesCount}", color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Filled.ChatBubble, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${post.commentsCount}", color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        post.title,
                        style    = MaterialTheme.typography.labelSmall,
                        color    = Color.White.copy(0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
        }
    }
}

// ─── Badges Section ───────────────────────────────────────────────────────────
@Composable
private fun BadgesSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Logros desbloqueados", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Stone700)
        Spacer(modifier = Modifier.height(12.dp))

        // Unlocked badges grid (2 per row)
        val unlocked = profileBadges.filter { it.unlocked }
        val locked   = profileBadges.filter { !it.unlocked }

        unlocked.chunked(2).forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { badge ->
                    BadgeCard(badge = badge, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Por desbloquear", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Stone400)
        Spacer(modifier = Modifier.height(10.dp))

        locked.chunked(2).forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { badge ->
                    BadgeCard(badge = badge, modifier = Modifier.weight(1f), locked = true)
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: Badge, modifier: Modifier = Modifier, locked: Boolean = false) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = if (locked) Stone50 else Color.White),
        elevation = CardDefaults.cardElevation(if (locked) 0.dp else 3.dp)
    ) {
        Column(
            modifier            = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier         = if (locked) {
                    Modifier.size(52.dp).clip(CircleShape).background(Stone200)
                } else {
                    Modifier.size(52.dp).clip(CircleShape).background(
                        Brush.radialGradient(listOf(badge.color.copy(0.2f), badge.color.copy(0.05f)))
                    )
                },
                contentAlignment = Alignment.Center
            ) {
                Text(if (locked) "🔒" else badge.icon, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                badge.label,
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color      = if (locked) Stone400 else Stone800,
                textAlign  = TextAlign.Center
            )
            if (!locked) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(shape = RoundedCornerShape(6.dp), color = badge.color.copy(0.12f)) {
                    Text("Obtenido", style = MaterialTheme.typography.labelSmall, color = badge.color, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }
        }
    }
}

// ─── Share Profile Sheet ──────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareProfileSheet(username: String, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = Color.White,
        shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier            = Modifier.padding(horizontal = 20.dp).padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Compartir perfil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("relife.app/@$username", style = MaterialTheme.typography.bodyMedium, color = Stone400)
            Spacer(modifier = Modifier.height(20.dp))

            // QR placeholder
            Box(
                modifier         = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Stone50),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.QrCode2, null, tint = Stone400, modifier = Modifier.size(80.dp))
                    Text("QR de perfil", style = MaterialTheme.typography.labelSmall, color = Stone400)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Social share buttons
            val socials = listOf(
                Triple("WhatsApp",  Color(0xFF25D366), Icons.Default.Chat),
                Triple("Instagram", Color(0xFFE1306C), Icons.Default.CameraAlt),
                Triple("Twitter/X", Color(0xFF1DA1F2), Icons.Default.Share),
                Triple("Copiar",    Stone600,          Icons.Default.ContentCopy)
            )
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                socials.forEach { (name, color, icon) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier         = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(color.copy(0.12f))
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(name, style = MaterialTheme.typography.labelSmall, color = Stone500)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick  = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp)
            ) {
                Text("Cerrar")
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Stone500)
    }
}

@Composable
private fun StatDivider() {
    Box(modifier = Modifier.height(32.dp).width(1.dp).background(Stone200))
}

@Composable
private fun ActionCircleButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    Box(
        modifier         = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(0.4f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, description, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun EmptyTabState(icon: ImageVector, title: String, desc: String) {
    Box(
        modifier         = Modifier.fillMaxWidth().height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Stone300, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Stone500)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall, color = Stone400, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 40.dp))
        }
    }
}
// ─── Guest Profile Screen ─────────────────────────────────────────────────────
@Composable
private fun GuestProfileScreen(
    onLoginClick: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // ── Cover area with gradient ────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Brush.linearGradient(listOf(Emerald600, Teal500, Cyan400)))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.25f))))
                )
            }
            // Settings button
            Row(
                modifier              = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                ActionCircleButton(Icons.Default.Settings, "Ajustes", onClick = onNavigateToSettings)
            }
            // Avatar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 45.dp)
            ) {
                UserAvatar(
                    imageUrl = null,
                    size     = 100.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(56.dp))

        // ── Guest info ──────────────────────────────────────────────────────
        Column(
            modifier            = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "Invitado",
                style      = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "@invitado",
                style = MaterialTheme.typography.bodyMedium,
                color = Stone400
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats row (all zeros)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GuestStatItem("0", "Posts")
                GuestStatItem("0", "Seguidores")
                GuestStatItem("0", "Siguiendo")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CTA card
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Emerald50, Teal50))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint     = Emerald500,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text       = "Crea tu perfil",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text      = "Regístrate para compartir tus creaciones, seguir a otros creadores y vender tus productos.",
                        style     = MaterialTheme.typography.bodyMedium,
                        color     = Stone500,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryButton(
                        text     = "Crear cuenta gratis",
                        onClick  = onLoginClick,
                        modifier = Modifier.fillMaxWidth(),
                        icon     = Icons.Default.PersonAdd
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        text    = "Ya tengo cuenta · Iniciar sesión",
                        onClick = onLoginClick
                    )
                }
            }
        }
    }
}

@Composable
private fun GuestStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = value,
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color      = Stone300
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall,
            color = Stone400
        )
    }
}