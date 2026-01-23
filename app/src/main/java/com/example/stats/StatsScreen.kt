package com.relife.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.relife.data.model.DayActivity
import com.relife.data.model.Transaction
import com.relife.data.model.TransactionType
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(onBack: () -> Unit) {
    val stats = remember { MockData.userStats }
    var selectedRange by remember { mutableStateOf("Semana") }
    val ranges = listOf("Semana", "Mes", "Año")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Range selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ranges.forEach { range ->
                        ReLifeChip(
                            text = range,
                            selected = selectedRange == range,
                            onClick = { selectedRange = range }
                        )
                    }
                }
            }
            
            // Main stats grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Visualizaciones",
                        value = "${stats.views}",
                        change = stats.viewsChange,
                        icon = Icons.Outlined.Visibility,
                        iconTint = Blue500,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Seguidores",
                        value = "${stats.followers}",
                        change = stats.followersChange,
                        icon = Icons.Outlined.People,
                        iconTint = Violet500,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Likes",
                        value = "${stats.totalLikes}",
                        change = stats.likesChange,
                        icon = Icons.Outlined.Favorite,
                        iconTint = Rose500,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Comentarios",
                        value = "${stats.comments}",
                        change = stats.commentsChange,
                        icon = Icons.Outlined.ChatBubble,
                        iconTint = Emerald500,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Sales stats
            item {
                SalesStatCard(
                    totalIncome = stats.salesStats.totalIncome,
                    monthlyIncome = stats.salesStats.monthlyIncome,
                    productsOnSale = stats.salesStats.productsOnSale,
                    pendingOrders = stats.salesStats.pendingOrders
                )
            }
            
            // Weekly activity chart
            item {
                Card(
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Actividad semanal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        WeeklyActivityChart(activities = stats.weeklyActivity)
                    }
                }
            }
            
            // Engagement
            item {
                Card(
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Engagement",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        EngagementBar(label = "Likes", value = stats.engagement.likesRate, color = Rose500)
                        Spacer(modifier = Modifier.height(12.dp))
                        EngagementBar(label = "Comentarios", value = stats.engagement.commentsRate, color = Blue500)
                        Spacer(modifier = Modifier.height(12.dp))
                        EngagementBar(label = "Compartidos", value = stats.engagement.sharesRate, color = Emerald500)
                        Spacer(modifier = Modifier.height(12.dp))
                        EngagementBar(label = "Guardados", value = stats.engagement.savesRate, color = Amber500)
                    }
                }
            }
            
            // Recent transactions
            item {
                Card(
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(
                            title = "Transacciones recientes",
                            action = "Ver todas",
                            onActionClick = { }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        stats.recentTransactions.forEach { transaction ->
                            TransactionItem(transaction = transaction)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            
            // Top products
            item {
                Card(
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Más vistos")
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        stats.topProducts.forEachIndexed { index, product ->
                            TopProductItem(rank = index + 1, product = product)
                            if (index < stats.topProducts.lastIndex) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
            
            // Audience insights
            item {
                Card(
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Insights de audiencia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            InsightItem(
                                icon = Icons.Outlined.People,
                                label = "Edad principal",
                                value = stats.audienceInsights.topAgeRange,
                                modifier = Modifier.weight(1f)
                            )
                            InsightItem(
                                icon = Icons.Outlined.LocationOn,
                                label = "Ciudad top",
                                value = stats.audienceInsights.topCity,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            InsightItem(
                                icon = Icons.Outlined.Schedule,
                                label = "Mejor hora",
                                value = stats.audienceInsights.bestHour,
                                modifier = Modifier.weight(1f)
                            )
                            InsightItem(
                                icon = Icons.Outlined.CalendarToday,
                                label = "Mejor día",
                                value = stats.audienceInsights.bestDay,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyActivityChart(activities: List<DayActivity>) {
    val maxValue = activities.maxOfOrNull { it.value } ?: 1
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        activities.forEach { activity ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height((100 * activity.value / maxValue).dp)
                        .background(Emerald500, CardShape)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = activity.day.take(3),
                    style = MaterialTheme.typography.labelSmall,
                    color = Stone500
                )
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (transaction.type == TransactionType.SALE) Emerald100 else Rose100,
                    CardShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (transaction.type == TransactionType.SALE) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = if (transaction.type == TransactionType.SALE) Emerald600 else Rose600,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = transaction.productTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = getDateString(transaction.date), style = MaterialTheme.typography.bodySmall, color = Stone500)
        }
        Text(
            text = "${if (transaction.type == TransactionType.SALE) "+" else "-"}€${String.format("%.2f", transaction.amount)}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (transaction.type == TransactionType.SALE) Emerald600 else Rose600
        )
    }
}

@Composable
private fun TopProductItem(rank: Int, product: com.relife.data.model.ProductPreview) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (rank == 1) Amber500 else Stone400
        )
        Spacer(modifier = Modifier.width(12.dp))
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.title,
            modifier = Modifier.size(48.dp).background(Stone100, CardShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Visibility, null, tint = Stone400, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${product.views}", style = MaterialTheme.typography.bodySmall, color = Stone600)
        }
    }
}

@Composable
private fun InsightItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).background(Emerald100, CardShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Emerald600, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Stone500)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun getDateString(timestamp: Long): String {
    val days = (System.currentTimeMillis() - timestamp) / 86400000
    return if (days == 0L) "Hoy" else "Hace ${days}d"
}
