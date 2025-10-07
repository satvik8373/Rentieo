package com.mavrix.Olx_Rental.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavrix.Olx_Rental.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mavrix.Olx_Rental.data.model.ListingModel
import com.mavrix.Olx_Rental.data.model.UserModel
import com.mavrix.Olx_Rental.ui.viewmodel.AuthViewModel
import com.mavrix.Olx_Rental.ui.viewmodel.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    listingViewModel: ListingViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)
    val allUsers by authViewModel.allUsers.collectAsState(initial = emptyList())
    val allListings by listingViewModel.listings.collectAsState(initial = emptyList())
    
    val tabs = listOf("Dashboard", "Users", "Listings", "Analytics")
    val tabIcons = listOf(
        Icons.Outlined.Dashboard,
        Icons.Outlined.People,
        Icons.Outlined.ListAlt,
        Icons.Outlined.Analytics
    )

    LaunchedEffect(Unit) {
        authViewModel.loadAllUsers()
        listingViewModel.loadAllListingsForAdmin()
    }
    
    // Check if user is actually admin
    if (currentUser?.isAdmin != true) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Spacer(Modifier.height(16.dp))
                Text("Access Denied", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("You don't have admin privileges", color = Color.Gray)
                Spacer(Modifier.height(24.dp))
                Button(onClick = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text("Sign Out")
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = R.drawable.bird_icon),
                                contentDescription = "Rentieo Logo",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(tabs[selectedTab])
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Outlined.Notifications, "Notifications")
                    }
                    IconButton(onClick = {
                        authViewModel.signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Filled.Logout, "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Row(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Sidebar
            NavigationRail(
                modifier = Modifier.fillMaxHeight(),
                header = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                currentUser?.name?.firstOrNull()?.uppercase() ?: "A",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            currentUser?.name ?: "Admin",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = Color.Red,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                "ADMIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationRailItem(
                        icon = { Icon(tabIcons[index], tab) },
                        label = { Text(tab, fontSize = 12.sp) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                when (selectedTab) {
                    0 -> AdminDashboardTab(allUsers, allListings)
                    1 -> AdminUsersTab(allUsers, authViewModel)
                    2 -> AdminListingsTab(allListings, listingViewModel)
                    3 -> AdminAnalyticsTab(allUsers, allListings)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardTab(users: List<UserModel>, listings: List<ListingModel>) {
    val totalUsers = users.size
    val verifiedUsers = users.count { it.isVerified }
    val totalListings = listings.size
    val activeListings = listings.count { it.isActive }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Dashboard Overview",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Welcome to Rentieo Admin Panel",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(200.dp)
            ) {
                item {
                    StatCard(
                        title = "Total Users",
                        value = totalUsers.toString(),
                        subtitle = "+$verifiedUsers verified",
                        icon = Icons.Filled.People,
                        color = Color(0xFF2196F3)
                    )
                }
                item {
                    StatCard(
                        title = "Total Listings",
                        value = totalListings.toString(),
                        subtitle = "$activeListings active",
                        icon = Icons.Filled.ListAlt,
                        color = Color(0xFF4CAF50)
                    )
                }
                item {
                    StatCard(
                        title = "Active Listings",
                        value = activeListings.toString(),
                        subtitle = "${if (totalListings > 0) (activeListings * 100 / totalListings) else 0}% of total",
                        icon = Icons.Filled.CheckCircle,
                        color = Color(0xFFFF9800)
                    )
                }
                item {
                    StatCard(
                        title = "Inactive",
                        value = (totalListings - activeListings).toString(),
                        subtitle = "Need attention",
                        icon = Icons.Filled.Cancel,
                        color = Color(0xFFF44336)
                    )
                }
            }
        }

        item {
            Text(
                "Recent Activity",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    ActivityItem(Icons.Filled.PersonAdd, "New user registered", "Just now", Color(0xFF2196F3))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ActivityItem(Icons.Filled.AddCircle, "New listing created", "5 minutes ago", Color(0xFF4CAF50))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ActivityItem(Icons.Filled.Edit, "Listing updated", "10 minutes ago", Color(0xFFFF9800))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ActivityItem(Icons.Filled.Delete, "Listing deleted", "1 hour ago", Color(0xFFF44336))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Modern gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.08f),
                                color.copy(alpha = 0.02f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(color.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon, 
                            contentDescription = null, 
                            tint = color, 
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Surface(
                        color = Color(0xFF4CAF50).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "+12%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
                
                Column {
                    Text(
                        value,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = color,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        title,
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        subtitle,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityItem(icon: ImageVector, title: String, time: String, color: Color) {
    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
        },
        headlineContent = {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        },
        trailingContent = {
            Surface(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    time,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    )
}

@Composable
fun AdminUsersTab(users: List<UserModel>, authViewModel: AuthViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var filterRole by remember { mutableStateOf("all") }
    
    val filteredUsers = users.filter { user ->
        val matchesSearch = user.name.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true)
        val matchesRole = filterRole == "all" || user.role.name.equals(filterRole, ignoreCase = true)
        matchesSearch && matchesRole
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "User Management",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Manage all users and their permissions", color = Color.Gray)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search users...") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    modifier = Modifier.weight(2f)
                )
                
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = filterRole.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filter by Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                        listOf("all", "rental", "labor", "admin").forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    filterRole = role
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        items(filteredUsers) { user ->
            UserCard(user, authViewModel)
        }
    }
}

@Composable
fun UserCard(user: UserModel, authViewModel: AuthViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(getRoleColor(user.role.name).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            user.name.firstOrNull()?.uppercase() ?: "U",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = getRoleColor(user.role.name)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                user.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                maxLines = 1,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (user.isVerified) {
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    "Verified",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            user.email,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            color = getRoleColor(user.role.name),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                user.role.name.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        authViewModel.toggleUserVerification(user.id, !user.isVerified)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (user.isVerified) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        if (user.isVerified) Icons.Filled.Block else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (user.isVerified) "Unverify" else "Verify",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                OutlinedButton(
                    onClick = {
                        authViewModel.toggleAdminRole(user.id, user.role)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Filled.AdminPanelSettings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Admin",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF44336)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Delete",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { 
                Text(
                    "Delete User",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text("Are you sure you want to delete ${user.name}? This action cannot be undone.") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.deleteUser(user.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminListingsTab(listings: List<ListingModel>, listingViewModel: ListingViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredListings = listings.filter { listing ->
        listing.title.contains(searchQuery, ignoreCase = true) ||
                listing.description.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Listing Management",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Manage all listings", color = Color.Gray)
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search listings...") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(filteredListings) { listing ->
            ListingCard(listing, listingViewModel)
        }
    }
}

@Composable
fun ListingCard(listing: ListingModel, listingViewModel: ListingViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    // Listing Image
                    Card(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        if (listing.images.isNotEmpty()) {
                            coil.compose.AsyncImage(
                                model = listing.images.first(),
                                contentDescription = listing.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Image,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = listing.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 2,
                            color = Color.Black
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = listing.category.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2196F3),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                            Surface(
                                color = if (listing.isActive) 
                                    Color(0xFF4CAF50).copy(alpha = 0.15f) 
                                else 
                                    Color(0xFFF44336).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (listing.isActive) Color(0xFF4CAF50) 
                                                else Color(0xFFF44336)
                                            )
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        if (listing.isActive) "Active" else "Inactive",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (listing.isActive) Color(0xFF4CAF50) 
                                               else Color(0xFFF44336)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "₹${listing.price}/day",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4CAF50)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = listing.description.take(80) + 
                                   if (listing.description.length > 80) "..." else "",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            maxLines = 2
                        )
                    }
                }
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.LightGray.copy(alpha = 0.3f)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        listingViewModel.toggleListingStatus(listing.id, !listing.isActive)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF9800)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        if (listing.isActive) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (listing.isActive) "Hide" else "Show",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF44336)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Delete",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { 
                Text(
                    "Delete Listing",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text("Are you sure you want to delete \"${listing.title}\"? This action cannot be undone.") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        listingViewModel.deleteListing(listing.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminAnalyticsTab(users: List<UserModel>, listings: List<ListingModel>) {
    val rentalUsers = users.count { it.role == com.mavrix.Olx_Rental.data.model.UserRole.RENTAL }
    val laborUsers = users.count { it.role == com.mavrix.Olx_Rental.data.model.UserRole.LABOR }
    val verifiedUsers = users.count { it.isVerified }
    val activeListings = listings.count { it.isActive }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                "Analytics & Reports",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("View platform statistics and insights", fontSize = 16.sp, color = Color.Gray)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "User Statistics",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = Color(0xFF2196F3).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.People,
                                    null,
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "${users.size} Total",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnalyticsItem(
                            "Verified Users",
                            verifiedUsers.toString(),
                            "${if (users.isNotEmpty()) (verifiedUsers * 100 / users.size) else 0}%",
                            Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsItem(
                            "Rental Users",
                            rentalUsers.toString(),
                            "${if (users.isNotEmpty()) (rentalUsers * 100 / users.size) else 0}%",
                            Color(0xFF2196F3),
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsItem(
                            "Labor Users",
                            laborUsers.toString(),
                            "${if (users.isNotEmpty()) (laborUsers * 100 / users.size) else 0}%",
                            Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Listing Statistics",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.ListAlt,
                                    null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "${listings.size} Total",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnalyticsItem(
                            "Active Listings",
                            activeListings.toString(),
                            "${if (listings.isNotEmpty()) (activeListings * 100 / listings.size) else 0}%",
                            Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsItem(
                            "Inactive",
                            (listings.size - activeListings).toString(),
                            "${if (listings.isNotEmpty()) ((listings.size - activeListings) * 100 / listings.size) else 0}%",
                            Color(0xFFF44336),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(20.dp))
                    
                    Text(
                        "Category Breakdown",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(12.dp))
                    
                    val categories = listings.groupBy { it.category }
                    categories.forEach { (category, items) ->
                        CategoryBreakdownItem(
                            category.name,
                            items.size,
                            if (listings.isNotEmpty()) (items.size * 100 / listings.size) else 0
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsItem(
    label: String,
    value: String,
    percentage: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(6.dp))
            Surface(
                color = color,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    percentage,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryBreakdownItem(category: String, count: Int, percentage: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                category,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Progress bar
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(percentage / 100f)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            
            Text(
                "$count",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(30.dp)
            )
            
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    "$percentage%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

fun getRoleColor(role: String): Color {
    return when (role.lowercase()) {
        "admin" -> Color(0xFFF44336)
        "labor" -> Color(0xFF2196F3)
        "rental" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }
}
