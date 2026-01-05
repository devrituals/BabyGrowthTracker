package com.example.babygrowthtracker.ui.paywall

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PaywallScreen(
    viewModel: PaywallViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Go Premium!", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Unlock all features:")
        Text("- Beautiful Growth Charts")
        Text("- PDF Reports")
        Text("- Multi-user Sync")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            if (context is Activity) {
                viewModel.purchase(context)
            }
        }) {
            Text("Subscribe Now")
        }
    }
}