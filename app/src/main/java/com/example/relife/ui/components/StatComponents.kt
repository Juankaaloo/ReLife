package com.relife.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.relife.ui.theme.*

@Composable
fun StatCard(
    title: String,
    value: String,
    change: Float,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconTint: Color = Emerald500
) {
    Card(
        modifier = modifier,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconTint.copy(alpha = 0.1f), shape = CardShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                ChangeIndicator(change = change)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Stone500
            )
        }
    }
}

@Composable
fun ChangeIndicator(
    change: Float,
    modifier: Modifier = Modifier
) {
    val isPositive = change >= 0
    val color = if (isPositive) Success else Error
    val icon = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown
    
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.1f), shape = ChipShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${if (isPositive) "+" else ""}${String.format("%.1f", change)}%",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SalesStatCard(
    totalIncome: Double,
    monthlyIncome: Double,
    productsOnSale: Int,
    pendingOrders: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Emerald500, Teal500, Cyan500)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Ventas",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SalesStatItem(
                        label = "Total",
                        value = "€${String.format("%.2f", totalIncome)}"
                    )
                    SalesStatItem(
                        label = "Este mes",
                        value = "€${String.format("%.2f", monthlyIncome)}"
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SalesStatItem(
                        label = "En venta",
                        value = "$productsOnSale"
                    )
                    SalesStatItem(
                        label = "Pendientes",
                        value = "$pendingOrders"
                    )
                }
            }
        }
    }
}

@Composable
private fun SalesStatItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun EngagementBar(
    label: String,
    value: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Stone600
            )
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Stone800
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = Stone200,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
