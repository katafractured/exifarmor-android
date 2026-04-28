package com.katafract.exifarmor

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.katafract.exifarmor.ui.HomeScreen
import com.katafract.exifarmor.ui.PreviewScreen
import com.katafract.exifarmor.ui.ResultsScreen
import com.katafract.exifarmor.ui.theme.ExifArmorTheme
import com.katafract.exifarmor.ui.theme.onBackgroundVariant
import com.katafract.exifarmor.viewmodel.MainViewModel
import com.katafract.exifarmor.viewmodel.Screen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    /**
     * Flips true once Compose has had at least one frame to lay out, so the
     * Android 12 splash dismisses on a real first-paint (no white flash).
     */
    @Volatile
    private var splashReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash MUST be installed before super.onCreate; gate it on a quick
        // ready flag so the first paint already has the model wired (no flash).
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Edge-to-edge: status + navigation bars become transparent and Compose
        // is responsible for honoring window insets via safeDrawingPadding().
        enableEdgeToEdge()

        // Keep splash up for one Choreographer frame after content is set, so
        // the first paint already has the Compose root attached (no flash). The
        // gate flips inside `setContent { ... }` below via `splashReady`.
        splashScreen.setKeepOnScreenCondition { !splashReady }

        // Handle incoming intents (share/send)
        handleIntent(intent)

        setContent {
            ExifArmorTheme {
                LaunchedEffect(Unit) { splashReady = true }
                MainScreenContent(viewModel, activity = this@MainActivity)
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

@Composable
private fun MainScreenContent(
    viewModel: MainViewModel,
    activity: ComponentActivity,
) {
    val screen = viewModel.screen.collectAsState()
    val photoList = viewModel.photoList.collectAsState()
    val stripResults = viewModel.stripResults.collectAsState()
    val stripOptions = viewModel.stripOptions.collectAsState()
    val processingProgress = viewModel.processingProgress.collectAsState()
    val isPro = viewModel.isPro.collectAsState()
    val error = viewModel.error.collectAsState()
    val showUpgradeSheet = viewModel.showUpgradeSheet.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show error snackbar
    LaunchedEffect(error.value) {
        error.value?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
                viewModel.clearError()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // safeDrawingPadding handles status bar + nav bar + IME insets so
                // edge-to-edge screens never paint behind opaque chrome.
                .safeDrawingPadding(),
        ) {
            // AnimatedContent wraps the screen swap so navigation gets a real
            // transition (slide+fade) instead of an abrupt cut.
            AnimatedContent(
                targetState = screen.value,
                transitionSpec = {
                    val forward = isForwardTransition(initialState, targetState)
                    val direction = if (forward) 1 else -1
                    (slideInHorizontally(animationSpec = tween(280)) { it * direction } +
                        fadeIn(animationSpec = tween(220))) togetherWith
                        (slideOutHorizontally(animationSpec = tween(280)) { -it * direction } +
                            fadeOut(animationSpec = tween(220)))
                },
                label = "screen-transition",
            ) { current ->
                when (current) {
                    Screen.HOME -> {
                        HomeScreen(
                            isPro = isPro.value,
                            onPhotosSelected = { uris ->
                                viewModel.loadPhotos(uris)
                            },
                            onUpgradeClick = {
                                viewModel.launchBilling(activity)
                            },
                        )
                    }

                    Screen.PREVIEW -> {
                        PreviewScreen(
                            photos = photoList.value,
                            currentOptions = stripOptions.value,
                            isPro = isPro.value,
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
                        ProcessingScreen(progress = processingProgress.value)
                    }

                    Screen.DONE -> {
                        ResultsScreen(
                            results = stripResults.value,
                            onScanAgain = {
                                viewModel.reset()
                            },
                            onShare = {
                                viewModel.shareResults(activity)
                            },
                        )
                    }
                }
            }

            // Upgrade sheet
            if (showUpgradeSheet.value) {
                Dialog(
                    onDismissRequest = { viewModel.dismissUpgrade() },
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.32f))
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(0.7f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            // Close button
                            IconButton(
                                onClick = { viewModel.dismissUpgrade() },
                                modifier = Modifier.align(Alignment.End),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                )
                            }

                            Text(
                                text = "Unlock Pro",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )

                            // This is a stub; actual upgrade UI would go here
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

/**
 * Treat HOME -> PREVIEW -> PROCESSING -> DONE as forward; anything else
 * (Done -> Home etc.) slides back. Keeps the gesture intuitive without
 * pulling in a NavController.
 */
private fun isForwardTransition(from: Screen, to: Screen): Boolean {
    val order = listOf(Screen.HOME, Screen.PREVIEW, Screen.PROCESSING, Screen.DONE)
    val a = order.indexOf(from)
    val b = order.indexOf(to)
    return if (a < 0 || b < 0) true else b > a
}

@Composable
private fun ProcessingScreen(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 3.dp,
            )

            Text(
                text = "Stripping metadata",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.padding(horizontal = 32.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackgroundVariant,
            )
        }
    }
}
