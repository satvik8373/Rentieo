package com.mavrix.Olx_Rental.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavrix.Olx_Rental.data.model.ListingCategory
import com.mavrix.Olx_Rental.ui.theme.DarkBackground

data class CategoryItem(
    val emoji: String,
    val title: String,
    val category: ListingCategory
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen(
    onCategorySelected: (ListingCategory) -> Unit,
    onBackClick: () -> Unit
) {
    val categories = listOf(
        CategoryItem("🚗", "Vehicles", ListingCategory.VEHICLES),
        CategoryItem("🏢", "Properties", ListingCategory.REAL_ESTATE),
        CategoryItem("📱", "Mobiles", ListingCategory.ELECTRONICS),
        CategoryItem("💼", "Jobs", ListingCategory.SERVICES),
        CategoryItem("🏍️", "Bikes", ListingCategory.VEHICLES),
        CategoryItem("📺", "Electronics", ListingCategory.ELECTRONICS),
        CategoryItem("🛋️", "Furniture", ListingCategory.FURNITURE),
        CategoryItem("👕", "Fashion", ListingCategory.FASHION),
        CategoryItem("🎸", "Hobbies", ListingCategory.OTHER),
        CategoryItem("🐕", "Pets", ListingCategory.OTHER),
        CategoryItem("🔧", "Services", ListingCategory.SERVICES),
        CategoryItem("📚", "Books", ListingCategory.OTHER)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkBackground,
                            Color(0xFF004D56),
                            DarkBackground
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "What are you offering?",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                Text(
                    "Choose a category to get started",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { item ->
                        CategoryCard(
                            emoji = item.emoji,
                            title = item.title,
                            onClick = { onCategorySelected(item.category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    emoji: String,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                emoji,
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
