package com.relife.ui.marketplace

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.relife.data.model.PostCategory
import com.relife.data.model.Product
import com.relife.data.model.ProductCondition
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

// ── Sort options ───────────────────────────────────────────────────────────────
private enum class SortOption(val label: String) {
    NEWEST("Más nuevos"),
    PRICE_ASC("Precio: menor"),
    PRICE_DESC("Precio: mayor"),
    RATING("Mejor valorados"),
    POPULAR("Más populares")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    isGuest: Boolean = false,
    onRequestLogin: () -> Unit = {}
) {
    var searchQuery       by remember { mutableStateOf("") }
    var selectedCategory  by remember { mutableStateOf(PostCategory.ALL) }
    var isGridView        by remember { mutableStateOf(true) }
    var showCreateDialog  by remember { mutableStateOf(false) }
    var showFiltersSheet  by remember { mutableStateOf(false) }
    var showGuestDialog   by remember { mutableStateOf(false) }
    var sortOption        by remember { mutableStateOf(SortOption.NEWEST) }
    var onlyShipping      by remember { mutableStateOf(false) }
    var maxPrice          by remember { mutableFloatStateOf(500f) }
    var selectedCondition by remember { mutableStateOf<ProductCondition?>(null) }

    val products = remember {
        if (isGuest) MockData.products.map { it.copy(isFavorite = false) }
        else MockData.products
    }

    // Active filter count badge
    val activeFilters = listOf(
        onlyShipping,
        selectedCondition != null,
        maxPrice < 500f
    ).count { it }

    val filteredProducts = remember(searchQuery, selectedCategory, sortOption, onlyShipping, maxPrice, selectedCondition) {
        products
            .filter { p ->
                val matchCat   = selectedCategory == PostCategory.ALL || p.category == selectedCategory
                val matchSearch = searchQuery.isEmpty() ||
                        p.title.contains(searchQuery, ignoreCase = true) ||
                        p.description.contains(searchQuery, ignoreCase = true)
                val matchShip  = !onlyShipping || p.shippingAvailable
                val matchPrice = p.price <= maxPrice
                val matchCond  = selectedCondition == null || p.condition == selectedCondition
                matchCat && matchSearch && matchShip && matchPrice && matchCond
            }
            .let { list ->
                when (sortOption) {
                    SortOption.PRICE_ASC  -> list.sortedBy { it.price }
                    SortOption.PRICE_DESC -> list.sortedByDescending { it.price }
                    SortOption.RATING     -> list.sortedByDescending { it.rating }
                    SortOption.POPULAR    -> list.sortedByDescending { it.reviewsCount }
                    SortOption.NEWEST     -> list
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {

            // ── Header ────────────────────────────────────────────────────────
            MarketplaceHeader(
                searchQuery      = searchQuery,
                onSearchChange   = { searchQuery = it },
                isGridView       = isGridView,
                onViewToggle     = { isGridView = !isGridView },
                onSellClick      = {
                    if (isGuest) showGuestDialog = true else showCreateDialog = true
                },
                activeFilters    = activeFilters,
                onFiltersClick   = { showFiltersSheet = true }
            )

            // ── Promo Banner ──────────────────────────────────────────────────
            PromoBanner()

            // ── Category chips ────────────────────────────────────────────────
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val cats = listOf(
                    PostCategory.ALL        to Icons.Default.Dashboard,
                    PostCategory.FURNITURE  to Icons.Default.Chair,
                    PostCategory.LIGHTING   to Icons.Default.Lightbulb,
                    PostCategory.DECORATION to Icons.Default.Palette,
                    PostCategory.FASHION    to Icons.Default.Checkroom,
                    PostCategory.GARDEN     to Icons.Default.Yard,
                    PostCategory.TECH       to Icons.Default.PhoneAndroid
                )
                items(cats) { (cat, icon) ->
                    val sel = selectedCategory == cat
                    val bg by animateColorAsState(if (sel) Emerald500 else Color.White, tween(200), label = "catBg")
                    Surface(
                        modifier        = Modifier.clickable { selectedCategory = cat },
                        shape           = RoundedCornerShape(20.dp),
                        color           = bg,
                        shadowElevation = if (sel) 0.dp else 2.dp
                    ) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, null, tint = if (sel) Color.White else Emerald600, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                cat.displayName,
                                style      = MaterialTheme.typography.labelMedium,
                                color      = if (sel) Color.White else Stone700,
                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // ── Sort row + results count ───────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "${filteredProducts.size} producto${if (filteredProducts.size != 1) "s" else ""}",
                    style      = MaterialTheme.typography.bodySmall,
                    color      = Stone500,
                    fontWeight = FontWeight.Medium
                )
                SortDropdown(sortOption = sortOption, onSortChange = { sortOption = it })
            }

            // ── Products grid/list ────────────────────────────────────────────
            if (filteredProducts.isEmpty()) {
                EmptyState(
                    icon        = Icons.Outlined.SearchOff,
                    title       = "Sin resultados",
                    description = "Prueba con otros filtros o categorías",
                    modifier    = Modifier.fillMaxSize()
                )
            } else {
                AnimatedContent(
                    targetState = isGridView,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "viewMode"
                ) { grid ->
                    if (grid) {
                        LazyVerticalGrid(
                            columns               = GridCells.Fixed(2),
                            contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement   = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredProducts, key = { it.id }) { product ->
                                ImprovedProductCard(product = product)
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredProducts, key = { it.id }) { product ->
                                ProductListItem(product = product)
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Filters bottom sheet ──────────────────────────────────────────────────
    if (showFiltersSheet) {
        FiltersBottomSheet(
            onDismiss         = { showFiltersSheet = false },
            onlyShipping      = onlyShipping,
            onShippingChange  = { onlyShipping = it },
            maxPrice          = maxPrice,
            onMaxPriceChange  = { maxPrice = it },
            selectedCondition = selectedCondition,
            onConditionChange = { selectedCondition = it },
            onClearAll        = {
                onlyShipping      = false
                maxPrice          = 500f
                selectedCondition = null
                showFiltersSheet  = false
            }
        )
    }

    // ── Create product dialog ─────────────────────────────────────────────────
    if (showCreateDialog) {
        CreateProductDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { showCreateDialog = false }
        )
    }

    // ── Guest login dialog ──────────────────────────────────────────────────
    GuestLoginRequiredDialog(
        show         = showGuestDialog,
        onDismiss    = { showGuestDialog = false },
        onLoginClick = {
            showGuestDialog = false
            onRequestLogin()
        },
        title   = "Accede al marketplace",
        message = "Inicia sesión para comprar, vender y guardar productos favoritos.",
        icon    = Icons.Default.ShoppingBag
    )
}

// ─── Header ───────────────────────────────────────────────────────────────────
@Composable
private fun MarketplaceHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    isGridView: Boolean,
    onViewToggle: () -> Unit,
    onSellClick: () -> Unit,
    activeFilters: Int,
    onFiltersClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(Emerald600, Teal500, Cyan400)))
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Marketplace", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
                    Text("Compra y vende creaciones únicas", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.85f))
                }
                // Sell button
                Surface(
                    modifier  = Modifier.clickable(onClick = onSellClick),
                    shape     = RoundedCornerShape(14.dp),
                    color     = Color.White
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, null, tint = Emerald600, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Vender", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Emerald600)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search + filter + view toggle row
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                modifier              = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search
                Surface(
                    shape    = RoundedCornerShape(16.dp),
                    color    = Color.White,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, null, tint = Stone400, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        androidx.compose.foundation.text.BasicTextField(
                            value         = searchQuery,
                            onValueChange = onSearchChange,
                            modifier      = Modifier.weight(1f).padding(vertical = 8.dp),
                            textStyle     = MaterialTheme.typography.bodyMedium.copy(color = Stone800),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) Text("Buscar productos...", style = MaterialTheme.typography.bodyMedium, color = Stone400)
                                inner()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            Icon(Icons.Default.Close, null, tint = Stone400, modifier = Modifier.size(16.dp).clickable { onSearchChange("") })
                        }
                    }
                }
                // Filter button with badge
                Box {
                    IconButton(
                        onClick  = onFiltersClick,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.2f))
                    ) {
                        Icon(Icons.Default.Tune, null, tint = Color.White)
                    }
                    if (activeFilters > 0) {
                        Box(
                            modifier         = Modifier.size(16.dp).align(Alignment.TopEnd).clip(CircleShape).background(Rose500),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$activeFilters", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 9.sp)
                        }
                    }
                }
                // View toggle
                IconButton(
                    onClick  = onViewToggle,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.2f))
                ) {
                    Icon(if (isGridView) Icons.Default.ViewList else Icons.Default.GridView, null, tint = Color.White)
                }
            }
        }
    }
}

// ─── Promo Banner ─────────────────────────────────────────────────────────────
@Composable
private fun PromoBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Emerald700, Teal600)))
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(0.2f)) {
                    Text("✨ Oferta especial", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Envío gratis\nen tu primera compra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.White, lineHeight = 22.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape    = RoundedCornerShape(10.dp),
                    color    = Color.White,
                    modifier = Modifier.clickable { }
                ) {
                    Text("Ver ofertas", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Emerald700, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier         = Modifier.size(72.dp).clip(CircleShape).background(Color.White.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🚚", fontSize = 36.sp)
            }
        }
    }
}

// ─── Sort Dropdown ────────────────────────────────────────────────────────────
@Composable
private fun SortDropdown(sortOption: SortOption, onSortChange: (SortOption) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            modifier  = Modifier.clickable { expanded = true },
            shape     = RoundedCornerShape(10.dp),
            color     = Color.White,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier          = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Sort, null, tint = Emerald600, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(sortOption.label, style = MaterialTheme.typography.labelSmall, color = Stone700, fontWeight = FontWeight.Medium)
                Icon(Icons.Default.KeyboardArrowDown, null, tint = Stone500, modifier = Modifier.size(14.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SortOption.entries.forEach { opt ->
                DropdownMenuItem(
                    text = {
                        Text(
                            opt.label,
                            fontWeight = if (opt == sortOption) FontWeight.Bold else FontWeight.Normal,
                            color      = if (opt == sortOption) Emerald600 else Stone700
                        )
                    },
                    onClick = { onSortChange(opt); expanded = false },
                    leadingIcon = {
                        if (opt == sortOption) Icon(Icons.Default.Check, null, tint = Emerald500, modifier = Modifier.size(16.dp))
                    }
                )
            }
        }
    }
}

// ─── Improved Product Card (Grid) ─────────────────────────────────────────────
@Composable
private fun ImprovedProductCard(product: Product) {
    var isFav by remember { mutableStateOf(product.isFavorite) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = CardShape,
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                AsyncImage(
                    model              = product.images.firstOrNull(),
                    contentDescription = product.title,
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop
                )
                // Shipping badge
                if (product.shippingAvailable) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(6.dp),
                        shape    = RoundedCornerShape(8.dp),
                        color    = Emerald500
                    ) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocalShipping, null, tint = Color.White, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Envío", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 9.sp)
                        }
                    }
                }
                // Condition badge
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp),
                    shape    = RoundedCornerShape(8.dp),
                    color    = when (product.condition) {
                        ProductCondition.NEW      -> Blue500.copy(0.9f)
                        ProductCondition.RESTORED -> Emerald500.copy(0.9f)
                        ProductCondition.USED     -> Amber500.copy(0.9f)
                    }
                ) {
                    Text(
                        product.condition.displayName,
                        style    = MaterialTheme.typography.labelSmall,
                        color    = Color.White,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                // Fav button
                IconButton(
                    onClick  = { isFav = !isFav },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).size(30.dp).clip(CircleShape).background(Color.White.copy(0.9f))
                ) {
                    Icon(
                        if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        null, tint = if (isFav) Rose500 else Stone400, modifier = Modifier.size(16.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(product.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("€${String.format("%.2f", product.price)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Emerald600)
                    if (product.rating > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, null, tint = Amber400, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(String.format("%.1f", product.rating), style = MaterialTheme.typography.labelSmall, color = Stone500)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(imageUrl = product.seller.avatarUrl, size = 18.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(product.seller.name.split(" ").first(), style = MaterialTheme.typography.labelSmall, color = Stone400)
                    if (product.location != null) {
                        Text(" · ${product.location}", style = MaterialTheme.typography.labelSmall, color = Stone400, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

// ─── Product List Item ────────────────────────────────────────────────────────
@Composable
private fun ProductListItem(product: Product) {
    var isFav by remember { mutableStateOf(product.isFavorite) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = CardShape,
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(120.dp)) {
            Box(modifier = Modifier.width(120.dp).fillMaxHeight()) {
                AsyncImage(
                    model              = product.images.firstOrNull(),
                    contentDescription = product.title,
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop
                )
                if (product.shippingAvailable) {
                    Box(
                        modifier = Modifier.align(Alignment.BottomStart).padding(6.dp)
                            .clip(RoundedCornerShape(6.dp)).background(Emerald500).padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text("Envío", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 9.sp)
                    }
                }
            }
            Column(
                modifier            = Modifier.weight(1f).padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Surface(shape = RoundedCornerShape(6.dp), color = when (product.condition) {
                        ProductCondition.NEW      -> Blue50
                        ProductCondition.RESTORED -> Emerald50
                        ProductCondition.USED     -> Amber50
                    }) {
                        Text(
                            product.condition.displayName,
                            style    = MaterialTheme.typography.labelSmall,
                            color    = when (product.condition) {
                                ProductCondition.NEW      -> Blue600
                                ProductCondition.RESTORED -> Emerald700
                                ProductCondition.USED     -> Amber600
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(product.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("€${String.format("%.2f", product.price)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Emerald600)
                        if (product.rating > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, null, tint = Amber400, modifier = Modifier.size(11.dp))
                                Text(" ${String.format("%.1f", product.rating)} (${product.reviewsCount})", style = MaterialTheme.typography.labelSmall, color = Stone400)
                            }
                        }
                    }
                    IconButton(onClick = { isFav = !isFav }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            null, tint = if (isFav) Rose500 else Stone300, modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Filters Bottom Sheet ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersBottomSheet(
    onDismiss: () -> Unit,
    onlyShipping: Boolean,
    onShippingChange: (Boolean) -> Unit,
    maxPrice: Float,
    onMaxPriceChange: (Float) -> Unit,
    selectedCondition: ProductCondition?,
    onConditionChange: (ProductCondition?) -> Unit,
    onClearAll: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = sheetState,
        containerColor    = Color.White,
        shape             = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            // Handle + title
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Filtros", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = onClearAll) {
                    Text("Limpiar todo", color = Rose500, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Shipping toggle
            Row(
                modifier              = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Stone50).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, null, tint = Emerald500, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Solo con envío", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("Productos con envío disponible", style = MaterialTheme.typography.bodySmall, color = Stone400)
                    }
                }
                ReLifeSwitch(checked = onlyShipping, onCheckedChange = onShippingChange)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Price slider
            Text("Precio máximo: €${maxPrice.toInt()}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value         = maxPrice,
                onValueChange = onMaxPriceChange,
                valueRange    = 10f..500f,
                steps         = 48,
                colors        = SliderDefaults.colors(
                    thumbColor       = Emerald500,
                    activeTrackColor = Emerald500,
                    inactiveTrackColor = Stone200
                )
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("€10", style = MaterialTheme.typography.labelSmall, color = Stone400)
                Text("€500", style = MaterialTheme.typography.labelSmall, color = Stone400)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Condition selector
            Text("Condición", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(null to "Todas", ProductCondition.NEW to "Nuevo", ProductCondition.RESTORED to "Restaurado", ProductCondition.USED to "Usado")
                    .forEach { (cond, label) ->
                        val sel = selectedCondition == cond
                        Surface(
                            modifier  = Modifier.clickable { onConditionChange(cond) },
                            shape     = RoundedCornerShape(20.dp),
                            color     = if (sel) Emerald500 else Stone100
                        ) {
                            Text(
                                label,
                                style      = MaterialTheme.typography.labelMedium,
                                color      = if (sel) Color.White else Stone600,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                modifier   = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(text = "Aplicar filtros", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
        }
    }
}

// ─── Create Product Dialog ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProductDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    var title            by remember { mutableStateOf("") }
    var description      by remember { mutableStateOf("") }
    var price            by remember { mutableStateOf("") }
    var shippingAvailable by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(PostCategory.FURNITURE) }
    var selectedCondition by remember { mutableStateOf(ProductCondition.NEW) }

    AlertDialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier         = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(listOf(Emerald400, Teal500))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Sell, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Nuevo producto", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Completa los detalles", style = MaterialTheme.typography.bodySmall, color = Stone400)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Photo upload area
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(Stone50, Emerald50)))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, tint = Emerald500, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Toca para añadir fotos", style = MaterialTheme.typography.bodySmall, color = Stone500)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ReLifeTextField(value = title, onValueChange = { title = it }, label = "Título del producto", leadingIcon = Icons.Default.Edit)
                Spacer(modifier = Modifier.height(12.dp))
                ReLifeTextField(value = description, onValueChange = { description = it }, label = "Descripción", singleLine = false, maxLines = 3)
                Spacer(modifier = Modifier.height(12.dp))
                ReLifeTextField(
                    value         = price,
                    onValueChange = { price = it },
                    label         = "Precio (€)",
                    leadingIcon   = Icons.Default.Euro,
                    keyboardType  = KeyboardType.Decimal
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category selector
                Text("Categoría", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Stone600)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val cats = listOf(PostCategory.FURNITURE, PostCategory.LIGHTING, PostCategory.DECORATION, PostCategory.FASHION, PostCategory.GARDEN, PostCategory.TECH)
                    items(cats) { cat ->
                        val sel = selectedCategory == cat
                        Surface(
                            modifier  = Modifier.clickable { selectedCategory = cat },
                            shape     = RoundedCornerShape(10.dp),
                            color     = if (sel) Emerald500 else Stone100
                        ) {
                            Text(cat.displayName, style = MaterialTheme.typography.labelSmall, color = if (sel) Color.White else Stone600, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Condition selector
                Text("Condición", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Stone600)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ProductCondition.entries.forEach { cond ->
                        val sel = selectedCondition == cond
                        Surface(
                            modifier  = Modifier.clickable { selectedCondition = cond },
                            shape     = RoundedCornerShape(10.dp),
                            color     = if (sel) Emerald500 else Stone100
                        ) {
                            Text(cond.displayName, style = MaterialTheme.typography.labelSmall, color = if (sel) Color.White else Stone600, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Shipping toggle
                Row(
                    modifier              = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Stone50).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalShipping, null, tint = Emerald500, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Envío disponible", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                    ReLifeSwitch(checked = shippingAvailable, onCheckedChange = { shippingAvailable = it })
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick  = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancelar", color = Stone600)
                    }
                    PrimaryButton(text = "Publicar", onClick = onConfirm, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}