package com.katafract.exifarmor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.LocalIndication

/**
 * Haptic style aligned with iOS `KataHaptic` (light/medium/success).
 * Compose only exposes two `HapticFeedbackType`s on Android pre-13,
 * so we map by intent.
 */
enum class KataHaptic {
    /** Tap on a primary CTA, save, share. */
    Confirm,
    /** Tap on a tab/chip/secondary toggle. */
    Light,
    /** Toggle / picker change. */
    Selection,
}

fun HapticFeedback.fire(haptic: KataHaptic) {
    val type = when (haptic) {
        KataHaptic.Confirm -> HapticFeedbackType.LongPress
        KataHaptic.Light -> HapticFeedbackType.TextHandleMove
        KataHaptic.Selection -> HapticFeedbackType.TextHandleMove
    }
    performHapticFeedback(type)
}

/**
 * Drop-in for `Modifier.clickable` that fires a haptic before the lambda.
 * Use everywhere a primary CTA, save, or destructive action is bound.
 */
@Suppress("ModifierFactoryUnreferencedReceiver")
fun Modifier.hapticClickable(
    enabled: Boolean = true,
    haptic: KataHaptic = KataHaptic.Confirm,
    onClickLabel: String? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val feedback = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    this.clickable(
        interactionSource = interaction,
        indication = LocalIndication.current,
        enabled = enabled,
        onClickLabel = onClickLabel,
    ) {
        feedback.fire(haptic)
        onClick()
    }
}

/**
 * Wrap a click lambda so it fires a haptic first. Useful for `Button(onClick = ...)`
 * call sites where we don't want to swap to `hapticClickable`.
 */
@Composable
fun rememberHapticClick(
    haptic: KataHaptic = KataHaptic.Confirm,
    onClick: () -> Unit,
): () -> Unit {
    val feedback = LocalHapticFeedback.current
    return remember(onClick, haptic) {
        {
            feedback.fire(haptic)
            onClick()
        }
    }
}
