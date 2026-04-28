package com.katafract.exifarmor.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Katafract / ExifArmor brand color tokens.
 * Mirrors the iOS `KataStyle.swift` palette so the two apps look like one product.
 *
 * iOS source of truth:
 *   katafractured/exifarmor-ios/ExifArmor/ExifArmor/Extensions/KataStyle.swift
 *   katafractured/exifarmor-ios/ExifArmor/ExifArmor/Extensions/Color+Theme.swift
 *
 * If the iOS palette evolves, update both.
 */

// --- Kata brand palette (mirrors iOS `kata*`) ---
val KataGold       = Color(0xFFC69838) // 0.776, 0.596, 0.220 — primary accent
val KataSapphire   = Color(0xFF1C3366)
val KataIce        = Color(0xFFEBF0F7) // 0.920, 0.940, 0.970 — text on dark
val KataMidnight   = Color(0xFF0A0A14) // 0.040, 0.040, 0.080 — primary background
val KataChampagne  = Color(0xFFF5E3C7)
val KataNavy       = Color(0xFF0F1A2E)

// --- Surface tiers (cards, sheets, dividers) ---
val SurfaceDark        = Color(0xFF14141C) // CardBackground from Color+Theme.swift
val SurfaceDarkElev    = Color(0xFF1B1B26)
val OutlineDark        = Color(0xFF2A2A38)
val TextSecondaryDark  = Color(0xFF8C94A6) // TextSecondary from Color+Theme.swift

// --- Light tier ---
val SurfaceLight        = Color(0xFFFFFFFF)
val SurfaceLightVariant = Color(0xFFF5F1EA) // warm off-white, gold-tinted
val BackgroundLight     = Color(0xFFFAF8F4)
val OutlineLight        = Color(0xFFE3DDD0)
val OnSurfaceLight      = Color(0xFF1F1A12)
val TextSecondaryLight  = Color(0xFF6B6356)

// --- Status colors (consistent across both schemes) ---
val SuccessGreen = Color(0xFF33E680)
val WarningAmber = Color(0xFFFFB23A)
val DangerRed    = Color(0xFFFF4040)
