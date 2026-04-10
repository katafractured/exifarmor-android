package com.katafract.exifarmor.models

import android.net.Uri

data class PhotoMetadata(
    val uri: Uri,
    val filename: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    val deviceMake: String? = null,
    val deviceModel: String? = null,
    val software: String? = null,
    val dateTimeOriginal: String? = null,
    val dateTimeDigitized: String? = null,
    val focalLength: String? = null,
    val aperture: String? = null,
    val exposureTime: String? = null,
    val iso: String? = null,
    val pixelWidth: Int? = null,
    val pixelHeight: Int? = null,
) {
    val hasGps: Boolean
        get() = latitude != null && longitude != null
}
