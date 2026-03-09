package com.relife.ui.stats

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.relife.data.model.*
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

private enum class RangeOption(val label: String) {
    WEEK("Semana"), MONTH("Mes"), YEAR("Año"), CUSTOM("Personalizado")
}

// Fake sparkline data per range for demo
private val sparklineData = mapOf(
    RangeOption.WEEK  to listOf(12, 18, 14, 22, 19, 28, 24),
    RangeOption.MONTH to listOf(80, 95, 70, 110, 105, 130, 120, 145, 138, 160, 155, 175),
    RangeOption.YEAR  to listOf(400, 520, 480, 610, 590, 720, 700, 810, 790, 900, 860, 950),
    RangeOption.CUSTOM to listOf(15, 20, 17, 25, 22, 30, 27)
)

// Fake comparison deltas (current vs prev period)
private val comparisonData = mapOf(
    RangeOption.WEEK  to mapOf("views" to +18f, "followers" to +5f, "likes" to +12f, "comments" to +8f),
    RangeOption.MONTH to mapOf("views" to +32f, "followers" to +14f, "likes" to +27f, "comments" to +19f),
    RangeOption.YEAR  to mapOf("views" to +65f, "followers" to +41f, "likes" to +53f, "comments" to +38f),
    RangeOption.CUSTOM to mapOf("views" to +10f, "followers" to +3f, "likes" to +7f, "comments" to +4f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(onBack: () -> Unit) {
    val stats        = remember { MockData.userStats }
    var selectedRange by remember { mutableStateOf(RangeOption.WEEK) }
    var showShareSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val spark   = sparklineData[selectedRange] ?: sparklineData[RangeOption.WEEK]!!
    val compare = comparisonData[selectedRange] ?: comparisonData[RangeOption.WEEK]!!

    // Animated bar values
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(selectedRange) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, tween(800, easing = EaseOutCubic))
    }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {

        // ── Gradient Header ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Emerald600, Teal500, Cyan400)))
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                // Top row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick  = onBack,
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(0.2f))
                        ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Estadísticas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Panel de rendimiento", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.8f))
                        }
                    }
                    // Export/share
                    Surface(
                        modifier = Modifier.clickable { showShareSheet = true },
                        shape    = RoundedCornerShape(12.dp),
                        color    = Color.White.copy(0.2f)
                    ) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.IosShare, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Exportar", style = MaterialTheme.typography.labelMedium, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick KPI summary row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HeaderKpi("${stats.views}", "Vistas",     Icons.Outlined.Visibility)
                    KpiDivider()
                    HeaderKpi("${stats.followers}", "Seguidores", Icons.Outlined.People)
                    KpiDivider()
                    HeaderKpi("${stats.totalLikes}", "Likes",      Icons.Outlined.Favorite)
                    KpiDivider()
                    HeaderKpi("€${stats.salesStats.monthlyIncome.toInt()}", "Ingresos", Icons.Outlined.TrendingUp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Range selector chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RangeOption.entries.forEach { opt ->
                        val sel = selectedRange == opt
                        val bg by animateColorAsState(if (sel) Color.White else Color.White.copy(0.2f), tween(200), label = "range$opt")
                        Surface(
                            modifier  = Modifier.clickable {
                                if (opt == RangeOption.CUSTOM) showDatePicker = true
                                else selectedRange = opt
                            },
                            shape     = RoundedCornerShape(20.dp),
                            color     = bg
                        ) {
                            Row(
                                modifier          = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (opt == RangeOption.CUSTOM) Icon(Icons.Default.DateRange, null, tint = if (sel) Emerald600 else Color.White, modifier = Modifier.size(12.dp).padding(end = 2.dp))
                                Text(opt.label, style = MaterialTheme.typography.labelMedium, color = if (sel) Emerald700 else Color.White, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
        }

        // ── Body ──────────────────────────────────────────────────────────────
        LazyColumn(
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Stat cards 2x2 with sparkline + comparison ────────────────────
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SparklineStatCard(
                        title      = "Visualizaciones",
                        value      = "${stats.views}",
                        change     = stats.viewsChange,
                        vsChange   = compare["views"] ?: 0f,
                        icon       = Icons.Outlined.Visibility,
                        iconColor  = Blue500,
                        sparkData  = spark,
                        sparkColor = Blue400,
                        modifier   = Modifier.weight(1f)
                    )
                    SparklineStatCard(
                        title      = "Seguidores",
                        value      = "${stats.followers}",
                        change     = stats.followersChange,
                        vsChange   = compare["followers"] ?: 0f,
                        icon       = Icons.Outlined.People,
                        iconColor  = Violet500,
                        sparkData  = spark.map { (it * 0.6).toInt() },
                        sparkColor = Violet400,
                        modifier   = Modifier.weight(1f)
                    )
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SparklineStatCard(
                        title      = "Likes",
                        value      = "${stats.totalLikes}",
                        change     = stats.likesChange,
                        vsChange   = compare["likes"] ?: 0f,
                        icon       = Icons.Outlined.Favorite,
                        iconColor  = Rose500,
                        sparkData  = spark.map { (it * 1.3).toInt() },
                        sparkColor = Rose400,
                        modifier   = Modifier.weight(1f)
                    )
                    SparklineStatCard(
                        title      = "Comentarios",
                        value      = "${stats.comments}",
                        change     = stats.commentsChange,
                        vsChange   = compare["comments"] ?: 0f,
                        icon       = Icons.Outlined.ChatBubble,
                        iconColor  = Emerald500,
                        sparkData  = spark.map { (it * 0.4).toInt() },
                        sparkColor = Emerald400,
                        modifier   = Modifier.weight(1f)
                    )
                }
            }

            // ── Sales card ────────────────────────────────────────────────────
            item {
                SalesStatCard(
                    totalIncome    = stats.salesStats.totalIncome,
                    monthlyIncome  = stats.salesStats.monthlyIncome,
                    productsOnSale = stats.salesStats.productsOnSale,
                    pendingOrders  = stats.salesStats.pendingOrders
                )
            }

            // ── Animated bar chart ────────────────────────────────────────────
            item {
                StatsCard(title = "Actividad semanal", icon = Icons.Outlined.BarChart) {
                    AnimatedBarChart(
                        activities   = stats.weeklyActivity,
                        animProgress = animProgress.value
                    )
                }
            }

            // ── Engagement bars ───────────────────────────────────────────────
            item {
                StatsCard(title = "Engagement", icon = Icons.Outlined.Insights) {
                    listOf(
                        Triple("Likes",       stats.engagement.likesRate,    Rose500),
                        Triple("Comentarios", stats.engagement.commentsRate, Blue500),
                        Triple("Compartidos", stats.engagement.sharesRate,   Emerald500),
                        Triple("Guardados",   stats.engagement.savesRate,    Amber500)
                    ).forEach { (label, value, color) ->
                        AnimatedEngagementBar(label = label, value = value, color = color, animProgress = animProgress.value)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            // ── Audience donut chart ──────────────────────────────────────────
            item {
                StatsCard(title = "Audiencia", icon = Icons.Outlined.People) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        // Donut chart
                        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                            DonutChart(
                                segments    = listOf(
                                    DonutSegment("18-24", 0.20f, Cyan400),
                                    DonutSegment("25-34", 0.38f, Emerald500),
                                    DonutSegment("35-44", 0.27f, Teal400),
                                    DonutSegment("45+",   0.15f, Stone300)
                                ),
                                animProgress = animProgress.value
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("38%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Emerald600)
                                Text("25-34", style = MaterialTheme.typography.labelSmall, color = Stone500)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        // Legend
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                Triple("18-24", "20%", Cyan400),
                                Triple("25-34", "38%", Emerald500),
                                Triple("35-44", "27%", Teal400),
                                Triple("45+",   "15%", Stone300)
                            ).forEach { (label, pct, color) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(40.dp))
                                    Text(pct, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Stone700)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        // Insight pills
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            InsightPill(Icons.Outlined.LocationOn, stats.audienceInsights.topCity)
                            InsightPill(Icons.Outlined.Schedule,   stats.audienceInsights.bestHour)
                            InsightPill(Icons.Outlined.CalendarToday, stats.audienceInsights.bestDay)
                        }
                    }
                }
            }

            // ── Top posts ranked ──────────────────────────────────────────────
            item {
                StatsCard(title = "Posts más vistos", icon = Icons.Outlined.EmojiEvents) {
                    stats.topProducts.forEachIndexed { index, product ->
                        RankedPostItem(rank = index + 1, product = product)
                        if (index < stats.topProducts.lastIndex) {
                            HorizontalDivider(color = Stone100, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }

            // ── Transactions ──────────────────────────────────────────────────
            item {
                StatsCard(title = "Transacciones recientes", icon = Icons.Outlined.Receipt, action = "Ver todas") {
                    stats.recentTransactions.forEach { tx ->
                        TransactionItem(transaction = tx)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }

    // ── Export sheet ──────────────────────────────────────────────────────────
    if (showShareSheet) {
        ExportSheet(stats = stats, range = selectedRange.label, onDismiss = { showShareSheet = false })
    }

    // ── Date picker dialog ────────────────────────────────────────────────────
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title   = { Text("Rango personalizado", fontWeight = FontWeight.Bold) },
            text    = {
                Text("Selecciona el período que quieres analizar.\n(Integración con DatePicker próximamente)", color = Stone500)
            },
            confirmButton = {
                TextButton(onClick = { selectedRange = RangeOption.CUSTOM; showDatePicker = false }) {
                    Text("Aplicar", color = Emerald600)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        )
    }
}

// ─── Header KPI ───────────────────────────────────────────────────────────────
@Composable
private fun HeaderKpi(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.height(3.dp))
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = Color.White)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.75f))
    }
}

@Composable
private fun KpiDivider() {
    Box(modifier = Modifier.height(36.dp).width(1.dp).background(Color.White.copy(0.25f)))
}

// ─── Sparkline Stat Card ──────────────────────────────────────────────────────
@Composable
private fun SparklineStatCard(
    title: String, value: String, change: Float, vsChange: Float,
    icon: ImageVector, iconColor: Color,
    sparkData: List<Int>, sparkColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = CardShape, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(iconColor.copy(0.12f)),
                    contentAlignment = Alignment.Center
                ) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp)) }
                // VS comparison badge
                Surface(shape = RoundedCornerShape(6.dp), color = if (vsChange >= 0) Emerald50 else Rose50) {
                    Text(
                        "${if (vsChange >= 0) "+" else ""}${vsChange.toInt()}% vs ant.",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = if (vsChange >= 0) Emerald600 else Rose600,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text(title, style = MaterialTheme.typography.labelSmall, color = Stone500)
            Spacer(modifier = Modifier.height(4.dp))
            // Change indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (change >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    null, tint = if (change >= 0) Emerald500 else Rose500, modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text("${if (change >= 0) "+" else ""}${change.toInt()}%", style = MaterialTheme.typography.labelSmall, color = if (change >= 0) Emerald600 else Rose600, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Mini sparkline
            MiniSparkline(data = sparkData, color = sparkColor)
        }
    }
}

// ─── Mini Sparkline ───────────────────────────────────────────────────────────
@Composable
private fun MiniSparkline(data: List<Int>, color: Color) {
    val animProg = remember { Animatable(0f) }
    LaunchedEffect(data) { animProg.snapTo(0f); animProg.animateTo(1f, tween(600)) }

    Canvas(modifier = Modifier.fillMaxWidth().height(32.dp)) {
        if (data.size < 2) return@Canvas
        val maxV  = data.max().toFloat().coerceAtLeast(1f)
        val stepX = size.width / (data.size - 1)
        val pts   = data.mapIndexed { i, v ->
            Offset(i * stepX, size.height - (v / maxV) * size.height * animProg.value)
        }
        // Gradient fill
        val path = Path().apply {
            moveTo(pts.first().x, size.height)
            pts.forEach { lineTo(it.x, it.y) }
            lineTo(pts.last().x, size.height)
            close()
        }
        drawPath(path, Brush.verticalGradient(listOf(color.copy(0.3f), Color.Transparent)))
        // Line
        for (i in 0 until pts.size - 1) {
            drawLine(color, pts[i], pts[i + 1], strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        }
    }
}

// ─── Animated Bar Chart ───────────────────────────────────────────────────────
@Composable
private fun AnimatedBarChart(activities: List<DayActivity>, animProgress: Float) {
    val maxValue = activities.maxOfOrNull { it.value }?.toFloat()?.coerceAtLeast(1f) ?: 1f
    val gradientBrush = Brush.verticalGradient(listOf(Emerald400, Teal500))

    Row(
        modifier              = Modifier.fillMaxWidth().height(130.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.Bottom
    ) {
        activities.forEach { activity ->
            val targetH = (100 * activity.value / maxValue * animProgress).dp
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${activity.value}", style = MaterialTheme.typography.labelSmall, color = Stone400, fontSize = 9.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(targetH)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(gradientBrush)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(activity.day.take(3), style = MaterialTheme.typography.labelSmall, color = Stone500)
            }
        }
    }
}

// ─── Animated Engagement Bar ──────────────────────────────────────────────────
@Composable
private fun AnimatedEngagementBar(label: String, value: Float, color: Color, animProgress: Float) {
    val animatedWidth = value * animProgress
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(Stone100)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Brush.horizontalGradient(listOf(color.copy(0.7f), color)))
            )
        }
    }
}

// ─── Donut Chart ─────────────────────────────────────────────────────────────
private data class DonutSegment(val label: String, val fraction: Float, val color: Color)

@Composable
private fun DonutChart(segments: List<DonutSegment>, animProgress: Float) {
    Canvas(modifier = Modifier.size(120.dp)) {
        val stroke    = 18.dp.toPx()
        val inset     = stroke / 2
        val arcRect   = Size(size.width - stroke, size.height - stroke)
        var startAngle = -90f
        segments.forEach { seg ->
            val sweep = seg.fraction * 360f * animProgress
            drawArc(
                color      = seg.color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter  = false,
                topLeft    = Offset(inset, inset),
                size       = arcRect,
                style      = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            startAngle += seg.fraction * 360f
        }
    }
}

// ─── Ranked Post Item ────────────────────────────────────────────────────────
@Composable
private fun RankedPostItem(rank: Int, product: ProductPreview) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        // Rank badge
        Box(
            modifier         = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(when (rank) { 1 -> Amber50; 2 -> Stone100; 3 -> Emerald50; else -> Stone50 }),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "#$rank",
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color      = when (rank) { 1 -> Amber600; 2 -> Stone500; 3 -> Emerald600; else -> Stone400 }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        // Thumbnail
        AsyncImage(
            model              = product.imageUrl,
            contentDescription = product.title,
            modifier           = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)),
            contentScale       = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(product.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(3.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Visibility, null, tint = Stone400, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${product.views}", style = MaterialTheme.typography.labelSmall, color = Stone500)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Favorite, null, tint = Rose400, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${product.views / 4}", style = MaterialTheme.typography.labelSmall, color = Stone500)
                }
            }
        }
        // Mini bar showing relative performance
        val maxViews = 500
        Box(
            modifier = Modifier.width(50.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Stone100)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((product.views.toFloat() / maxViews).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(Brush.horizontalGradient(listOf(Emerald400, Teal500)))
            )
        }
    }
}

// ─── Transaction Item ─────────────────────────────────────────────────────────
@Composable
private fun TransactionItem(transaction: Transaction) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val isSale = transaction.type == TransactionType.SALE
        Box(
            modifier         = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                .background(if (isSale) Emerald50 else Rose50),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isSale) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                null, tint = if (isSale) Emerald600 else Rose600, modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.productTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(getDateString(transaction.date), style = MaterialTheme.typography.labelSmall, color = Stone400)
        }
        Text(
            "${if (isSale) "+" else "-"}€${String.format("%.2f", transaction.amount)}",
            style      = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color      = if (isSale) Emerald600 else Rose600
        )
    }
}

// ─── Insight Pill ─────────────────────────────────────────────────────────────
@Composable
private fun InsightPill(icon: ImageVector, value: String) {
    Surface(shape = RoundedCornerShape(10.dp), color = Stone50) {
        Row(
            modifier          = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Emerald600, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Stone700)
        }
    }
}

// ─── Stats Card wrapper ───────────────────────────────────────────────────────
@Composable
private fun StatsCard(title: String, icon: ImageVector, action: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = CardShape, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier         = Modifier.size(32.dp).clip(RoundedCornerShape(9.dp)).background(Emerald50),
                        contentAlignment = Alignment.Center
                    ) { Icon(icon, null, tint = Emerald600, modifier = Modifier.size(17.dp)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                if (action != null) {
                    Text(action, style = MaterialTheme.typography.labelSmall, color = Emerald600, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { })
                }
            }
            content()
        }
    }
}

// ─── Export Sheet ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportSheet(stats: UserStats, range: String, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 36.dp)) {
            Text("Exportar estadísticas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Período: $range", style = MaterialTheme.typography.bodySmall, color = Stone400)
            Spacer(modifier = Modifier.height(20.dp))

            // Summary card
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Emerald600, Teal500)))
                    .padding(16.dp)
            ) {
                Column {
                    Text("Resumen de rendimiento", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ExportKpi("${stats.views}", "Vistas")
                        ExportKpi("${stats.followers}", "Seguidores")
                        ExportKpi("€${stats.salesStats.totalIncome.toInt()}", "Ingresos")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Export options
            listOf(
                Triple("PDF Report", Icons.Default.PictureAsPdf, Rose500),
                Triple("Imagen PNG", Icons.Default.Image,        Blue500),
                Triple("CSV datos",  Icons.Default.TableChart,   Emerald500),
                Triple("Compartir",  Icons.Default.Share,        Violet500)
            ).forEach { (label, icon, color) ->
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onDismiss() },
                    shape    = RoundedCornerShape(14.dp),
                    color    = Stone50
                ) {
                    Row(
                        modifier          = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier         = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(0.12f)),
                            contentAlignment = Alignment.Center
                        ) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, null, tint = Stone400, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportKpi(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.White)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.8f))
    }
}

private fun getDateString(timestamp: Long): String {
    val days = (System.currentTimeMillis() - timestamp) / 86400000
    return if (days == 0L) "Hoy" else "Hace ${days}d"
}