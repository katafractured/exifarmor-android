package com.katafract.exifarmor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.katafract.exifarmor.models.StripResult
import kotlin.OptIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    results: List<StripResult>,
    onScanAgain: () -> Unit,
    onShare: () -> Unit,
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                // windowInsets handles the status bar inset; the top-level
                // safeDrawingPadding() in MainActivity is the safety net.
                windowInsets = TopAppBarDefaults.windowInsets,
                title = {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = rememberHapticClick(KataHaptic.Light) { onDone() }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Done",
                            modifier = Modifier.size(22.dp),
                            tint = Color(0xFF33E680),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Summary Card
            val successCount = results.count { it.success }
            val totalFields = results.sumOf { it.fieldsRemoved }
            val failureCount = results.size - successCount

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "$successCount photos cleaned · $totalFields fields removed",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    if (failureCount > 0) {
                        Text(
                            text = "$failureCount photo(s) failed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        )
                    }
                }
            }

            // Results List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 12.dp,
                ),
            ) {
                items(results) { result ->
                    ResultCard(result = result)
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = rememberHapticClick { onShare() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 6.dp),
                    )
                    Text(
                        text = "Share Cleaned Photos",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                OutlinedButton(
                    onClick = rememberHapticClick(KataHaptic.Light) { onScanAgain() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        text = "Strip More Photos",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun ResultCard(
    result: StripResult,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
    ) {
        // Header with status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = result.originalMetadata.filename,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (result.success) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Success",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF33E680),
                    )
                    Text(
                        text = "Clean",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF33E680),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Failed",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFFFF4040),
                    )
                    Text(
                        text = "Failed",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF4040),
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        )

        if (result.success) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "${result.fieldsRemoved} field${if (result.fieldsRemoved != 1) "s" else ""} removed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                // Show what was removed
                val removed = mutableListOf<String>()
                if (result.originalMetadata.hasGps) removed.add("GPS location")
                if (!result.originalMetadata.deviceMake.isNullOrBlank() ||
                    !result.originalMetadata.deviceModel.isNullOrBlank()
                ) {
                    removed.add("Device info")
                }
                if (!result.originalMetadata.dateTimeOriginal.isNullOrBlank()) {
                    removed.add("Timestamp")
                }

                if (removed.isNotEmpty()) {
                    Text(
                        text = removed.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                    )
                }
            }
        } else {
            Text(
                text = "Error: ${result.error ?: "Unknown error"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFF4040),
            )
        }
    }
}
