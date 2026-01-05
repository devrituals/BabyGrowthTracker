package com.example.babygrowthtracker.ui.guide

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.babygrowthtracker.R
import com.example.babygrowthtracker.domain.model.GuideData
import com.example.babygrowthtracker.domain.model.MonthGuide
import com.example.babygrowthtracker.ui.theme.BabyBlue
import com.example.babygrowthtracker.ui.theme.BabyGreen
import com.example.babygrowthtracker.ui.theme.BabyOrange
import com.example.babygrowthtracker.ui.theme.OffWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = OffWhite,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.guide_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(GuideData.list) { guide ->
                MonthGuideCard(guide)
            }
        }
    }
}

@Composable
fun MonthGuideCard(guide: MonthGuide) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Month
            Text(
                text = stringResource(R.string.month_label, guide.month),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Section 1: Milestones
            GuideSection(
                title = stringResource(R.string.milestones_title),
                content = stringResource(guide.milestonesRes),
                color = BabyBlue
            )

            // Section 2: Food
            GuideSection(
                title = stringResource(R.string.food_title),
                content = stringResource(guide.foodRes),
                color = BabyGreen
            )

            // Section 3: Vaccines
            GuideSection(
                title = stringResource(R.string.vaccine_title),
                content = stringResource(guide.vaccineRes),
                color = BabyOrange
            )
        }
    }
}

@Composable
fun GuideSection(title: String, content: String, color: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}