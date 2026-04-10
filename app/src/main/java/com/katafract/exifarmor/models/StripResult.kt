package com.katafract.exifarmor.models

import android.net.Uri

data class StripResult(
    val originalUri: Uri,
    val cleanUri: Uri? = null,
    val originalMetadata: PhotoMetadata,
    val fieldsRemoved: Int = 0,
    val success: Boolean = false,
    val error: String? = null,
)
