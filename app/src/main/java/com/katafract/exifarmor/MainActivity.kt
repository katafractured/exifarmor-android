package com.katafract.exifarmor

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.katafract.exifarmor.ui.HomeScreen
import com.katafract.exifarmor.ui.PreviewScreen
import com.katafract.exifarmor.ui.ResultsScreen
import com.katafract.exifarmor.ui.theme.ExifArmorTheme
import com.katafract.exifarmor.viewmodel.MainViewModel
import com.katafract.exifarmor.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle incoming intents (share/send)
        handleIntent(intent)

        setContent {
            ExifArmorTheme {
                val screen = viewModel.screen.collectAsState()
                val photoList = viewModel.photoList.collectAsState()
                val stripResults = viewModel.stripResults.collectAsState()
                val stripOptions = viewModel.stripOptions.collectAsState()

                when (screen.value) {
                    Screen.HOME -> {
                        HomeScreen(
                            onPhotosSelected = { uris ->
                                viewModel.loadPhotos(uris)
                            },
                        )
                    }

                    Screen.PREVIEW -> {
                        PreviewScreen(
                            photos = photoList.value,
                            currentOptions = stripOptions.value,
                            onOptionsChanged = { options ->
                                viewModel.updateOptions(options)
                            },
                            onStrip = {
                                viewModel.startStrip()
                            },
                            onBack = {
                                viewModel.reset()
                            },
                        )
                    }

                    Screen.PROCESSING -> {
                        androidx.compose.material3.Text("Processing...")
                    }

                    Screen.DONE -> {
                        ResultsScreen(
                            results = stripResults.value,
                            onScanAgain = {
                                viewModel.reset()
                            },
                            onShare = {
                                viewModel.shareResults(this)
                            },
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: android.content.Intent?) {
        if (intent == null) return

        when (intent.action) {
            android.content.Intent.ACTION_SEND -> {
                val uri = intent.getParcelableExtra<Uri>(android.content.Intent.EXTRA_STREAM)
                if (uri != null) {
                    viewModel.loadPhotos(listOf(uri))
                }
            }

            android.content.Intent.ACTION_SEND_MULTIPLE -> {
                val uris = intent.getParcelableArrayListExtra<Uri>(android.content.Intent.EXTRA_STREAM)
                if (uris != null && uris.isNotEmpty()) {
                    viewModel.loadPhotos(uris)
                }
            }
        }
    }
}
