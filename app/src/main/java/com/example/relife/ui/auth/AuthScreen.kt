package com.relife.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relife.ui.components.*
import com.relife.R
import com.relife.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    var isLogin      by remember { mutableStateOf(true) }
    var email        by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }
    var name         by remember { mutableStateOf("") }
    var username     by remember { mutableStateOf("") }
    var isLoading    by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Floating circle animation
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val circleOffset by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label         = "circle"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Gradient background (top half) ────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
                .background(
                    Brush.linearGradient(listOf(Emerald600, Teal500, Cyan400))
                )
        )

        // Decorative animated circles
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-40).dp, y = (-30 + circleOffset * 20).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (40 - circleOffset * 15).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )

        // ── Scrollable content ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo section
            Spacer(modifier = Modifier.height(56.dp))
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.relifer),
                    contentDescription = "ReLife Logo",
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Card form ─────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape  = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Tab selector
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Stone100)
                            .padding(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            TabPill(
                                text       = "Iniciar Sesión",
                                selected   = isLogin,
                                onClick    = { isLogin = true },
                                modifier   = Modifier.weight(1f)
                            )
                            TabPill(
                                text       = "Registrarse",
                                selected   = !isLogin,
                                onClick    = { isLogin = false },
                                modifier   = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Animated form fields
                    AnimatedContent(
                        targetState = isLogin,
                        transitionSpec = {
                            (fadeIn(tween(220)) + slideInHorizontally(tween(220)) { if (targetState) -40 else 40 })
                                .togetherWith(fadeOut(tween(180)) + slideOutHorizontally(tween(180)) { if (targetState) 40 else -40 })
                        },
                        label = "formContent"
                    ) { login ->
                        Column(
                            modifier            = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (!login) {
                                ReLifeTextField(
                                    value         = name,
                                    onValueChange = { name = it },
                                    label         = "Nombre completo",
                                    leadingIcon   = Icons.Default.Person,
                                    modifier      = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                ReLifeTextField(
                                    value         = username,
                                    onValueChange = { username = it },
                                    label         = "Nombre de usuario",
                                    leadingIcon   = Icons.Default.AlternateEmail,
                                    modifier      = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            ReLifeTextField(
                                value         = email,
                                onValueChange = { email = it; errorMessage = null },
                                label         = "Correo electrónico",
                                leadingIcon   = Icons.Default.Email,
                                keyboardType  = KeyboardType.Email,
                                modifier      = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            PasswordTextField(
                                value         = password,
                                onValueChange = { password = it; errorMessage = null },
                                modifier      = Modifier.fillMaxWidth(),
                                imeAction     = ImeAction.Done
                            )

                            if (login) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        text    = "¿Olvidaste tu contraseña?",
                                        onClick = { }
                                    )
                                }
                            }

                            // Error message
                            AnimatedVisibility(visible = errorMessage != null) {
                                errorMessage?.let {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Error.copy(alpha = 0.1f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier          = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.ErrorOutline, null, tint = Error, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(it, style = MaterialTheme.typography.bodySmall, color = Error)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(22.dp))

                            // Main CTA button
                            PrimaryButton(
                                text      = if (login) "Iniciar Sesión" else "Crear cuenta",
                                onClick   = {
                                    if (email.isBlank() || password.isBlank()) {
                                        errorMessage = "Por favor completa todos los campos"
                                        return@PrimaryButton
                                    }
                                    isLoading = true
                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(1500)
                                        isLoading    = false
                                        onLoginSuccess()
                                    }
                                },
                                isLoading = isLoading,
                                modifier  = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { onLoginSuccess() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Stone300),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Stone500)
                            ) {
                                Icon(Icons.Default.PersonOutline, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Continuar como invitado", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Divider
                            Row(
                                modifier          = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(modifier = Modifier.weight(1f), color = Stone200)
                                Text(
                                    "  o continuar con  ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Stone400
                                )
                                HorizontalDivider(modifier = Modifier.weight(1f), color = Stone200)
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Social buttons
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                SocialButton(label = "G", gradientColors = listOf(Color(0xFFEA4335), Color(0xFFFF7043)), onClick = { })
                                Spacer(modifier = Modifier.width(16.dp))
                                SocialButton(label = "f", gradientColors = listOf(Color(0xFF1877F2), Color(0xFF42A5F5)), onClick = { })
                                Spacer(modifier = Modifier.width(16.dp))
                                SocialButton(label = "in", gradientColors = listOf(Color(0xFF0077B5), Color(0xFF00A0DC)), onClick = { })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Switch mode
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text(
                            text  = if (isLogin) "¿No tienes cuenta? " else "¿Ya tienes cuenta? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Stone500
                        )
                        Text(
                            text       = if (isLogin) "Regístrate" else "Inicia sesión",
                            style      = MaterialTheme.typography.bodyMedium,
                            color      = Emerald600,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.clickable { isLogin = !isLogin }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Terms text
            Text(
                text      = "Al continuar, aceptas nuestros Términos de servicio\ny Política de privacidad",
                style     = MaterialTheme.typography.bodySmall,
                color     = Stone400,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─── Tab pill inside card ──────────────────────────────────────────────────────
@Composable
private fun TabPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue   = if (selected) Color.White else Color.Transparent,
        animationSpec = tween(200),
        label         = "tabBg"
    )
    val textColor by animateColorAsState(
        targetValue   = if (selected) Emerald600 else Stone400,
        animationSpec = tween(200),
        label         = "tabText"
    )
    Box(
        modifier         = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            style      = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color      = textColor
        )
    }
}

// ─── Social Button ─────────────────────────────────────────────────────────────
@Composable
private fun SocialButton(
    label: String,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(gradientColors))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            color      = Color.White,
            fontWeight = FontWeight.Black,
            fontSize   = 16.sp
        )
    }
}