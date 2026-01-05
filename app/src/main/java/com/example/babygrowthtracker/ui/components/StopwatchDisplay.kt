package com.example.babygrowthtracker.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun StopwatchDisplay(
    isRunning: Boolean,
    elapsedSeconds: Long,
    onTick: (Long) -> Unit
) {
    LaunchedEffect(isRunning, elapsedSeconds) {
        if (isRunning) {
            delay(1000L)
            onTick(elapsedSeconds + 1)
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)

    Text(
        text = formattedTime,
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.primary
    )
}