package com.mavrix.Olx_Rental

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mavrix.Olx_Rental.ui.screen.*
import com.mavrix.Olx_Rental.ui.theme.RentieoTheme
import com.mavrix.Olx_Rental.ui.viewmodel.AuthViewModel
import com.mavrix.Olx_Rental.ui.viewmodel.ListingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentieoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RentieoApp()
                }
            }
        }
    }
}

@Composable
fun RentieoApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val listingViewModel: ListingViewModel = viewModel()
    
    val isInitialized by authViewModel.isInitialized.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    if (!isInitialized) {
        SplashScreen(
            onNavigateToLogin = {},
            onNavigateToHome = {},
            isAuthenticated = false
        )
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) "main" else "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                isAuthenticated = currentUser != null
            )
        }

        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate("signup")
                },
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            val currentUser by authViewModel.currentUser.collectAsState()
            
            // If user is admin, show admin panel directly
            if (currentUser?.isAdmin == true) {
                AdminPanelScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    listingViewModel = listingViewModel
                )
            } else {
                // Regular users see the normal app
                MainScreenImproved(
                    authViewModel = authViewModel,
                    listingViewModel = listingViewModel,
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
