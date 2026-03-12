package com.relife.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.relife.data.model.Post
import com.relife.data.model.PostCategory
import com.relife.data.repository.MockData
import com.relife.data.repository.Story
import com.relife.ui.components.*
import com.relife.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Mini-game / challenge data ───────────────────────────────────────────────
private data class GameChallenge(
    val id: Int,
    val title: String,
    val description: String,
    val reward: String,
    val rewardPoints: Int,
    val icon: ImageVector,
    val gradient: List<Color>,
    val progress: Float,          // 0f..1f
    val totalSteps: Int,
    val currentStep: Int,
    val timeLeft: String,
    val difficulty: String
)

private val gameChallenges = listOf(
    GameChallenge(
        id           = 1,
        title        = "Recicla & Gana",
        description  = "Publica 3 proyectos de upcycling esta semana y gana puntos extra",
        reward       = "−15% descuento",
        rewardPoints = 500,
        icon         = Icons.Default.Recycling,
        gradient     = listOf(Emerald500, Teal400),
        progress     = 0.66f,
        totalSteps   = 3,
        currentStep  = 2,
        timeLeft     = "2 días",
        difficulty   = "Fácil"
    ),
    GameChallenge(
        id           = 2,
        title        = "Streak de Creatividad",
        description  = "Entra a la app 7 días seguidos y completa tu racha creativa",
        reward       = "Badge exclusivo",
        rewardPoints = 300,
        icon         = Icons.Default.LocalFireDepartment,
        gradient     = listOf(Color(0xFFFF6B35), Color(0xFFFF9500)),
        progress     = 0.43f,
        totalSteps   = 7,
        currentStep  = 3,
        timeLeft     = "4 días",
        difficulty   = "Media"
    ),
    GameChallenge(
        id           = 3,
        title        = "Maestro del Mercado",
        description  = "Vende tu primer producto en el Marketplace y desbloquea tu insignia",
        reward       = "Sin comisión 1 mes",
        rewardPoints = 800,
        icon         = Icons.Default.EmojiEvents,
        gradient     = listOf(Color(0xFF7C3AED), Color(0xFFEC4899)),
        progress     = 0.0f,
        totalSteps   = 1,
        currentStep  = 0,
        timeLeft     = "Sin límite",
        difficulty   = "Reto"
    )
)

// Category icon mapping
private data class CategoryInfo(val icon: ImageVector)

private val categoryIcons = mapOf(
    PostCategory.FURNITURE  to Icons.Default.Chair,
    PostCategory.LIGHTING   to Icons.Default.Lightbulb,
    PostCategory.FASHION    to Icons.Default.Checkroom,
    PostCategory.GARDEN     to Icons.Default.Yard,
    PostCategory.DECORATION to Icons.Default.Palette,
    PostCategory.TECH       to Icons.Default.PhoneAndroid
)

// ─── Screen ────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToMessages: () -> Unit,
    isGuest: Boolean = false,
    onRequestLogin: () -> Unit = {}
) {
    var selectedTab      by remember { mutableIntStateOf(0) }
    var searchQuery      by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<PostCategory?>(null) }
    var showSearch       by remember { mutableStateOf(false) }
    var isRefreshing     by remember { mutableStateOf(false) }
    var showGuestDialog  by remember { mutableStateOf(false) }
    val posts            = remember {
        if (isGuest) MockData.posts.map { it.copy(isLiked = false, isSaved = false) }
        else MockData.posts
    }
    val stories          = remember { MockData.stories }
    val trendingTags     = remember { MockData.trendingTags }
    val gridState        = rememberLazyGridState()
    val scope            = rememberCoroutineScope()
    val pullState        = rememberPullToRefreshState()

    val filteredPosts = remember(searchQuery, selectedCategory, selectedTab) {
        posts.filter { post ->
            val matchesSearch = searchQuery.isEmpty() ||
                    post.title.contains(searchQuery, ignoreCase = true) ||
                    (post.description?.contains(searchQuery, ignoreCase = true) == true) ||
                    post.tags.any { it.contains(searchQuery, ignoreCase = true) }
            val matchesCategory = selectedCategory == null || post.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    val showScrollTop by remember { derivedStateOf { gridState.firstVisibleItemIndex > 3 } }

    Box(modifier = Modifier.fillMaxSize()) {

        // Guest login required dialog
        GuestLoginRequiredDialog(
            show         = showGuestDialog,
            onDismiss    = { showGuestDialog = false },
            onLoginClick = {
                showGuestDialog = false
                onRequestLogin()
            }
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh    = {
                scope.launch {
                    isRefreshing = true
                    delay(1500)
                    isRefreshing = false
                }
            },
            state    = pullState,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
            ) {
                HomeHeader(
                    userName             = if (isGuest) "Invitado" else MockData.currentUser.name.split(" ").first(),
                    avatarUrl            = if (isGuest) null else MockData.currentUser.avatarUrl,
                    searchQuery          = searchQuery,
                    onSearchChange       = { searchQuery = it },
                    onNotificationsClick = {
                        if (isGuest) showGuestDialog = true else onNavigateToNotifications()
                    },
                    onMessagesClick      = {
                        if (isGuest) showGuestDialog = true else onNavigateToMessages()
                    },
                    showSearch           = showSearch,
                    onToggleSearch       = { showSearch = !showSearch }
                )

                LazyVerticalGrid(
                    state                 = gridState,
                    columns               = GridCells.Fixed(2),
                    contentPadding        = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp)
                ) {
                    // Stories
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        StoriesSection(stories = stories)
                    }

                    // 🎮 Challenges carousel
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        ChallengesSection(
                            challenges    = gameChallenges,
                            isGuest       = isGuest,
                            onGuestAction = { showGuestDialog = true }
                        )
                    }

                    // Category chips
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        CategoryFilterSection(
                            selectedCategory   = selectedCategory,
                            onCategorySelected = { cat ->
                                selectedCategory = if (selectedCategory == cat) null else cat
                            }
                        )
                    }

                    if (searchQuery.isEmpty() && selectedCategory == null) {
                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                            TrendingTagsSection(tags = trendingTags)
                        }
                    }

                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor   = Color.Transparent,
                            contentColor     = Emerald600,
                            modifier         = Modifier.padding(horizontal = 16.dp),
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
                            listOf("Tendencias", "Seguidos").forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick  = { selectedTab = index },
                                    text = {
                                        Text(
                                            title,
                                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    },
                                    selectedContentColor   = Emerald600,
                                    unselectedContentColor = Stone400
                                )
                            }
                        }
                    }

                    if (filteredPosts.isEmpty()) {
                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                            EmptyState(
                                icon        = Icons.Outlined.SearchOff,
                                title       = "Sin resultados",
                                description = "No encontramos posts para tu búsqueda",
                                modifier    = Modifier.padding(32.dp)
                            )
                        }
                    } else {
                        items(filteredPosts, key = { it.id }) { post ->
                            val idx = filteredPosts.indexOf(post)
                            Box(modifier = Modifier.padding(
                                start = if (idx % 2 == 0) 16.dp else 0.dp,
                                end   = if (idx % 2 == 1) 16.dp else 0.dp
                            )) {
                                AnimatedPostCard(
                                    post = post,
                                    isGuest = isGuest,
                                    onGuestAction = { showGuestDialog = true }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Scroll-to-top FAB
        AnimatedVisibility(
            visible  = showScrollTop,
            enter    = fadeIn() + scaleIn(),
            exit     = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
                .zIndex(10f)
        ) {
            FloatingActionButton(
                onClick        = { scope.launch { gridState.animateScrollToItem(0) } },
                containerColor = Emerald500,
                contentColor   = Color.White,
                modifier       = Modifier.size(44.dp),
                shape          = CircleShape
            ) {
                Icon(Icons.Default.KeyboardArrowUp, "Volver arriba", modifier = Modifier.size(22.dp))
            }
        }
    }
}

// ─── Challenges Section ────────────────────────────────────────────────────────
@Composable
private fun ChallengesSection(
    challenges: List<GameChallenge>,
    isGuest: Boolean = false,
    onGuestAction: () -> Unit = {}
) {
    // Track claimed state per challenge
    val claimed = remember { mutableStateMapOf<Int, Boolean>() }

    Column(modifier = Modifier.padding(top = 4.dp)) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = "Retos & Recompensas",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(shape = RoundedCornerShape(6.dp), color = Emerald500) {
                    Text(
                        "🎮 NUEVO",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = Color.White,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        PointsMiniCard(isGuest = isGuest)

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding        = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(challenges) { challenge ->
                ChallengeCard(
                    challenge     = if (isGuest) challenge.copy(progress = 0f, currentStep = 0) else challenge,
                    isClaimed     = if (isGuest) false else claimed[challenge.id] == true,
                    onClaim       = {
                        if (isGuest) onGuestAction()
                        else claimed[challenge.id] = true
                    },
                    isGuest       = isGuest,
                    onGuestAction = onGuestAction
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}


// ─── Points Mini Card ─────────────────────────────────────────────────────────
@Composable
private fun PointsMiniCard(isGuest: Boolean = false) {
    val targetXp = if (isGuest) 0f else 1250f / 2000f
    val animXp = remember { Animatable(0f) }
    LaunchedEffect(isGuest) { animXp.animateTo(targetXp, tween(900, easing = EaseOutCubic)) }

    val level      = if (isGuest) "–" else "5"
    val levelLabel = if (isGuest) "Nivel –" else "Nivel 5"
    val rankLabel  = if (isGuest) "Invitado" else "Creador"
    val rankColor  = if (isGuest) Stone400 else Emerald600
    val rankBg     = if (isGuest) Stone100 else Emerald50
    val xpText     = if (isGuest) "0 / 0 XP" else "1.250 / 2.000 XP"
    val points     = if (isGuest) "0" else "1.250"
    val streak     = if (isGuest) "0" else "7"
    val barGradient = if (isGuest) listOf(Stone200, Stone300) else listOf(Emerald500, Teal400)
    val badgeGradient = if (isGuest) listOf(Stone300, Stone400) else listOf(Emerald500, Teal400)

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Nivel + barra XP
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(badgeGradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(level, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.White)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            levelLabel,
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color      = Stone800,
                            fontSize   = 12.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = rankBg) {
                            Text(
                                rankLabel,
                                style    = MaterialTheme.typography.labelSmall,
                                color    = rankColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Stone100)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animXp.value)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(Brush.horizontalGradient(barGradient))
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        xpText,
                        style    = MaterialTheme.typography.labelSmall,
                        color    = Stone400,
                        fontSize = 8.sp
                    )
                }
            }

            // Divider
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Stone100))

            // Puntos
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Stars, null, tint = if (isGuest) Stone300 else Amber500, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    points,
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color      = if (isGuest) Stone400 else Stone800,
                    fontSize   = 13.sp
                )
                Text("puntos", style = MaterialTheme.typography.labelSmall, color = Stone400, fontSize = 8.sp)
            }

            // Divider
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Stone100))

            // Racha
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (isGuest) "🔒" else "🔥", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    streak,
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color      = if (isGuest) Stone400 else Stone800,
                    fontSize   = 13.sp
                )
                Text("días", style = MaterialTheme.typography.labelSmall, color = Stone400, fontSize = 8.sp)
            }
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: GameChallenge,
    isClaimed: Boolean,
    onClaim: () -> Unit,
    isGuest: Boolean = false,
    onGuestAction: () -> Unit = {}
) {
    var showConfetti by remember { mutableStateOf(false) }
    val scope        = rememberCoroutineScope()

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(challenge.id) {
        animProgress.animateTo(challenge.progress, tween(1000, easing = EaseOutCubic))
    }

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue  = 1f,
        targetValue   = if (challenge.progress > 0.8f && !isClaimed) 1.1f else 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label         = "iconPulse"
    )

    val accentColor = if (isClaimed) Stone400 else challenge.gradient.first()
    val isComplete  = challenge.progress >= 1f

    // Ticket shape: fondo blanco, borde discontinuo del color del reto
    Box(modifier = Modifier.width(210.dp).height(310.dp)) {
        // Sombra suave simulada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(accentColor.copy(alpha = 0.12f))
                .height(4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                // Borde del color del reto
                .then(
                    Modifier.background(Color.Transparent)
                )
        ) {
            // Borde discontinuo dibujado con Canvas
            Canvas(modifier = Modifier.matchParentSize()) {
                val strokeWidth = 2.dp.toPx()
                val dashLength  = 8.dp.toPx()
                val gapLength   = 5.dp.toPx()
                val cornerRadius = 16.dp.toPx()
                drawRoundRect(
                    color       = accentColor.copy(alpha = if (isClaimed) 0.3f else 0.7f),
                    style       = Stroke(
                        width      = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(dashLength, gapLength), 0f
                        )
                    ),
                    cornerRadius = CornerRadius(cornerRadius)
                )
            }

            Column(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment   = Alignment.CenterHorizontally
            ) {
                // ── Badges fila superior ──────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Difficulty badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            challenge.difficulty,
                            style    = MaterialTheme.typography.labelSmall,
                            color    = accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    // Time badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Timer, null, tint = Stone400, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(challenge.timeLeft, style = MaterialTheme.typography.labelSmall, color = Stone500, fontSize = 9.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Icono grande centrado (achievement style) ─────────────
                Box(
                    modifier         = Modifier
                        .size(64.dp)
                        .scale(pulse)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                if (isClaimed) listOf(Stone200, Stone300)
                                else challenge.gradient
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        challenge.icon, null,
                        tint     = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                // ── Título y descripción ──────────────────────────────────
                Text(
                    challenge.title,
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color      = Stone800,
                    fontSize   = 13.sp,
                    textAlign  = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    challenge.description,
                    style      = MaterialTheme.typography.bodySmall,
                    color      = Stone500,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    fontSize   = 10.sp,
                    lineHeight = 13.sp,
                    textAlign  = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ── Separador estilo ticket (línea punteada + semicírculos) ──
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Stone100))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(listOf(Stone200, Stone200))
                            )
                    )
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Stone100))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Progreso ──────────────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "${challenge.currentStep}/${challenge.totalSteps}",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = Stone500,
                        fontSize = 9.sp
                    )
                    Text(
                        "${(challenge.progress * 100).toInt()}%",
                        style      = MaterialTheme.typography.labelSmall,
                        color      = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 9.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Stone100)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animProgress.value)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(Brush.horizontalGradient(
                                if (isClaimed) listOf(Stone300, Stone300)
                                else challenge.gradient
                            ))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Reward pill ───────────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier              = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CardGiftcard, null, tint = accentColor, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            challenge.reward,
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color      = accentColor,
                            fontSize   = 10.sp,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            "+${challenge.rewardPoints}pts",
                            style    = MaterialTheme.typography.labelSmall,
                            color    = accentColor.copy(0.65f),
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── CTA button ────────────────────────────────────────────
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isGuest) {
                                onGuestAction()
                            } else if (isComplete && !isClaimed) {
                                showConfetti = true
                                scope.launch {
                                    delay(1500)
                                    showConfetti = false
                                    onClaim()
                                }
                            }
                        },
                    shape = RoundedCornerShape(10.dp),
                    color = when {
                        isGuest    -> Emerald500
                        isClaimed  -> Stone100
                        isComplete -> accentColor
                        else       -> accentColor.copy(alpha = 0.12f)
                    }
                ) {
                    Text(
                        text = when {
                            isGuest    -> "Regístrate para participar"
                            isClaimed  -> "✓ Canjeado"
                            isComplete -> "¡Canjear!"
                            else       -> "En progreso"
                        },
                        style      = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 10.sp,
                        textAlign  = TextAlign.Center,
                        color      = when {
                            isGuest    -> Color.White
                            isClaimed  -> Stone400
                            isComplete -> Color.White
                            else       -> accentColor
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }

            // ✨ Confetti overlay
            androidx.compose.animation.AnimatedVisibility(
                visible = showConfetti,
                enter   = fadeIn(tween(100)),
                exit    = fadeOut(tween(400))
            ) {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .matchParentSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(0.92f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "¡Recompensa\ncanjeada!",
                            style      = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color      = accentColor,
                            textAlign  = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ─── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun HomeHeader(
    userName: String,
    avatarUrl: String?,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    showSearch: Boolean,
    onToggleSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(colors = listOf(Emerald600, Teal500, Cyan500)))
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(2.dp)
                    ) {
                        UserAvatar(imageUrl = avatarUrl, size = 42.dp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text       = "Hola, $userName 👋",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                        Text(
                            text  = "¿Qué vas a crear hoy?",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
                Row {
                    IconButton(onClick = onToggleSearch) {
                        Icon(
                            if (showSearch) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onMessagesClick) {
                        BadgedBox(badge = { Badge { Text("3") } }) {
                            Icon(Icons.AutoMirrored.Outlined.Chat, "Mensajes", tint = Color.White)
                        }
                    }
                    IconButton(onClick = onNotificationsClick) {
                        BadgedBox(badge = { Badge { Text("5") } }) {
                            Icon(Icons.Outlined.Notifications, "Notificaciones", tint = Color.White)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showSearch,
                enter   = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit    = fadeOut(tween(200)) + shrinkVertically(tween(200))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchBar(
                        value         = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder   = "Buscar proyectos, creadores, tags...",
                        leadingIcon   = Icons.Default.Search
                    )
                    if (searchQuery.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text  = "Resultados para \"$searchQuery\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

// ─── Stories ───────────────────────────────────────────────────────────────────
@Composable
private fun StoriesSection(stories: List<Story>) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text       = "Historias",
            style      = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier   = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            contentPadding        = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { AddStoryButton() }
            items(stories) { story -> StoryItem(story = story) }
        }
    }
}

@Composable
private fun AddStoryButton() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
        Box(
            modifier         = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Stone100, Stone200)))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier         = Modifier.size(26.dp).clip(CircleShape).background(Emerald500),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, "Añadir historia", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text("Tu historia", style = MaterialTheme.typography.labelSmall, color = Stone600, maxLines = 1)
    }
}

@Composable
private fun StoryItem(story: Story) {
    val ringBrush = if (!story.isViewed)
        Brush.linearGradient(StoryGradientColors)
    else
        Brush.linearGradient(listOf(Stone300, Stone300))

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
        Box(modifier = Modifier.size(76.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(ringBrush))
            Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(Color.White))
            UserAvatar(imageUrl = story.user.avatarUrl, size = 64.dp)
            if (!story.isViewed) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Emerald500)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text       = story.user.name.split(" ").first(),
            style      = MaterialTheme.typography.labelSmall,
            color      = if (!story.isViewed) Stone800 else Stone500,
            fontWeight = if (!story.isViewed) FontWeight.SemiBold else FontWeight.Normal,
            maxLines   = 1
        )
    }
}

// ─── Category Chips ────────────────────────────────────────────────────────────
@Composable
private fun CategoryFilterSection(
    selectedCategory: PostCategory?,
    onCategorySelected: (PostCategory) -> Unit
) {
    val orderedCategories = listOf(
        PostCategory.FURNITURE,
        PostCategory.LIGHTING,
        PostCategory.FASHION,
        PostCategory.GARDEN,
        PostCategory.DECORATION,
        PostCategory.TECH
    )
    LazyRow(
        contentPadding        = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(orderedCategories) { category ->
            val isSelected = selectedCategory == category
            val bgColor by animateColorAsState(
                targetValue   = if (isSelected) Emerald500 else Color.White,
                animationSpec = tween(200),
                label         = "chipBg"
            )
            val icon = categoryIcons[category] ?: Icons.Default.Category
            Surface(
                modifier        = Modifier.clickable { onCategorySelected(category) },
                shape           = ChipShape,
                color           = bgColor,
                shadowElevation = if (isSelected) 0.dp else 2.dp
            ) {
                Row(
                    modifier              = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector        = icon,
                        contentDescription = null,
                        tint               = if (isSelected) Color.White else Emerald600,
                        modifier           = Modifier.size(16.dp)
                    )
                    Text(
                        text       = category.displayName,
                        style      = MaterialTheme.typography.labelMedium,
                        color      = if (isSelected) Color.White else Stone700,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ─── Trending Tags ─────────────────────────────────────────────────────────────
@Composable
private fun TrendingTagsSection(tags: List<Pair<String, Int>>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "Trending 🔥", action = "Ver todo", onActionClick = { })
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(tags.take(6)) { (tag, count) ->
                TrendingTagCard(tag = tag, count = count)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

private val tagGradients = listOf(
    listOf(Emerald400, Teal500),
    listOf(Teal400, Cyan500),
    listOf(Cyan400, Blue500),
    listOf(Violet400, Pink500),
    listOf(Amber400, Rose400),
    listOf(Pink400, Violet400)
)

@Composable
private fun TrendingTagCard(tag: String, count: Int) {
    val gradient = tagGradients[Math.abs(tag.hashCode()) % tagGradients.size]
    Card(
        modifier  = Modifier.width(130.dp).clickable { },
        shape     = CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier         = Modifier.fillMaxWidth().height(52.dp).background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Text("#", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.6f))
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(tag, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(2.dp))
                Text("$count posts", style = MaterialTheme.typography.labelSmall, color = Stone500)
            }
        }
    }
}

// ─── Post Cards ────────────────────────────────────────────────────────────────
@Composable
private fun AnimatedPostCard(
    post: Post,
    isGuest: Boolean = false,
    onGuestAction: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(post.id) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.92f)
    ) {
        EnhancedPostCard(post = post, isGuest = isGuest, onGuestAction = onGuestAction)
    }
}

@Composable
private fun EnhancedPostCard(
    post: Post,
    isGuest: Boolean = false,
    onGuestAction: () -> Unit = {}
) {
    var isLiked          by remember { mutableStateOf(post.isLiked) }
    var isSaved          by remember { mutableStateOf(post.isSaved) }
    var likesCount       by remember { mutableIntStateOf(post.likesCount) }
    var showShareConfirm by remember { mutableStateOf(false) }
    var floatingHearts   by remember { mutableStateOf<List<Long>>(emptyList()) }
    val scope            = rememberCoroutineScope()

    val likeScale by animateFloatAsState(
        targetValue   = if (isLiked) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "likeScale"
    )

    LaunchedEffect(showShareConfirm) {
        if (showShareConfirm) { delay(2000); showShareConfirm = false }
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = CardShape,
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.85f)) {
                AsyncImage(
                    model              = post.imageUrl,
                    contentDescription = post.title,
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop
                )
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)),
                            startY = 180f
                        )
                    )
                )
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                    shape    = ChipShape,
                    color    = Emerald500.copy(alpha = 0.92f)
                ) {
                    Text(
                        text       = post.category.displayName,
                        style      = MaterialTheme.typography.labelSmall,
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                IconButton(
                    onClick  = {
                        if (isGuest) onGuestAction() else { isSaved = !isSaved }
                    },
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        "Guardar",
                        tint     = if (isSaved) Emerald500 else Stone400,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Row(
                    modifier          = Modifier.align(Alignment.BottomStart).padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(imageUrl = post.author.avatarUrl, size = 22.dp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text       = post.author.name.split(" ").first(),
                        style      = MaterialTheme.typography.labelSmall,
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                floatingHearts.forEach { id ->
                    FloatingHeart(key = id, modifier = Modifier.align(Alignment.Center))
                }
            }

            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text       = post.title,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(
                        modifier          = Modifier.clickable {
                            if (isGuest) {
                                onGuestAction()
                            } else {
                                isLiked    = !isLiked
                                likesCount = if (isLiked) likesCount + 1 else likesCount - 1
                                if (isLiked) {
                                    val id = System.currentTimeMillis()
                                    floatingHearts = floatingHearts + id
                                    scope.launch {
                                        delay(900)
                                        floatingHearts = floatingHearts - id
                                    }
                                }
                            }
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            "Like",
                            tint     = if (isLiked) Rose500 else Stone400,
                            modifier = Modifier.size(18.dp).scale(likeScale)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("$likesCount", style = MaterialTheme.typography.labelSmall, color = if (isLiked) Rose500 else Stone500)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.ChatBubbleOutline, "Comentarios", tint = Stone400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("${post.commentsCount}", style = MaterialTheme.typography.labelSmall, color = Stone500)
                    }
                    Icon(
                        Icons.Outlined.Share, "Compartir",
                        tint     = Stone400,
                        modifier = Modifier.size(16.dp).clickable {
                            if (isGuest) onGuestAction() else showShareConfirm = true
                        }
                    )
                }
                AnimatedVisibility(
                    visible = showShareConfirm,
                    enter   = fadeIn() + expandVertically(),
                    exit    = fadeOut() + shrinkVertically()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        shape    = RoundedCornerShape(6.dp),
                        color    = Emerald50
                    ) {
                        Text(
                            "✓ Enlace copiado",
                            style    = MaterialTheme.typography.labelSmall,
                            color    = Emerald700,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Floating Heart ────────────────────────────────────────────────────────────
@Composable
private fun FloatingHeart(key: Long, modifier: Modifier = Modifier) {
    var triggered by remember { mutableStateOf(false) }

    val offsetY by animateFloatAsState(
        targetValue   = if (triggered) -110f else 0f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label         = "heartY"
    )
    val alpha by animateFloatAsState(
        targetValue   = if (triggered) 0f else 1f,
        animationSpec = tween(900, easing = LinearEasing),
        label         = "heartAlpha"
    )
    val scale by animateFloatAsState(
        targetValue   = if (triggered) 1.6f else 0.8f,
        animationSpec = tween(400, easing = EaseOutBack),
        label         = "heartScale"
    )

    LaunchedEffect(key) { triggered = true }

    Icon(
        imageVector        = Icons.Filled.Favorite,
        contentDescription = null,
        tint               = Rose400,
        modifier           = modifier
            .size(34.dp)
            .graphicsLayer {
                translationY = offsetY
                scaleX       = scale
                scaleY       = scale
                this.alpha   = alpha
            }
    )
}