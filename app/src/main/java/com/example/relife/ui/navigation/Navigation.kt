package com.relife.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.relife.ui.auth.AuthScreen
import com.relife.ui.components.GuestBlockedScreen
import com.relife.ui.components.GuestLoginRequiredDialog
import com.relife.ui.explore.ExploreScreen
import com.relife.ui.home.HomeScreen
import com.relife.ui.marketplace.MarketplaceScreen
import com.relife.ui.messages.MessagesScreen
import com.relife.ui.notifications.NotificationsScreen
import com.relife.ui.profile.ProfileScreen
import com.relife.ui.settings.SettingsScreen
import com.relife.ui.stats.StatsScreen
import com.relife.ui.theme.*

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Auth        : Screen("auth",           "Auth",           Icons.Filled.Login,         Icons.Outlined.Login)
    object Home        : Screen("home",           "Inicio",         Icons.Filled.Home,           Icons.Outlined.Home)
    object Explore     : Screen("explore",        "Explorar",       Icons.Filled.Explore,        Icons.Outlined.Explore)
    object Create      : Screen("create",         "Crear",          Icons.Filled.Add,            Icons.Outlined.Add)
    object Marketplace : Screen("marketplace",    "Mercado",        Icons.Filled.ShoppingBag,    Icons.Outlined.ShoppingBag)
    object Profile     : Screen("profile",        "Perfil",         Icons.Filled.Person,         Icons.Outlined.Person)
    object Notifications : Screen("notifications","Notificaciones", Icons.Filled.Notifications,  Icons.Outlined.Notifications)
    object Messages    : Screen("messages",       "Mensajes",       Icons.Filled.Chat,           Icons.Outlined.Chat)
    object Stats       : Screen("stats",          "Estadísticas",   Icons.Filled.Analytics,      Icons.Outlined.Analytics)
    object Settings    : Screen("settings",       "Ajustes",        Icons.Filled.Settings,       Icons.Outlined.Settings)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Explore,
    Screen.Create,
    Screen.Marketplace,
    Screen.Profile
)

@Composable
fun ReLifeNavigation() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var isGuest    by remember { mutableStateOf(false) }

    if (!isLoggedIn && !isGuest) {
        AuthScreen(
            onLoginSuccess = {
                isLoggedIn = true
                isGuest    = false
            },
            onGuestLogin = {
                isGuest    = true
                isLoggedIn = false
            }
        )
    } else {
        val navController = rememberNavController()
        MainScaffold(
            navController = navController,
            isGuest       = isGuest,
            onLogout      = {
                isLoggedIn = false
                isGuest    = false
            },
            onRequestLogin = {
                // Guest wants to log in — go back to auth
                isLoggedIn = false
                isGuest    = false
            }
        )
    }
}

@Composable
fun MainScaffold(
    navController: NavHostController,
    isGuest: Boolean,
    onLogout: () -> Unit,
    onRequestLogin: () -> Unit
) {
    val navBackStackEntry  by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    // Dialog state for when guest tries to use Create (FAB)
    var showGuestCreateDialog by remember { mutableStateOf(false) }

    GuestLoginRequiredDialog(
        show         = showGuestCreateDialog,
        onDismiss    = { showGuestCreateDialog = false },
        onLoginClick = {
            showGuestCreateDialog = false
            onRequestLogin()
        },
        title   = "Crea tu primera publicación",
        message = "Inicia sesión o regístrate para compartir tus creaciones con la comunidad ReLife.",
        icon    = Icons.Default.AddCircle
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                ReLifeBottomBar(
                    navController      = navController,
                    currentDestination = currentDestination,
                    isGuest            = isGuest,
                    onGuestCreateClick = { showGuestCreateDialog = true }
                )
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(paddingValues),
            enterTransition = {
                fadeIn(tween(300)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300))
            },
            exitTransition = {
                fadeOut(tween(300)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300))
            },
            popEnterTransition = {
                fadeIn(tween(300)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300))
            },
            popExitTransition = {
                fadeOut(tween(300)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToMessages      = { navController.navigate(Screen.Messages.route) },
                    isGuest                   = isGuest,
                    onRequestLogin            = onRequestLogin
                )
            }
            composable(Screen.Explore.route) {
                ExploreScreen(
                    isGuest        = isGuest,
                    onRequestLogin = onRequestLogin
                )
            }
            composable(Screen.Create.route) {
                if (isGuest) {
                    GuestBlockedScreen(
                        icon        = Icons.Default.AddPhotoAlternate,
                        title       = "Comparte tus creaciones",
                        description = "Inicia sesión para publicar tus proyectos de upcycling y conectar con la comunidad.",
                        onLoginClick = onRequestLogin,
                        onBack       = { navController.popBackStack() }
                    )
                } else {
                    CreatePostPlaceholder(onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Marketplace.route) {
                MarketplaceScreen(
                    isGuest        = isGuest,
                    onRequestLogin = onRequestLogin
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToStats    = { navController.navigate(Screen.Stats.route) },
                    isGuest              = isGuest,
                    onRequestLogin       = onRequestLogin
                )
            }
            composable(Screen.Notifications.route) {
                if (isGuest) {
                    GuestBlockedScreen(
                        icon        = Icons.Default.Notifications,
                        title       = "Tus notificaciones",
                        description = "Inicia sesión para recibir notificaciones sobre likes, comentarios, ventas y más.",
                        onLoginClick = onRequestLogin,
                        onBack       = { navController.popBackStack() }
                    )
                } else {
                    NotificationsScreen(onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Messages.route) {
                if (isGuest) {
                    GuestBlockedScreen(
                        icon        = Icons.Default.Chat,
                        title       = "Mensajes privados",
                        description = "Inicia sesión para chatear con otros creadores, negociar y compartir ideas.",
                        onLoginClick = onRequestLogin,
                        onBack       = { navController.popBackStack() }
                    )
                } else {
                    MessagesScreen(onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Stats.route) {
                if (isGuest) {
                    GuestBlockedScreen(
                        icon        = Icons.Default.Analytics,
                        title       = "Tus estadísticas",
                        description = "Inicia sesión para ver las métricas de tus publicaciones, seguidores y ventas.",
                        onLoginClick = onRequestLogin,
                        onBack       = { navController.popBackStack() }
                    )
                } else {
                    StatsScreen(onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack         = { navController.popBackStack() },
                    onLogout       = onLogout,
                    isGuest        = isGuest,
                    onRequestLogin = onRequestLogin
                )
            }
        }
    }
}

@Composable
fun ReLifeBottomBar(
    navController: NavHostController,
    currentDestination: androidx.navigation.NavDestination?,
    isGuest: Boolean = false,
    onGuestCreateClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            if (screen == Screen.Create) {
                NavigationBarItem(
                    selected = false,
                    onClick  = {
                        if (isGuest) {
                            onGuestCreateClick()
                        } else {
                            navController.navigate(screen.route)
                        }
                    },
                    icon     = {
                        FloatingActionButton(
                            onClick        = {
                                if (isGuest) {
                                    onGuestCreateClick()
                                } else {
                                    navController.navigate(screen.route)
                                }
                            },
                            modifier       = Modifier.size(48.dp),
                            containerColor = Emerald500,
                            contentColor   = Color.White,
                            elevation      = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Crear")
                        }
                    },
                    label  = { },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
            } else {
                NavigationBarItem(
                    selected = selected,
                    onClick  = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    icon   = {
                        Icon(
                            imageVector        = if (selected) screen.selectedIcon else screen.unselectedIcon,
                            contentDescription = screen.title
                        )
                    },
                    label  = { Text(screen.title, style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = Emerald600,
                        selectedTextColor   = Emerald600,
                        unselectedIconColor = Stone400,
                        unselectedTextColor = Stone400,
                        indicatorColor      = Emerald100
                    )
                )
            }
        }
    }
}

@Composable
fun CreatePostPlaceholder(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AddPhotoAlternate, null, tint = Emerald500, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Crear nuevo post", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = onBack) { Text("Volver") }
        }
    }
}