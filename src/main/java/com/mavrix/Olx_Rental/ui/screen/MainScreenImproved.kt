package com.mavrix.Olx_Rental.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mavrix.Olx_Rental.ui.theme.*
import com.mavrix.Olx_Rental.ui.viewmodel.AuthViewModel
import com.mavrix.Olx_Rental.ui.viewmodel.ListingViewModel

@Composable
fun MainScreenImproved(
    authViewModel: AuthViewModel,
    listingViewModel: ListingViewModel,
    onNavigateToLogin: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Scaffold(
        bottomBar = {
            BottomNavigationBarWithFAB(
                currentRoute = currentRoute ?: "home",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onFABClick = {
                    navController.navigate("category_selection")
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreenFinal(
                    listingViewModel = listingViewModel,
                    onListingClick = { listing ->
                        navController.navigate("listing_detail/${listing.id}")
                    },
                    onSearchClick = { navController.navigate("search") },
                    onNotificationsClick = { navController.navigate("notifications") }
                )
            }
            
            composable("chats") {
                ChatListScreen(
                    authViewModel = authViewModel,
                    onChatClick = { chatId, otherUserId ->
                        navController.navigate("chat/$chatId/$otherUserId")
                    }
                )
            }
            
            composable("favorites") {
                SavedListingsScreen(
                    authViewModel = authViewModel,
                    listingViewModel = listingViewModel,
                    onBackClick = { navController.popBackStack() },
                    onListingClick = { listing ->
                        navController.navigate("listing_detail/${listing.id}")
                    }
                )
            }
            
            composable("profile") {
                ProfileScreen(
                    authViewModel = authViewModel,
                    listingViewModel = listingViewModel,
                    onNavigateToEditProfile = { navController.navigate("edit_profile") },
                    onNavigateToSavedListings = { navController.navigate("favorites") },
                    onNavigateToNotifications = { navController.navigate("notifications") },
                    onNavigateToPayments = { navController.navigate("payment_methods") },
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToAdminPanel = { navController.navigate("admin_panel") }
                )
            }
            
            // Admin Panel (Only accessible for admin users)
            composable("admin_panel") {
                AdminPanelScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    listingViewModel = listingViewModel
                )
            }
            
            // Category Selection (+ button)
            composable("category_selection") {
                CategorySelectionScreen(
                    onCategorySelected = { category ->
                        navController.navigate("create_listing/$category") {
                            popUpTo("category_selection") { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            // Create Listing
            composable("create_listing/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category")
                CreateListingScreen(
                    authViewModel = authViewModel,
                    listingViewModel = listingViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
            
            // Edit Profile
            composable("edit_profile") {
                EditProfileScreen(
                    authViewModel = authViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            // Notifications
            composable("notifications") {
                NotificationsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            // Payment Methods
            composable("payment_methods") {
                PaymentMethodsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            // Search
            composable("search") {
                SearchScreen(
                    listingViewModel = listingViewModel,
                    onBackClick = { navController.popBackStack() },
                    onListingClick = { listing ->
                        navController.navigate("listing_detail/${listing.id}")
                    }
                )
            }
            
            // Listing Detail
            composable("listing_detail/{listingId}") { backStackEntry ->
                val listingId = backStackEntry.arguments?.getString("listingId")
                val listing = listingViewModel.listings.value.find { it.id == listingId }
                val currentUserId = authViewModel.currentUser.value?.id ?: ""
                
                listing?.let {
                    ListingDetailScreenImproved(
                        listing = it,
                        authViewModel = authViewModel,
                        onBackClick = { navController.popBackStack() },
                        onChatClick = {
                            // Create or get chat ID
                            val chatId = if (currentUserId < it.userId) {
                                "${currentUserId}_${it.userId}"
                            } else {
                                "${it.userId}_${currentUserId}"
                            }
                            // Navigate to chat with proper parameters
                            navController.navigate("chat/$chatId/${it.userId}")
                        },
                        onEditClick = {
                            navController.navigate("edit_listing/${it.id}")
                        }
                    )
                }
            }
            
            // Chat
            composable("chat/{chatId}/{otherUserId}") { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""
                ChatScreen(
                    chatId = chatId,
                    otherUserId = otherUserId,
                    authViewModel = authViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBarWithFAB(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onFABClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = CardBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            BottomNavItem(
                icon = Icons.Outlined.Home,
                selectedIcon = Icons.Filled.Home,
                label = "HOME",
                isSelected = currentRoute == "home",
                onClick = { onNavigate("home") }
            )
            
            // Chats
            BottomNavItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                selectedIcon = Icons.Filled.ChatBubble,
                label = "CHATS",
                isSelected = currentRoute == "chats",
                onClick = { onNavigate("chats") }
            )
            
            // FAB Space with Gradient Ring
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                GradientFAB(onClick = onFABClick)
            }
            
            // Favorites
            BottomNavItem(
                icon = Icons.Outlined.FavoriteBorder,
                selectedIcon = Icons.Filled.Favorite,
                label = "MY ADS",
                isSelected = currentRoute == "favorites",
                onClick = { onNavigate("favorites") }
            )
            
            // Profile
            BottomNavItem(
                icon = Icons.Outlined.PersonOutline,
                selectedIcon = Icons.Filled.Person,
                label = "ACCOUNT",
                isSelected = currentRoute == "profile",
                onClick = { onNavigate("profile") }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(60.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else icon,
                contentDescription = label,
                tint = if (isSelected) DarkBackground else TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) DarkBackground else TextSecondary
            )
        }
    }
}

@Composable
fun GradientFAB(onClick: () -> Unit) {
    // Animated gradient rotation
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )
    
    Box(
        modifier = Modifier.size(72.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer gradient ring
        Box(
            modifier = Modifier
                .size(72.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF3A77FF), // Blue
                            Color(0xFF00D4FF), // Cyan
                            Color(0xFFFFD700), // Yellow/Gold
                            Color(0xFF3A77FF)  // Back to blue
                        )
                    )
                )
        )
        
        // Inner white circle
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        
        // Center button
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            containerColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Create Listing",
                tint = TextSecondary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
