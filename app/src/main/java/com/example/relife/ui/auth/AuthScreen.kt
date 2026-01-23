package com.relife.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.relife.ui.components.*
import com.relife.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    Row(modifier = Modifier.fillMaxSize()) {
        // Left side - Branding (hidden on small screens)
        Box(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Emerald500, Teal500, Cyan500)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                // Logo
                Icon(
                    imageVector = Icons.Default.Recycling,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "ReLife",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Transforma, Crea, Inspira",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Únete a la comunidad de reciclaje creativo más grande del mundo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Right side - Form
        Box(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AuthTab(
                        text = "Iniciar Sesión",
                        selected = isLogin,
                        onClick = { isLogin = true }
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    AuthTab(
                        text = "Registrarse",
                        selected = !isLogin,
                        onClick = { isLogin = false }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Form
                AnimatedContent(
                    targetState = isLogin,
                    transitionSpec = {
                        fadeIn() + slideInHorizontally() togetherWith fadeOut() + slideOutHorizontally()
                    },
                    label = "authForm"
                ) { login ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!login) {
                            ReLifeTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = "Nombre",
                                leadingIcon = Icons.Default.Person,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            ReLifeTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = "Usuario",
                                leadingIcon = Icons.Default.AlternateEmail,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        ReLifeTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            leadingIcon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        PasswordTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            imeAction = ImeAction.Done
                        )
                        
                        if (login) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    text = "¿Olvidaste tu contraseña?",
                                    onClick = { /* TODO */ }
                                )
                            }
                        }
                        
                        errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                color = Error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        PrimaryButton(
                            text = if (login) "Iniciar Sesión" else "Registrarse",
                            onClick = {
                                isLoading = true
                                // Simulate login
                                kotlinx.coroutines.MainScope().launch {
                                    delay(1500)
                                    isLoading = false
                                    onLoginSuccess()
                                }
                            },
                            isLoading = isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Stone200)
                            Text(
                                text = "  o continuar con  ",
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone400
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Stone200)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Social login buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            SocialButton(icon = Icons.Default.Search, onClick = { }) // Google
                            Spacer(modifier = Modifier.width(16.dp))
                            SocialButton(icon = Icons.Default.Tag, onClick = { }) // Twitter
                            Spacer(modifier = Modifier.width(16.dp))
                            SocialButton(icon = Icons.Default.Code, onClick = { }) // GitHub
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Switch auth mode
                        Row {
                            Text(
                                text = if (login) "¿No tienes cuenta? " else "¿Ya tienes cuenta? ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Stone500
                            )
                            Text(
                                text = if (login) "Regístrate" else "Inicia sesión",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Emerald600,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { isLogin = !isLogin }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Emerald600 else Stone400
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(3.dp)
                .background(
                    color = if (selected) Emerald500 else Color.Transparent,
                    shape = ChipShape
                )
        )
    }
}

@Composable
private fun SocialButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        shape = ButtonShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Stone600
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun kotlinx.coroutines.MainScope() = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
private fun kotlinx.coroutines.CoroutineScope.launch(block: suspend () -> Unit) {
    kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) { block() }
}
