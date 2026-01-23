package com.relife.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user = remember { MockData.currentUser }
    var selectedSection by remember { mutableStateOf("Perfil") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Form states
    var name by remember { mutableStateOf(user.name) }
    var username by remember { mutableStateOf(user.username) }
    var bio by remember { mutableStateOf(user.bio ?: "") }
    var email by remember { mutableStateOf(user.email) }
    var website by remember { mutableStateOf(user.website ?: "") }
    
    // Toggle states
    var emailNotifications by remember { mutableStateOf(true) }
    var pushNotifications by remember { mutableStateOf(true) }
    var publicProfile by remember { mutableStateOf(true) }
    var showOnlineStatus by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", fontWeight = FontWeight.Bold) },
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
        Row(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Sidebar
            Surface(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // User card
                    Card(
                        shape = CardShape,
                        colors = CardDefaults.cardColors(containerColor = Stone50)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                UserAvatar(imageUrl = user.avatarUrl, size = 56.dp)
                                IconButton(
                                    onClick = { /* Change photo */ },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(Emerald500, CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        "Editar foto",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "@${user.username}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Stone500
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Navigation items
                    SettingsNavItem(
                        icon = Icons.Outlined.Person,
                        title = "Editar Perfil",
                        selected = selectedSection == "Perfil",
                        onClick = { selectedSection = "Perfil" }
                    )
                    SettingsNavItem(
                        icon = Icons.Outlined.Security,
                        title = "Seguridad",
                        selected = selectedSection == "Seguridad",
                        onClick = { selectedSection = "Seguridad" }
                    )
                    SettingsNavItem(
                        icon = Icons.Outlined.WorkspacePremium,
                        title = "Suscripción Pro",
                        selected = selectedSection == "Pro",
                        onClick = { selectedSection = "Pro" },
                        badge = "PRO"
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    HorizontalDivider(color = Stone200)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SettingsNavItem(
                        icon = Icons.Outlined.Logout,
                        title = "Cerrar Sesión",
                        selected = false,
                        onClick = { showLogoutDialog = true },
                        tint = Rose500
                    )
                }
            }
            
            // Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedSection) {
                    "Perfil" -> {
                        item {
                            Text(
                                text = "Editar Perfil",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        item {
                            Card(
                                shape = CardShape,
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    ReLifeTextField(
                                        value = name,
                                        onValueChange = { name = it },
                                        label = "Nombre",
                                        leadingIcon = Icons.Outlined.Person
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    ReLifeTextField(
                                        value = username,
                                        onValueChange = { username = it },
                                        label = "Usuario",
                                        leadingIcon = Icons.Outlined.AlternateEmail
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    ReLifeTextField(
                                        value = bio,
                                        onValueChange = { bio = it },
                                        label = "Biografía",
                                        singleLine = false,
                                        maxLines = 3
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    ReLifeTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = "Email",
                                        leadingIcon = Icons.Outlined.Email,
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    ReLifeTextField(
                                        value = website,
                                        onValueChange = { website = it },
                                        label = "Sitio web",
                                        leadingIcon = Icons.Outlined.Language
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    PrimaryButton(
                                        text = "Guardar cambios",
                                        onClick = { /* Save */ },
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                        
                        item {
                            Card(
                                shape = CardShape,
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        text = "Notificaciones",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    SettingsToggle(
                                        title = "Notificaciones por email",
                                        subtitle = "Recibir emails sobre actividad",
                                        checked = emailNotifications,
                                        onCheckedChange = { emailNotifications = it }
                                    )
                                    HorizontalDivider(color = Stone100, modifier = Modifier.padding(vertical = 12.dp))
                                    SettingsToggle(
                                        title = "Notificaciones push",
                                        subtitle = "Notificaciones en tu dispositivo",
                                        checked = pushNotifications,
                                        onCheckedChange = { pushNotifications = it }
                                    )
                                }
                            }
                        }
                        
                        item {
                            Card(
                                shape = CardShape,
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        text = "Privacidad",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    SettingsToggle(
                                        title = "Perfil público",
                                        subtitle = "Cualquiera puede ver tu perfil",
                                        checked = publicProfile,
                                        onCheckedChange = { publicProfile = it }
                                    )
                                    HorizontalDivider(color = Stone100, modifier = Modifier.padding(vertical = 12.dp))
                                    SettingsToggle(
                                        title = "Mostrar estado en línea",
                                        subtitle = "Otros pueden ver cuando estás activo",
                                        checked = showOnlineStatus,
                                        onCheckedChange = { showOnlineStatus = it }
                                    )
                                }
                            }
                        }
                    }
                    
                    "Seguridad" -> {
                        item {
                            Text(
                                text = "Seguridad",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        item {
                            Card(
                                shape = CardShape,
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text("Próximamente: Cambio de contraseña, 2FA, etc.")
                                }
                            }
                        }
                    }
                    
                    "Pro" -> {
                        item {
                            Text(
                                text = "Suscripción Pro",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        item {
                            Card(
                                shape = CardShape,
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text("Próximamente: Planes de suscripción Pro")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showLogoutDialog) {
        ConfirmationDialog(
            title = "Cerrar sesión",
            message = "¿Estás seguro de que quieres cerrar sesión?",
            confirmText = "Cerrar sesión",
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false },
            isDestructive = true
        )
    }
}

@Composable
private fun SettingsNavItem(
    icon: ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    tint: Color = if (selected) Emerald600 else Stone600,
    badge: String? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(ButtonShape)
            .clickable(onClick = onClick),
        color = if (selected) Emerald50 else Color.Transparent,
        shape = ButtonShape
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = tint,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            badge?.let {
                Surface(
                    shape = ChipShape,
                    color = Amber500
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Stone500)
        }
        ReLifeSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
