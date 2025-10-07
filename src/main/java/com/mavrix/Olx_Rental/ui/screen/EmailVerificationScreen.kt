package com.mavrix.Olx_Rental.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavrix.Olx_Rental.ui.theme.DarkBackground

@Composable
fun EmailVerificationScreen(
    email: String,
    onResendClick: () -> Unit,
    onBackToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Email,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = DarkBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Verify Your Email",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "We've sent a verification email to:",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            email,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Please check your inbox and click the verification link to activate your account.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onResendClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DarkBackground)
        ) {
            Text("Resend Verification Email")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Back to Login", color = DarkBackground)
        }
    }
}
