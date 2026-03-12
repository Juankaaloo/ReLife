package com.relife.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.relife.ui.theme.*

/**
 * Dialog that appears when a guest user tries to perform a restricted action.
 * Offers options to log in, register, or dismiss.
 */
@Composable
fun GuestLoginRequiredDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    title: String = "Inicia sesión para continuar",
    message: String = "Crea una cuenta o inicia sesión para acceder a todas las funciones de ReLife.",
    icon: ImageVector = Icons.Default.Lock
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(listOf(Emerald100, Teal100))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Emerald600,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Stone500,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(listOf(Emerald500, Teal500, Cyan500)),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Login,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Iniciar sesión / Registrarse",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Seguir como invitado",
                    color = Stone400,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

/**
 * A full-screen empty state shown when a guest accesses a restricted section.
 * Shows a friendly message and a login CTA.
 */
@Composable
fun GuestBlockedScreen(
    icon: ImageVector,
    title: String,
    description: String,
    onLoginClick: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar with back button if needed
        if (onBack != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Stone700
                    )
                }
            }
        }

        // Centered content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(listOf(Emerald50, Teal50, Cyan50))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Emerald500,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Stone500,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoginClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(listOf(Emerald500, Teal500, Cyan500)),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Crear cuenta gratis",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onLoginClick) {
                Text(
                    "Ya tengo cuenta · Iniciar sesión",
                    color = Emerald600,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}