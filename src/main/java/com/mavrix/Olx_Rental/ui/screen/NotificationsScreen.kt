package com.mavrix.Olx_Rental.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavrix.Olx_Rental.ui.theme.DarkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("notifications_prefs", android.content.Context.MODE_PRIVATE)
    
    var pushNotifications by remember { mutableStateOf(prefs.getBoolean("push_notifications", true)) }
    var emailNotifications by remember { mutableStateOf(prefs.getBoolean("email_notifications", true)) }
    var messageNotifications by remember { mutableStateOf(prefs.getBoolean("message_notifications", true)) }
    var listingUpdates by remember { mutableStateOf(prefs.getBoolean("listing_updates", true)) }
    var promotionalEmails by remember { mutableStateOf(prefs.getBoolean("promotional_emails", false)) }

    fun savePreference(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Push Notifications Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    Text(
                        "Push Notifications",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider()
                    
                    SwitchListItem(
                        title = "Enable Push Notifications",
                        subtitle = "Receive notifications on your device",
                        checked = pushNotifications,
                        onCheckedChange = {
                            pushNotifications = it
                            savePreference("push_notifications", it)
                        }
                    )
                    Divider()
                    
                    SwitchListItem(
                        title = "Message Notifications",
                        subtitle = "Get notified when you receive messages",
                        checked = messageNotifications,
                        onCheckedChange = {
                            messageNotifications = it
                            savePreference("message_notifications", it)
                        }
                    )
                    Divider()
                    
                    SwitchListItem(
                        title = "Listing Updates",
                        subtitle = "Updates about your listings",
                        checked = listingUpdates,
                        onCheckedChange = {
                            listingUpdates = it
                            savePreference("listing_updates", it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email Notifications Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    Text(
                        "Email Notifications",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider()
                    
                    SwitchListItem(
                        title = "Enable Email Notifications",
                        subtitle = "Receive notifications via email",
                        checked = emailNotifications,
                        onCheckedChange = {
                            emailNotifications = it
                            savePreference("email_notifications", it)
                        }
                    )
                    Divider()
                    
                    SwitchListItem(
                        title = "Promotional Emails",
                        subtitle = "Receive offers and promotions",
                        checked = promotionalEmails,
                        onCheckedChange = {
                            promotionalEmails = it
                            savePreference("promotional_emails", it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Clear All Button
            OutlinedButton(
                onClick = {
                    // TODO: Implement clear all notifications
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Text("Clear All Notifications")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SwitchListItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DarkBackground
            )
        )
    }
}
