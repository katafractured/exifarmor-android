package com.katafract.exifarmor.services

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.katafract.exifarmor.models.PhotoMetadata
import kotlin.math.abs

object MetadataService {

    fun readMetadata(context: Context, uri: Uri): PhotoMetadata? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val exif = ExifInterface(inputStream)
            inputStream.close()

            val filename = uri.lastPathSegment ?: "photo"

            // GPS coordinates
            val latitude = exif.getLatLong()?.get(0)
            val longitude = exif.getLatLong()?.get(1)
            val altitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_ALTITUDE, Double.NaN)
                .let { if (it.isNaN()) null else it }

            // Device info
            val deviceMake = exif.getAttribute(ExifInterface.TAG_MAKE)
            val deviceModel = exif.getAttribute(ExifInterface.TAG_MODEL)
            val software = exif.getAttribute(ExifInterface.TAG_SOFTWARE)

            // DateTime
            val dateTimeOriginal = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            val dateTimeDigitized = exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)

            // Camera settings
            val focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
            val aperture = exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE)
            val exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
            val iso = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS)

            // Image dimensions
            val pixelWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0).takeIf { it > 0 }
            val pixelHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0).takeIf { it > 0 }

            PhotoMetadata(
                uri = uri,
                filename = filename,
                latitude = latitude,
                longitude = longitude,
                altitude = altitude,
                deviceMake = deviceMake,
                deviceModel = deviceModel,
                software = software,
                dateTimeOriginal = dateTimeOriginal,
                dateTimeDigitized = dateTimeDigitized,
                focalLength = focalLength,
                aperture = aperture,
                exposureTime = exposureTime,
                iso = iso,
                pixelWidth = pixelWidth,
                pixelHeight = pixelHeight,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
