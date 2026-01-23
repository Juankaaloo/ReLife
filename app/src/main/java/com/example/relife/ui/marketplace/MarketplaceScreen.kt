package com.relife.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.relife.data.model.PostCategory
import com.relife.data.model.Product
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

@Composable
fun MarketplaceScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PostCategory.ALL) }
    var isGridView by remember { mutableStateOf(true) }
    var showCreateDialog by remember { mutableStateOf(false) }
    val products = remember { MockData.products }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Marketplace",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    PrimaryButton(
                        text = "Vender",
                        onClick = { showCreateDialog = true },
                        icon = Icons.Default.Add,
                        modifier = Modifier.height(44.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Buscar productos...",
                        leadingIcon = Icons.Default.Search,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Cambiar vista",
                            tint = Stone600
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PostCategory.values().forEach { category ->
                        ReLifeChip(
                            text = category.displayName,
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }
        }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isGridView) 2 else 1),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                products.filter { selectedCategory == PostCategory.ALL || it.category == selectedCategory }
            ) { product ->
                if (isGridView) {
                    ProductCard(
                        product = product,
                        onClick = { },
                        onFavoriteClick = { }
                    )
                } else {
                    ProductListItem(product = product, onClick = { }, onFavoriteClick = { })
                }
            }
        }
    }
    
    if (showCreateDialog) {
        CreateProductDialog(onDismiss = { showCreateDialog = false }, onConfirm = { showCreateDialog = false })
    }
}

@Composable
private fun ProductListItem(product: Product, onClick: () -> Unit, onFavoriteClick: () -> Unit) {
    var isFavorite by remember { mutableStateOf(product.isFavorite) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Card(modifier = Modifier.size(100.dp), shape = CardShape) {
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "€${String.format("%.2f", product.price)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Emerald600)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(imageUrl = product.seller.avatarUrl, size = 20.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = product.seller.name, style = MaterialTheme.typography.bodySmall, color = Stone500)
                }
            }
            IconButton(onClick = { isFavorite = !isFavorite; onFavoriteClick() }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) Rose500 else Stone400
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProductDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var shippingAvailable by remember { mutableStateOf(false) }
    
    AlertDialog(onDismissRequest = onDismiss) {
        Card(shape = DialogShape, colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Text(text = "Nuevo producto", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(modifier = Modifier.fillMaxWidth().height(100.dp), shape = CardShape, colors = CardDefaults.cardColors(containerColor = Stone100)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = Stone400, modifier = Modifier.size(32.dp))
                            Text("Añadir fotos", style = MaterialTheme.typography.bodySmall, color = Stone500)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                ReLifeTextField(value = title, onValueChange = { title = it }, label = "Título")
                Spacer(modifier = Modifier.height(12.dp))
                ReLifeTextField(value = description, onValueChange = { description = it }, label = "Descripción", singleLine = false, maxLines = 3)
                Spacer(modifier = Modifier.height(12.dp))
                ReLifeTextField(value = price, onValueChange = { price = it }, label = "Precio (€)", keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Envío disponible", style = MaterialTheme.typography.bodyLarge)
                    ReLifeSwitch(checked = shippingAvailable, onCheckedChange = { shippingAvailable = it })
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar", color = Stone600) }
                    Spacer(modifier = Modifier.width(8.dp))
                    PrimaryButton(text = "Publicar", onClick = onConfirm)
                }
            }
        }
    }
}
