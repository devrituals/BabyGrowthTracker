package com.example.babygrowthtracker.ui.growth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babygrowthtracker.R
import com.example.babygrowthtracker.domain.model.LogEntry
import com.example.babygrowthtracker.ui.theme.*
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthScreen(
    viewModel: GrowthChartViewModel = hiltViewModel(),
    onNavigateToPaywall: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.growth_history), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = OffWhite,
                contentColor = BabyBlueDark,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = BabyBlueDark
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text(stringResource(R.string.tab_height)) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text(stringResource(R.string.tab_weight)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isPremium) {
                val currentLogs = if (selectedTabIndex == 0) state.heightLogs else state.weightLogs
                val unitLabel = if (selectedTabIndex == 0) stringResource(R.string.tab_height) else stringResource(R.string.tab_weight)
                // Extract just "Height" or "Weight" for the chart title if needed, or use full label
                val title = stringResource(R.string.chart_progress, unitLabel)
                val color = if (selectedTabIndex == 0) BabyGreen else BabyPurple

                PremiumGrowthChart(
                    logs = currentLogs,
                    chartTitle = title,
                    lineColor = color,
                    onExportClick = {
                        viewModel.generatePdfReport()
                        Toast.makeText(context, "Report saved", Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                FreeGrowthPreview(onNavigateToPaywall = onNavigateToPaywall)
            }
        }
    }
}

@Composable
fun PremiumGrowthChart(
    logs: List<LogEntry>,
    chartTitle: String,
    lineColor: Color,
    onExportClick: () -> Unit
) {
    val entries = remember(logs) {
        logs.mapIndexed { index, log ->
            val cleanValue = log.value.replace(Regex("[^0-9.]"), "")
            val value = cleanValue.toFloatOrNull() ?: 0f
            FloatEntry(index.toFloat(), value)
        }
    }

    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        val index = value.toInt()
        if (index in logs.indices) {
            val date = Date(logs[index].timestamp)
            SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        } else {
            ""
        }
    }

    val chartModelProducer = remember { ChartEntryModelProducer() }
    LaunchedEffect(entries) {
        chartModelProducer.setEntries(entries)
    }

    Column {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    chartTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (entries.isNotEmpty()) {
                    Chart(
                        chart = lineChart(
                            lines = listOf(
                                LineChart.LineSpec(
                                    lineColor = lineColor.toArgb(),
                                    lineBackgroundShader = DynamicShaders.fromBrush(
                                        Brush.verticalGradient(
                                            listOf(lineColor.copy(alpha = 0.4f), lineColor.copy(alpha = 0.0f))
                                        )
                                    )
                                )
                            )
                        ),
                        chartModelProducer = chartModelProducer,
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(
                            valueFormatter = bottomAxisValueFormatter,
                            guideline = null
                        ),
                        modifier = Modifier.fillMaxWidth().height(250.dp)
                    )
                } else {
                    Box(modifier = Modifier.height(200.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_data_chart), color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(stringResource(R.string.history_section), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(logs.reversed()) { log ->
                HistoryItem(log, lineColor)
            }
        }
    }
}

@Composable
fun HistoryItem(log: LogEntry, color: Color) {
    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(log.timestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(stringResource(R.string.measured_label), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(dateStr, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Text(
                log.value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun FreeGrowthPreview(onNavigateToPaywall: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.unlock_premium), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onNavigateToPaywall, colors = ButtonDefaults.buttonColors(containerColor = BabyBlue)) {
                Text(stringResource(R.string.upgrade_premium))
            }
        }
    }
}