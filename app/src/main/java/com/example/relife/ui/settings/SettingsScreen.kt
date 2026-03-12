package com.relife.ui.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relife.data.repository.MockData
import com.relife.ui.components.*
import com.relife.ui.theme.*

private enum class SettingsSection { PROFILE, SECURITY, PRO, APPEARANCE }
private enum class ThemeOption(val label: String, val icon: ImageVector) {
    LIGHT("Claro", Icons.Default.LightMode),
    DARK("Oscuro", Icons.Default.DarkMode),
    SYSTEM("Sistema", Icons.Default.SettingsBrightness)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    isGuest: Boolean = false,
    onRequestLogin: () -> Unit = {}
) {
    val user = remember { MockData.currentUser }
    var activeSection    by remember { mutableStateOf(SettingsSection.PROFILE) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var savedSuccess     by remember { mutableStateOf(false) }

    // ── Profile form ─────────────────────────────────────────────────────────
    var name     by remember { mutableStateOf(user.name) }
    var username by remember { mutableStateOf(user.username) }
    var bio      by remember { mutableStateOf(user.bio ?: "") }
    var email    by remember { mutableStateOf(user.email) }
    var website  by remember { mutableStateOf(user.website ?: "") }

    // ── Validation ────────────────────────────────────────────────────────────
    val nameError     = name.isBlank()
    val usernameError = username.isBlank() || username.contains(" ")
    val emailError    = email.isBlank() || !email.contains("@")
    val bioMaxChars   = 160

    // ── Toggles ───────────────────────────────────────────────────────────────
    var emailNotifs   by remember { mutableStateOf(true) }
    var pushNotifs    by remember { mutableStateOf(true) }
    var publicProfile by remember { mutableStateOf(true) }
    var onlineStatus  by remember { mutableStateOf(true) }

    // ── Appearance ────────────────────────────────────────────────────────────
    var selectedTheme by remember { mutableStateOf(ThemeOption.SYSTEM) }

    // ── Social links ─────────────────────────────────────────────────────────
    var instagram  by remember { mutableStateOf("") }
    var twitter    by remember { mutableStateOf("") }
    var tiktok     by remember { mutableStateOf("") }
    var youtube    by remember { mutableStateOf("") }

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

        // ── Guest Settings ──────────────────────────────────────────────────
        if (isGuest) {
            LazyColumn(
                modifier            = Modifier.padding(padding).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Login CTA card
                item {
                    Card(
                        modifier  = Modifier.fillMaxWidth().padding(16.dp),
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
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(Emerald100, Teal100))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, null, tint = Emerald600, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Modo invitado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Inicia sesión para acceder a todas las opciones de configuración.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Stone500,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PrimaryButton(
                                text    = "Iniciar sesión / Registrarse",
                                onClick = onRequestLogin,
                                modifier = Modifier.fillMaxWidth(),
                                icon    = Icons.Default.Login
                            )
                        }
                    }
                }
                // Appearance section for guest
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Apariencia", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Emerald600)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ThemeOption.entries.forEach { theme ->
                                    val sel = selectedTheme == theme
                                    Surface(
                                        modifier = Modifier.weight(1f).clickable { selectedTheme = theme },
                                        shape    = RoundedCornerShape(12.dp),
                                        color    = if (sel) Emerald50 else Stone100,
                                        border   = if (sel) androidx.compose.foundation.BorderStroke(2.dp, Emerald500) else null
                                    ) {
                                        Column(
                                            modifier            = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(theme.icon, null, tint = if (sel) Emerald600 else Stone400, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(theme.label, style = MaterialTheme.typography.labelSmall, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal, color = if (sel) Emerald700 else Stone600)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Logout / exit guest
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier          = Modifier
                                .fillMaxWidth()
                                .clickable { onLogout() }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ExitToApp, null, tint = Error, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Salir del modo invitado", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Error)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
            return@Scaffold
        }

        // ── Regular (logged-in) Settings ────────────────────────────────────
        LazyColumn(
            modifier            = Modifier.padding(padding).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── Profile header card ──────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Emerald600, Teal500, Cyan400)))
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            UserAvatar(
                                imageUrl        = user.avatarUrl,
                                size            = 72.dp,
                                showStoryBorder = false
                            )
                            Box(
                                modifier         = Modifier
                                    .size(26.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable { },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, null, tint = Emerald600, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White)
                            Text("@${user.username}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.8f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(0.2f)) {
                                Text(
                                    if (user.isVerified) "✨ Verificado" else "Free",
                                    style    = MaterialTheme.typography.labelSmall,
                                    color    = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── Section nav pills ────────────────────────────────────────────
            item {
                Surface(color = Color.White, shadowElevation = 2.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            SettingsSection.PROFILE    to (Icons.Outlined.Person      to "Perfil"),
                            SettingsSection.SECURITY   to (Icons.Outlined.Security    to "Seguridad"),
                            SettingsSection.PRO        to (Icons.Outlined.WorkspacePremium to "Pro"),
                            SettingsSection.APPEARANCE to (Icons.Outlined.Palette     to "Apariencia")
                        ).forEach { (section, pair) ->
                            val (icon, label) = pair
                            val sel = activeSection == section
                            val bg by animateColorAsState(if (sel) Emerald500 else Stone100, tween(200), label = "sec$label")
                            Surface(
                                modifier  = Modifier.weight(1f).clickable { activeSection = section },
                                shape     = RoundedCornerShape(12.dp),
                                color     = bg
                            ) {
                                Column(
                                    modifier              = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment   = Alignment.CenterHorizontally
                                ) {
                                    Icon(icon, null, tint = if (sel) Color.White else Stone500, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(label, style = MaterialTheme.typography.labelSmall, color = if (sel) Color.White else Stone500, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                }
            }

            // ── Section content ──────────────────────────────────────────────
            item {
                AnimatedContent(
                    targetState = activeSection,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(220)) },
                    label = "sectionContent"
                ) { section ->
                    Column(
                        modifier            = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        when (section) {

                            // ── PROFILE ─────────────────────────────────────
                            SettingsSection.PROFILE -> {
                                // Basic info card
                                SettingsCard(title = "Información básica", icon = Icons.Outlined.Person) {
                                    ValidatedField(
                                        value         = name,
                                        onValueChange = { name = it },
                                        label         = "Nombre completo",
                                        leadingIcon   = Icons.Outlined.Badge,
                                        isError       = nameError,
                                        errorMessage  = "El nombre no puede estar vacío"
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    ValidatedField(
                                        value         = username,
                                        onValueChange = { username = it.trim() },
                                        label         = "Usuario (@)",
                                        leadingIcon   = Icons.Outlined.AlternateEmail,
                                        isError       = usernameError,
                                        errorMessage  = if (username.isBlank()) "Requerido" else "Sin espacios"
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    ValidatedField(
                                        value         = email,
                                        onValueChange = { email = it },
                                        label         = "Email",
                                        leadingIcon   = Icons.Outlined.Email,
                                        keyboardType  = KeyboardType.Email,
                                        isError       = emailError,
                                        errorMessage  = "Email inválido"
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    // Bio with char counter
                                    Column {
                                        ReLifeTextField(
                                            value         = bio,
                                            onValueChange = { if (it.length <= bioMaxChars) bio = it },
                                            label         = "Biografía",
                                            singleLine    = false,
                                            maxLines      = 3
                                        )
                                        Row(
                                            modifier              = Modifier.fillMaxWidth().padding(top = 4.dp, end = 4.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                "${bio.length}/$bioMaxChars",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (bio.length > bioMaxChars * 0.9) Rose500 else Stone400
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    ReLifeTextField(
                                        value         = website,
                                        onValueChange = { website = it },
                                        label         = "Sitio web",
                                        leadingIcon   = Icons.Outlined.Language
                                    )
                                }

                                // Social links card
                                SettingsCard(title = "Redes sociales", icon = Icons.Default.Share) {
                                    SocialField(value = instagram, onValueChange = { instagram = it }, platform = "Instagram", placeholder = "@tuusuario", color = Color(0xFFE1306C))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    SocialField(value = twitter, onValueChange = { twitter = it }, platform = "X / Twitter", placeholder = "@tuusuario", color = Color(0xFF1DA1F2))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    SocialField(value = tiktok, onValueChange = { tiktok = it }, platform = "TikTok", placeholder = "@tuusuario", color = Color(0xFF000000))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    SocialField(value = youtube, onValueChange = { youtube = it }, platform = "YouTube", placeholder = "tu canal", color = Color(0xFFFF0000))
                                }

                                // Notifications card
                                SettingsCard(title = "Notificaciones", icon = Icons.Outlined.Notifications) {
                                    SettingsToggleRow(
                                        icon     = Icons.Outlined.Email,
                                        title    = "Email",
                                        subtitle = "Actividad y novedades por email",
                                        checked  = emailNotifs,
                                        onChange = { emailNotifs = it }
                                    )
                                    HorizontalDivider(color = Stone100, modifier = Modifier.padding(vertical = 10.dp))
                                    SettingsToggleRow(
                                        icon     = Icons.Outlined.NotificationsActive,
                                        title    = "Push",
                                        subtitle = "Notificaciones en el dispositivo",
                                        checked  = pushNotifs,
                                        onChange = { pushNotifs = it }
                                    )
                                }

                                // Privacy card
                                SettingsCard(title = "Privacidad", icon = Icons.Outlined.Lock) {
                                    SettingsToggleRow(
                                        icon     = Icons.Outlined.Public,
                                        title    = "Perfil público",
                                        subtitle = "Cualquiera puede ver tu perfil",
                                        checked  = publicProfile,
                                        onChange = { publicProfile = it }
                                    )
                                    HorizontalDivider(color = Stone100, modifier = Modifier.padding(vertical = 10.dp))
                                    SettingsToggleRow(
                                        icon     = Icons.Outlined.Circle,
                                        title    = "Estado en línea",
                                        subtitle = "Mostrar cuando estás activo",
                                        checked  = onlineStatus,
                                        onChange = { onlineStatus = it }
                                    )
                                }

                                // Save button
                                val canSave = !nameError && !usernameError && !emailError
                                AnimatedVisibility(visible = savedSuccess) {
                                    Surface(shape = RoundedCornerShape(12.dp), color = Emerald50) {
                                        Row(
                                            modifier          = Modifier.fillMaxWidth().padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, null, tint = Emerald600, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("¡Cambios guardados correctamente!", style = MaterialTheme.typography.bodySmall, color = Emerald700)
                                        }
                                    }
                                }
                                PrimaryButton(
                                    text     = "Guardar cambios",
                                    onClick  = {
                                        if (canSave) { savedSuccess = true }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled  = canSave
                                )
                            }

                            // ── SECURITY ────────────────────────────────────
                            SettingsSection.SECURITY -> SecuritySection()

                            // ── PRO ─────────────────────────────────────────
                            SettingsSection.PRO -> ProSection()

                            // ── APPEARANCE ──────────────────────────────────
                            SettingsSection.APPEARANCE -> {
                                SettingsCard(title = "Tema de la app", icon = Icons.Outlined.Palette) {
                                    ThemeOption.entries.forEach { opt ->
                                        val sel = selectedTheme == opt
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (sel) Emerald50 else Color.Transparent)
                                                .clickable { selectedTheme = opt }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier         = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                                                    .background(if (sel) Emerald500 else Stone100),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(opt.icon, null, tint = if (sel) Color.White else Stone500, modifier = Modifier.size(20.dp))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(opt.label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal, modifier = Modifier.weight(1f))
                                            if (sel) Icon(Icons.Default.CheckCircle, null, tint = Emerald500, modifier = Modifier.size(20.dp))
                                        }
                                        if (opt != ThemeOption.SYSTEM) HorizontalDivider(color = Stone100, modifier = Modifier.padding(start = 64.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Logout button ────────────────────────────────────────────────
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                    OutlinedButton(
                        onClick  = { showLogoutDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = Rose500),
                        border   = androidx.compose.foundation.BorderStroke(1.dp, Rose500)
                    ) {
                        Icon(Icons.Outlined.Logout, null, tint = Rose500, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar sesión", color = Rose500, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        ConfirmationDialog(
            title         = "Cerrar sesión",
            message       = "¿Estás seguro de que quieres cerrar sesión?",
            confirmText   = "Cerrar sesión",
            onConfirm     = { showLogoutDialog = false; onLogout() },
            onDismiss     = { showLogoutDialog = false },
            isDestructive = true
        )
    }
}

// ─── Security Section ─────────────────────────────────────────────────────────
@Composable
private fun SecuritySection() {
    var currentPassword  by remember { mutableStateOf("") }
    var newPassword      by remember { mutableStateOf("") }
    var confirmPassword  by remember { mutableStateOf("") }
    var showCurrent      by remember { mutableStateOf(false) }
    var showNew          by remember { mutableStateOf(false) }
    var showConfirm      by remember { mutableStateOf(false) }
    var twoFAEnabled     by remember { mutableStateOf(false) }
    var sessionsExpanded by remember { mutableStateOf(false) }

    val passwordMismatch = newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword
    val passwordWeak     = newPassword.isNotEmpty() && newPassword.length < 8
    val passwordStrength = when {
        newPassword.length >= 12 && newPassword.any { it.isDigit() } && newPassword.any { !it.isLetterOrDigit() } -> 3
        newPassword.length >= 8  -> 2
        newPassword.isNotEmpty() -> 1
        else                     -> 0
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SettingsCard(title = "Cambiar contraseña", icon = Icons.Outlined.Lock) {
            PasswordInputField(value = currentPassword, onValueChange = { currentPassword = it }, label = "Contraseña actual", show = showCurrent, onToggle = { showCurrent = !showCurrent })
            Spacer(modifier = Modifier.height(12.dp))
            PasswordInputField(value = newPassword, onValueChange = { newPassword = it }, label = "Nueva contraseña", show = showNew, onToggle = { showNew = !showNew })
            if (newPassword.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                PasswordStrengthBar(strength = passwordStrength)
                if (passwordWeak) {
                    Text("Mínimo 8 caracteres", style = MaterialTheme.typography.labelSmall, color = Amber500)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            PasswordInputField(
                value         = confirmPassword,
                onValueChange = { confirmPassword = it },
                label         = "Confirmar contraseña",
                show          = showConfirm,
                onToggle      = { showConfirm = !showConfirm },
                isError       = passwordMismatch,
                errorMessage  = "Las contraseñas no coinciden"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                text     = "Actualizar contraseña",
                onClick  = { },
                modifier = Modifier.fillMaxWidth(),
                enabled  = currentPassword.isNotEmpty() && newPassword.length >= 8 && !passwordMismatch
            )
        }

        SettingsCard(title = "Autenticación en dos pasos", icon = Icons.Outlined.Security) {
            SettingsToggleRow(
                icon     = Icons.Default.PhonelinkLock,
                title    = "Verificación 2FA",
                subtitle = if (twoFAEnabled) "Activa — tu cuenta está protegida" else "Añade una capa extra de seguridad",
                checked  = twoFAEnabled,
                onChange = { twoFAEnabled = it }
            )
            AnimatedVisibility(visible = twoFAEnabled) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Stone100)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = Emerald50) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Emerald600, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("2FA activado. Usa una app como Google Authenticator.", style = MaterialTheme.typography.bodySmall, color = Emerald700)
                        }
                    }
                }
            }
        }

        SettingsCard(title = "Sesiones activas", icon = Icons.Outlined.Devices) {
            listOf("Android · Madrid" to "Activa ahora", "Chrome · Windows" to "Hace 2 días", "Safari · iPhone" to "Hace 1 semana")
                .take(if (sessionsExpanded) 3 else 1)
                .forEachIndexed { i, (device, time) ->
                    if (i > 0) HorizontalDivider(color = Stone100, modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier         = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Stone100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhoneAndroid, null, tint = Stone500, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(device, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(time, style = MaterialTheme.typography.labelSmall, color = Stone400)
                        }
                        if (i == 0) {
                            Surface(shape = RoundedCornerShape(6.dp), color = Emerald50) {
                                Text("Esta", style = MaterialTheme.typography.labelSmall, color = Emerald700, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        } else {
                            TextButton(onClick = { }, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
                                Text("Cerrar", style = MaterialTheme.typography.labelSmall, color = Rose500)
                            }
                        }
                    }
                }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { sessionsExpanded = !sessionsExpanded }, modifier = Modifier.fillMaxWidth()) {
                Text(if (sessionsExpanded) "Ver menos" else "Ver todas las sesiones", color = Emerald600, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Pro Section ─────────────────────────────────────────────────────────────
@Composable
private fun ProSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Current plan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Emerald600, Teal500)))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Plan actual: Free", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Actualiza a Pro para desbloquear todo el potencial de ReLife.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.85f))
            }
        }

        // Plans
        listOf(
            Triple("Básico", "4,99 €/mes", listOf("Sin anuncios", "Hasta 50 publicaciones/mes", "Estadísticas básicas", "Badge verificado")),
            Triple("Pro", "9,99 €/mes", listOf("Todo lo de Básico", "Publicaciones ilimitadas", "Estadísticas avanzadas", "Acceso prioritario a soporte", "Venta en Marketplace sin comisión")),
            Triple("Pro Anual", "79,99 €/año", listOf("Todo lo de Pro", "2 meses gratis", "Acceso beta a nuevas funciones", "Insignia exclusiva de fundador"))
        ).forEachIndexed { i, (plan, price, features) ->
            val isPro = i == 1
            val brush = if (isPro) Brush.linearGradient(listOf(Emerald500, Teal500)) else null

            Card(
                shape    = RoundedCornerShape(20.dp),
                colors   = CardDefaults.cardColors(containerColor = if (isPro) Color.Transparent else Color.White),
                modifier = Modifier.fillMaxWidth().let { if (brush != null) it.background(brush, RoundedCornerShape(20.dp)) else it }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            if (isPro) {
                                Surface(shape = RoundedCornerShape(6.dp), color = Color.White.copy(0.25f)) {
                                    Text("⭐ Más popular", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(plan, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = if (isPro) Color.White else Stone800)
                        }
                        Text(price, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (isPro) Color.White else Emerald600)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    features.forEach { feat ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                            Icon(Icons.Default.CheckCircle, null, tint = if (isPro) Color.White.copy(0.9f) else Emerald500, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(feat, style = MaterialTheme.typography.bodySmall, color = if (isPro) Color.White.copy(0.9f) else Stone600)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier  = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable { },
                        shape     = RoundedCornerShape(14.dp),
                        color     = if (isPro) Color.White else Emerald500
                    ) {
                        Text(
                            "Suscribirse a $plan",
                            style                 = MaterialTheme.typography.labelLarge,
                            fontWeight            = FontWeight.Bold,
                            color                 = if (isPro) Emerald600 else Color.White,
                            modifier              = Modifier.padding(vertical = 12.dp).wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        // Features comparison hint
        Surface(shape = RoundedCornerShape(16.dp), color = Stone50, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Stone400, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Cancela cuando quieras. Sin permanencia.", style = MaterialTheme.typography.bodySmall, color = Stone500)
            }
        }
    }
}

// ─── Reusable composables ─────────────────────────────────────────────────────
@Composable
private fun SettingsCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = CardShape, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 14.dp)) {
                Box(
                    modifier         = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(Emerald50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = Emerald600, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            content()
        }
    }
}

@Composable
private fun ValidatedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column {
        ReLifeTextField(
            value         = value,
            onValueChange = onValueChange,
            label         = label,
            leadingIcon   = leadingIcon,
            keyboardType  = keyboardType,
            isError       = isError
        )
        AnimatedVisibility(visible = isError) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.padding(start = 4.dp, top = 3.dp)
            ) {
                Icon(Icons.Default.Error, null, tint = Rose500, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(errorMessage, style = MaterialTheme.typography.labelSmall, color = Rose500)
            }
        }
    }
}

@Composable
private fun SocialField(
    value: String,
    onValueChange: (String) -> Unit,
    platform: String,
    placeholder: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier         = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(platform.first().toString(), fontWeight = FontWeight.Black, color = color, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        ReLifeTextField(
            value         = value,
            onValueChange = onValueChange,
            label         = platform,
            modifier      = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Stone400, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Stone400)
        }
        ReLifeSwitch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    show: Boolean,
    onToggle: () -> Unit,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column {
        PasswordTextField(
            value         = value,
            onValueChange = onValueChange,
            label         = label,
            isError       = isError,
            errorMessage  = if (isError) errorMessage else null
        )
    }
}

@Composable
private fun PasswordStrengthBar(strength: Int) {
    val labels  = listOf("Débil", "Aceptable", "Fuerte")
    val colors  = listOf(Rose500, Amber500, Emerald500)
    val label   = if (strength > 0) labels[strength - 1] else ""
    val barColor = if (strength > 0) colors[strength - 1] else Stone200
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..3).forEach { level ->
                Box(
                    modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp))
                        .background(if (level <= strength) barColor else Stone200)
                )
            }
        }
        if (label.isNotEmpty()) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = barColor, modifier = Modifier.padding(top = 3.dp))
        }
    }
}