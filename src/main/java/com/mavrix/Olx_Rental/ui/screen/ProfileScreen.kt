package com.mavrix.Olx_Rental.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mavrix.Olx_Rental.ui.theme.DarkBackground
import com.mavrix.Olx_Rental.ui.viewmodel.AuthViewModel
import com.mavrix.Olx_Rental.ui.viewmodel.ListingViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    listingViewModel: ListingViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSavedListings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToAdminPanel: (() -> Unit)? = null
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val myListings by listingViewModel.listings.collectAsState()
    
    val userListings = remember(myListings, currentUser) {
        myListings.filter { it.userId == currentUser?.id }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Profile Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkBackground),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Photo
                    if (currentUser?.photoUrl != null) {
                        AsyncImage(
                            model = currentUser?.photoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.name?.firstOrNull()?.uppercase() ?: "U",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkBackground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentUser?.name ?: "User",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentUser?.email ?: "",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    if (currentUser?.isVerified == true) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color.Green,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Member",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Stats Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(Icons.Default.ListAlt, "${userListings.size}", "Listings")
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                    )
                    StatItem(Icons.Default.Star, "${currentUser?.rating ?: 0.0}", "Rating")
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                    )
                    StatItem(Icons.Default.Reviews, "${currentUser?.totalRatings ?: 0}", "Reviews")
                }
            }
        }

        // Admin Panel (Only for Admin Users)
        if (currentUser?.isAdmin == true && onNavigateToAdminPanel != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B35))
                ) {
                    MenuItem(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "Admin Panel",
                        onClick = onNavigateToAdminPanel,
                        textColor = Color.White,
                        iconColor = Color.White
                    )
                }
            }
        }

        // Menu Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    MenuItem(Icons.Default.Person, "Edit Profile", onNavigateToEditProfile)
                    Divider()
                    MenuItem(Icons.Default.Favorite, "Saved Listings", onNavigateToSavedListings)
                    Divider()
                    MenuItem(Icons.Default.Notifications, "Notifications", onNavigateToNotifications)
                    Divider()
                    MenuItem(Icons.Default.Payment, "Payment Methods", onNavigateToPayments)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Support Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    MenuItem(Icons.Default.Help, "Help & Support", onClick = { })
                    Divider()
                    MenuItem(Icons.Default.Info, "About", onClick = { })
                    Divider()
                    MenuItem(Icons.Default.PrivacyTip, "Privacy Policy", onClick = { })
                    Divider()
                    MenuItem(Icons.Default.Description, "Terms & Conditions", onClick = { })
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Sign Out Button
        item {
            OutlinedButton(
                onClick = {
                    authViewModel.signOut()
                    onNavigateToLogin()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", fontSize = 16.sp)
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    textColor: Color = Color.Black,
    iconColor: Color = DarkBackground
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            title,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = if (textColor == Color.White) Color.White else Color.Gray)
    }
}
