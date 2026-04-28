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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.katafract.exifarmor.models.PhotoMetadata
import com.katafract.exifarmor.models.StripOptions
import kotlin.OptIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    photos: List<PhotoMetadata>,
    currentOptions: StripOptions,
    isPro: Boolean = false,
    onOptionsChanged: (StripOptions) -> Unit,
    onStrip: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                // Honor system bars so the bar paints under the status bar
                // with the correct top padding (no overlap).
                windowInsets = TopAppBarDefaults.windowInsets,
                title = {
                    Text(
                        text = "Review Photos",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = rememberHapticClick(KataHaptic.Light) { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "${photos.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
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
            // Strip Options
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "Cleanup Preset",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PresetChip(
                        label = "Minimal",
                        selected = currentOptions == StripOptions.LOCATION_ONLY,
                        onClick = { onOptionsChanged(StripOptions.LOCATION_ONLY) },
                    )
                    PresetChip(
                        label = "Privacy",
                        selected = currentOptions == StripOptions.PRIVACY_FOCUSED,
                        onClick = { onOptionsChanged(StripOptions.PRIVACY_FOCUSED) },
                    )
                    PresetChip(
                        label = "Full Clean",
                        selected = currentOptions == StripOptions.ALL,
                        onClick = { onOptionsChanged(StripOptions.ALL) },
                    )
                }
            }

            // Free tier warning
            if (!isPro && photos.size > 5) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFB23A).copy(alpha = 0.15f))
                        .padding(12.dp),
                ) {
                    Text(
                        text = "Free tier: only first 5 will be processed. Upgrade for unlimited.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB8860B),
                    )
                }
            }

            // Photos List — empty state if a parent ever pushes us here with [].
            if (photos.isEmpty()) {
                KataEmpty(
                    title = "No photos selected",
                    description = "Choose photos from your library or share them from another app.",
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    ),
                ) {
                    items(photos) { photo ->
                        PhotoPreviewCard(photo = photo)
                    }
                }
            }

            // Action Button
            Button(
                onClick = rememberHapticClick { onStrip() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = photos.isNotEmpty(),
            ) {
                Text(
                    text = "Strip All",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = rememberHapticClick(KataHaptic.Selection) { onClick() },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        modifier = modifier.height(34.dp),
        shape = RoundedCornerShape(10.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            selectedLabelColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
fun PhotoPreviewCard(
    photo: PhotoMetadata,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
    ) {
        // Thumbnail + Filename
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = photo.uri,
                contentDescription = photo.filename,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = photo.filename,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                // Risk indicator
                val riskText = when {
                    photo.hasGps -> "High risk: GPS detected"
                    !photo.deviceMake.isNullOrBlank() -> "Medium risk: Device info"
                    else -> "Low risk: Clean"
                }
                val riskColor = when {
                    photo.hasGps -> Color(0xFFFF4040)
                    !photo.deviceMake.isNullOrBlank() -> Color(0xFFFFB23A)
                    else -> Color(0xFF33E680)
                }

                Text(
                    text = riskText,
                    style = MaterialTheme.typography.labelSmall,
                    color = riskColor,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        // Metadata details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (photo.hasGps) {
                MetadataRow(label = "GPS", value = "Yes", color = Color(0xFFFF4040))
            }

            if (!photo.deviceMake.isNullOrBlank() || !photo.deviceModel.isNullOrBlank()) {
                MetadataRow(
                    label = "Device",
                    value = "${photo.deviceMake ?: "Unknown"} ${photo.deviceModel ?: ""}".trim(),
                )
            }

            if (!photo.dateTimeOriginal.isNullOrBlank()) {
                MetadataRow(label = "Timestamp", value = photo.dateTimeOriginal!!)
            }

            if (photo.pixelWidth != null && photo.pixelHeight != null) {
                MetadataRow(label = "Resolution", value = "${photo.pixelWidth}×${photo.pixelHeight}")
            }
        }
    }
}

@Composable
private fun MetadataRow(
    label: String,
    value: String,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = if (color == Color.Unspecified) MaterialTheme.colorScheme.onSurface else color,
            maxLines = 1,
        )
    }
}
