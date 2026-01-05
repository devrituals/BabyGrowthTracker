package com.example.babygrowthtracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babygrowthtracker.R
import com.example.babygrowthtracker.domain.model.LogEntry
import com.example.babygrowthtracker.domain.model.LogType
import com.example.babygrowthtracker.ui.components.StopwatchDisplay
import com.example.babygrowthtracker.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToGrowthChart: () -> Unit,
    onNavigateToGuide: () -> Unit
) {
    val logs by viewModel.logs.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val babyName by viewModel.babyName.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var showInputDialog by remember { mutableStateOf(false) }
    var selectedLogType by remember { mutableStateOf<LogType?>(null) }
    var inputValue by remember { mutableStateOf("") }

    Scaffold(
        containerColor = OffWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = BabyBlueDark,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Log", modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HeaderSection(
                babyName = babyName,
                onChartClick = onNavigateToGrowthChart,
                onGuideClick = onNavigateToGuide
            )

            SummaryStatsRow(stats = stats)

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(R.string.recent_activity), // Localized
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (logs.isEmpty()) {
                        item { EmptyStateMessage() }
                    } else {
                        items(logs) { log -> LogItemCard(log) }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = CardWhite
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(stringResource(R.string.new_activity), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = OffWhite),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            StopwatchDisplay(
                                isRunning = viewModel.isTimerRunning,
                                elapsedSeconds = viewModel.timerSeconds,
                                onTick = { viewModel.updateTimer(it) }
                            )
                            Spacer(Modifier.height(8.dp))

                            IconButton(
                                onClick = { viewModel.toggleTimer() },
                                modifier = Modifier.size(64.dp)
                            ) {
                                Icon(
                                    imageVector = if (viewModel.isTimerRunning) PauseIcon else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle Timer",
                                    modifier = Modifier.size(40.dp),
                                    tint = BabyBlueDark
                                )
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.saveTimerLog(LogType.FEEDING)
                                        scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BabyBlue),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.save_feeding))
                                }
                                Button(
                                    onClick = {
                                        viewModel.saveTimerLog(LogType.SLEEP)
                                        scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BabyPurple),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.save_sleep))
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    Text(stringResource(R.string.manual_entry), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))

                    QuickActionButton(stringResource(R.string.log_feeding), "🍼", BabyBlue) {
                        selectedLogType = LogType.FEEDING
                        inputValue = ""
                        showInputDialog = true
                        scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
                    }
                    QuickActionButton(stringResource(R.string.log_sleep), "💤", BabyPurple) {
                        selectedLogType = LogType.SLEEP
                        inputValue = ""
                        showInputDialog = true
                        scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
                    }
                    QuickActionButton(stringResource(R.string.log_diaper), "👶", BabyOrange) {
                        selectedLogType = LogType.DIAPER
                        inputValue = "Wet"
                        showInputDialog = true
                        scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
                    }
                    QuickActionButton(stringResource(R.string.log_growth), "📏", BabyGreen) {
                        selectedLogType = LogType.GROWTH
                        inputValue = ""
                        showInputDialog = true
                        scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }

        if (showInputDialog && selectedLogType != null) {
            val type = selectedLogType!!
            val meta = getLogMetadata(type)
            val titleString = stringResource(meta.titleRes)

            AlertDialog(
                onDismissRequest = { showInputDialog = false },
                containerColor = CardWhite,
                title = { Text(stringResource(R.string.log_title, titleString)) },
                text = {
                    Column {
                        Text(stringResource(R.string.enter_details))
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            label = { Text(stringResource(getInputLabelRes(type))) },
                            singleLine = true,
                            keyboardOptions = getKeyboardOptions(type),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (inputValue.isNotBlank()) {
                                viewModel.addLog(type, inputValue)
                                showInputDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = meta.color)
                    ) {
                        Text(stringResource(R.string.save_entry), color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showInputDialog = false }) {
                        Text(stringResource(R.string.cancel), color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun SummaryStatsRow(stats: DashboardStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-30).dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(stringResource(R.string.last_feed), stats.lastFeedTime, Icons.Rounded.ShoppingCart, BabyBlueDark, Modifier.weight(1f))
        StatCard(stringResource(R.string.diapers), stringResource(R.string.diapers_today, stats.diapersToday), Icons.Rounded.Face, BabyOrange, Modifier.weight(1f))
        StatCard(stringResource(R.string.last_sleep), stats.lastSleepTime, Icons.Default.Face, BabyPurple, Modifier.weight(1f))
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
        }
    }
}

// CHANGED: Returns Res ID (Int) instead of String
fun getInputLabelRes(type: LogType): Int {
    return when (type) {
        LogType.FEEDING -> R.string.label_feeding
        LogType.SLEEP -> R.string.label_sleep
        LogType.DIAPER -> R.string.label_diaper
        LogType.GROWTH -> R.string.label_growth
    }
}

fun getKeyboardOptions(type: LogType): KeyboardOptions {
    return KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences)
}

@Composable
fun HeaderSection(babyName: String, onChartClick: () -> Unit, onGuideClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp))
            .background(brush = Brush.verticalGradient(colors = listOf(BabyBlue, BabyBlueDark)))
    ) {
        Column(modifier = Modifier.padding(24.dp).align(Alignment.TopStart)) {
            Text(stringResource(R.string.greeting), color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.titleMedium)
            Text(babyName, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 56.dp, end = 24.dp)
        ) {
            Button(
                onClick = onGuideClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.nav_guide), color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onChartClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.charts_btn), color = Color.White)
            }
        }
    }
}

@Composable
fun LogItemCard(log: LogEntry) {
    val meta = getLogMetadata(log.type)
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeString = formatter.format(Date(log.timestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(meta.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = meta.icon, contentDescription = null, tint = meta.color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                // CHANGED: Use stringResource with meta.titleRes
                Text(text = stringResource(meta.titleRes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = timeString, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text(text = log.value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = meta.color)
        }
    }
}

@Composable
fun QuickActionButton(text: String, emoji: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(12.dp))
            Text(text, color = TextPrimary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(60.dp), tint = Color.LightGray)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.empty_state), color = Color.Gray)
    }
}

// CHANGED: Now holds Resource ID (Int) instead of String
data class LogUiMetadata(val titleRes: Int, val icon: ImageVector, val color: Color)

fun getLogMetadata(type: LogType): LogUiMetadata {
    return when (type) {
        LogType.FEEDING -> LogUiMetadata(R.string.log_feeding, Icons.Rounded.ShoppingCart, BabyBlueDark)
        LogType.SLEEP -> LogUiMetadata(R.string.log_sleep, Icons.Default.Face, BabyPurple)
        LogType.DIAPER -> LogUiMetadata(R.string.log_diaper, Icons.Rounded.Face, BabyOrange)
        LogType.GROWTH -> LogUiMetadata(R.string.log_growth, Icons.Default.Star, BabyGreen)
    }
}

val PauseIcon: ImageVector
    get() {
        if (_pause != null) return _pause!!
        _pause = androidx.compose.ui.graphics.vector.ImageVector.Builder(
            name = "Pause",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color.Black)) {
                moveTo(6.0f, 19.0f)
                horizontalLineToRelative(4.0f)
                verticalLineTo(5.0f)
                horizontalLineTo(6.0f)
                verticalLineToRelative(14.0f)
                close()
                moveTo(14.0f, 5.0f)
                verticalLineToRelative(14.0f)
                horizontalLineToRelative(4.0f)
                verticalLineTo(5.0f)
                horizontalLineToRelative(-4.0f)
                close()
            }
        }.build()
        return _pause!!
    }

private var _pause: ImageVector? = null