package com.katafract.exifarmor.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onPhotosSelected: (List<android.net.Uri>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                onPhotosSelected(uris)
            }
        },
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Image,
            contentDescription = "Pick photos",
            modifier = Modifier.padding(bottom = 24.dp),
            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "ExifArmor",
            fontSize = 32.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            text = "Remove EXIF metadata from your photos",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp),
        )

        Button(
            onClick = {
                launcher.launch("image/*")
            },
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            Text("Select Photos")
        }
    }
}
