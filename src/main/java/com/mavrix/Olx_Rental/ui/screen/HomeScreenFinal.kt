package com.mavrix.Olx_Rental.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mavrix.Olx_Rental.data.model.ListingCategory
import com.mavrix.Olx_Rental.data.model.ListingModel
import com.mavrix.Olx_Rental.data.service.LocationService
import com.mavrix.Olx_Rental.ui.viewmodel.ListingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenFinal(
    listingViewModel: ListingViewModel,
    onListingClick: (ListingModel) -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationService = remember { LocationService(context) }
    
    val listings by listingViewModel.listings.collectAsState()
    val isLoading by listingViewModel.isLoading.collectAsState()
    var selectedCategory by remember { mutableStateOf<ListingCategory?>(null) }
    var currentLocation by remember { mutableStateOf("Detecting location...") }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    
    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                isLoadingLocation = true
                locationService.getCurrentLocation().onSuccess { locationData ->
                    currentLocation = locationData.address
                    isLoadingLocation = false
                }.onFailure {
                    currentLocation = "Location unavailable"
                    isLoadingLocation = false
                }
            }
        } else {
            currentLocation = "Location permission denied"
        }
    }
    
    // Request location on first load
    LaunchedEffect(Unit) {
        if (locationService.hasLocationPermission()) {
            isLoadingLocation = true
            locationService.getCurrentLocation().onSuccess { locationData ->
                currentLocation = locationData.address
                isLoadingLocation = false
            }.onFailure {
                currentLocation = "Gulberg Phase 4, Lahore"
                isLoadingLocation = false
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            isRefreshing = true
            scope.launch {
                listingViewModel.refreshListings(selectedCategory)
                isRefreshing = false
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Logo
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                                .background(Color.White, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "R",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF002F34)
                            )
                        }
                        
                        Spacer(Modifier.width(12.dp))
                        
                        // Title
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rentieo",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF002F34),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Your Marketplace for Everything",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                letterSpacing = 0.3.sp
                            )
                        }
                        
                        // Search Icon
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        
                        // Notification Icon
                        IconButton(onClick = onNotificationsClick) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    // Category Dropdown
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            .clickable { expanded = true }
                            .padding(horizontal = 12.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCategory?.name?.replace("_", " ") ?: "All Categories",
                                fontSize = 14.sp
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Categories") },
                                onClick = {
                                    selectedCategory = null
                                    listingViewModel.loadListings(null)
                                    expanded = false
                                }
                            )
                            ListingCategory.values().forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name.replace("_", " ")) },
                                    onClick = {
                                        selectedCategory = category
                                        listingViewModel.loadListings(category)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Location Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE0E0E0)
                        )
                        .clickable { /* Show location options */ }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoadingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (currentLocation == "Location unavailable") Color.Red else Color.Black
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = currentLocation,
                        fontSize = 14.sp,
                        color = if (currentLocation == "Location unavailable") Color.Red else Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Browse Categories Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Browse Categories",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "14+",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // 2-Row Category Grid (Horizontal Scroll)
            item {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(2),
                    modifier = Modifier.height(200.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(getCategoryList()) { category ->
                        CategoryCard(
                            icon = category.icon,
                            label = category.label,
                            isSelected = selectedCategory == category.category,
                            onClick = {
                                selectedCategory = category.category
                                listingViewModel.loadListings(category.category)
                            }
                        )
                    }
                }
            }
            
            // Featured Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Featured",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "10+",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    TextButton(onClick = { /* See more */ }) {
                        Text("See more", fontSize = 14.sp)
                    }
                }
            }
            
            // Featured Listings (Horizontal Scroll)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listings.take(5)) { listing ->
                        FeaturedListingCard(
                            listing = listing,
                            onClick = { onListingClick(listing) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            
            // Mobile Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Mobile",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "100+",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    TextButton(onClick = { /* See more */ }) {
                        Text("See more", fontSize = 14.sp)
                    }
                }
            }
            
            // Grid Listings
            if (isLoading && listings.isEmpty()) {
                items(6) {
                    ShimmerGridCard()
                }
            } else if (listings.isEmpty()) {
                item {
                    EmptyStateGrid()
                }
            } else {
                items(listings.chunked(2)) { rowListings ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowListings.forEach { listing ->
                            Box(modifier = Modifier.weight(1f)) {
                                GridListingCard(
                                    listing = listing,
                                    onClick = { onListingClick(listing) }
                                )
                            }
                        }
                        if (rowListings.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            
            // Bottom Spacing
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(85.dp)
            .border(
                width = 1.5.dp,
                color = if (isSelected) Color(0xFF002F34) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = if (isSelected) Color(0xFF002F34) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) Color.White else Color(0xFF002F34)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FeaturedListingCard(
    listing: ListingModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = listing.images.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Heart Icon
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { /* Toggle favorite */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Featured Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(Color(0xFFFFC107), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Featured",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = listing.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Rs ${listing.price.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(
                        text = listing.condition?.name ?: "New",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${listing.views}/10",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = listing.location,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun GridListingCard(
    listing: ListingModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = listing.images.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Heart Icon
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { /* Toggle favorite */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Rs ${listing.price.toInt()}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = listing.title,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = listing.location,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ShimmerGridCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(12.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                )
            }
        }
    }
}

@Composable
fun EmptyStateGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.Inventory2,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No listings found",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

data class HomeCategoryItem(
    val icon: ImageVector,
    val label: String,
    val category: ListingCategory
)

fun getCategoryList() = listOf(
    HomeCategoryItem(Icons.Default.PhoneAndroid, "Mobiles", ListingCategory.ELECTRONICS),
    HomeCategoryItem(Icons.Default.DirectionsCar, "Vehicles", ListingCategory.VEHICLES),
    HomeCategoryItem(Icons.Default.Home, "Property", ListingCategory.REAL_ESTATE),
    HomeCategoryItem(Icons.Default.Computer, "Electronics", ListingCategory.ELECTRONICS),
    HomeCategoryItem(Icons.Default.Chair, "Furniture", ListingCategory.FURNITURE),
    HomeCategoryItem(Icons.Default.TwoWheeler, "Bikes", ListingCategory.VEHICLES),
    HomeCategoryItem(Icons.Default.Checkroom, "Fashion", ListingCategory.FASHION),
    HomeCategoryItem(Icons.Default.Business, "Business", ListingCategory.SERVICES),
    HomeCategoryItem(Icons.Default.Build, "Services", ListingCategory.SERVICES),
    HomeCategoryItem(Icons.Default.Pets, "Animals", ListingCategory.OTHER),
    HomeCategoryItem(Icons.Default.Book, "Books", ListingCategory.OTHER),
    HomeCategoryItem(Icons.Default.SportsSoccer, "Sports", ListingCategory.OTHER),
    HomeCategoryItem(Icons.Default.Toys, "Kids", ListingCategory.OTHER),
    HomeCategoryItem(Icons.Default.Restaurant, "Food", ListingCategory.SERVICES)
)
