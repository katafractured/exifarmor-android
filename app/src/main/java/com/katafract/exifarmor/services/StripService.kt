package com.katafract.exifarmor.services

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import com.katafract.exifarmor.models.PhotoMetadata
import com.katafract.exifarmor.models.StripOptions
import com.katafract.exifarmor.models.StripResult
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object StripService {

    fun strip(
        context: Context,
        sourceUri: Uri,
        originalMetadata: PhotoMetadata,
        options: StripOptions,
    ): StripResult {
        return try {
            // Read source bytes
            val sourceBytes = context.contentResolver.openInputStream(sourceUri)?.readBytes()
                ?: return StripResult(
                    originalUri = sourceUri,
                    cleanUri = null,
                    originalMetadata = originalMetadata,
                    success = false,
                    error = "Failed to read source file",
                )

            // Create temp file
            val cacheDir = context.cacheDir
            val tempFile = File.createTempFile("exif_", ".jpg", cacheDir)
            tempFile.writeBytes(sourceBytes)

            // Strip EXIF
            val fieldsRemoved = stripExif(tempFile, options)

            // Save to MediaStore
            val cleanUri = saveToMediaStore(context, tempFile, originalMetadata)

            // Cleanup temp file
            tempFile.delete()

            StripResult(
                originalUri = sourceUri,
                cleanUri = cleanUri,
                originalMetadata = originalMetadata,
                fieldsRemoved = fieldsRemoved,
                success = cleanUri != null,
                error = if (cleanUri == null) "Failed to save cleaned image" else null,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            StripResult(
                originalUri = sourceUri,
                cleanUri = null,
                originalMetadata = originalMetadata,
                success = false,
                error = e.message ?: "Unknown error",
            )
        }
    }

    private fun stripExif(file: File, options: StripOptions): Int {
        var fieldsRemoved = 0

        try {
            val exif = ExifInterface(file)

            if (options.removeAll) {
                // Remove all attributes
                val allTags = listOf(
                    ExifInterface.TAG_GPS_LATITUDE,
                    ExifInterface.TAG_GPS_LONGITUDE,
                    ExifInterface.TAG_GPS_ALTITUDE,
                    ExifInterface.TAG_GPS_LATITUDE_REF,
                    ExifInterface.TAG_GPS_LONGITUDE_REF,
                    ExifInterface.TAG_GPS_ALTITUDE_REF,
                    ExifInterface.TAG_GPS_TIMESTAMP,
                    ExifInterface.TAG_GPS_DATESTAMP,
                    ExifInterface.TAG_DATETIME,
                    ExifInterface.TAG_DATETIME_ORIGINAL,
                    ExifInterface.TAG_DATETIME_DIGITIZED,
                    ExifInterface.TAG_MAKE,
                    ExifInterface.TAG_MODEL,
                    ExifInterface.TAG_SOFTWARE,
                    ExifInterface.TAG_LENS_MODEL,
                    ExifInterface.TAG_FOCAL_LENGTH,
                    ExifInterface.TAG_APERTURE_VALUE,
                    ExifInterface.TAG_EXPOSURE_TIME,
                    ExifInterface.TAG_ISO_SPEED_RATINGS,
                )
                for (tag in allTags) {
                    if (exif.getAttribute(tag) != null) {
                        exif.setAttribute(tag, null)
                        fieldsRemoved++
                    }
                }
            } else {
                // Remove selectively
                if (options.removeLocation) {
                    val gpsTags = listOf(
                        ExifInterface.TAG_GPS_LATITUDE,
                        ExifInterface.TAG_GPS_LONGITUDE,
                        ExifInterface.TAG_GPS_ALTITUDE,
                        ExifInterface.TAG_GPS_LATITUDE_REF,
                        ExifInterface.TAG_GPS_LONGITUDE_REF,
                        ExifInterface.TAG_GPS_ALTITUDE_REF,
                        ExifInterface.TAG_GPS_TIMESTAMP,
                        ExifInterface.TAG_GPS_DATESTAMP,
                    )
                    for (tag in gpsTags) {
                        if (exif.getAttribute(tag) != null) {
                            exif.setAttribute(tag, null)
                            fieldsRemoved++
                        }
                    }
                }

                if (options.removeDateTime) {
                    val dateTags = listOf(
                        ExifInterface.TAG_DATETIME,
                        ExifInterface.TAG_DATETIME_ORIGINAL,
                        ExifInterface.TAG_DATETIME_DIGITIZED,
                    )
                    for (tag in dateTags) {
                        if (exif.getAttribute(tag) != null) {
                            exif.setAttribute(tag, null)
                            fieldsRemoved++
                        }
                    }
                }

                if (options.removeDeviceInfo) {
                    val deviceTags = listOf(
                        ExifInterface.TAG_MAKE,
                        ExifInterface.TAG_MODEL,
                        ExifInterface.TAG_SOFTWARE,
                        ExifInterface.TAG_LENS_MODEL,
                    )
                    for (tag in deviceTags) {
                        if (exif.getAttribute(tag) != null) {
                            exif.setAttribute(tag, null)
                            fieldsRemoved++
                        }
                    }
                }

                if (options.removeCameraSettings) {
                    val cameraTags = listOf(
                        ExifInterface.TAG_FOCAL_LENGTH,
                        ExifInterface.TAG_APERTURE_VALUE,
                        ExifInterface.TAG_EXPOSURE_TIME,
                        ExifInterface.TAG_ISO_SPEED_RATINGS,
                    )
                    for (tag in cameraTags) {
                        if (exif.getAttribute(tag) != null) {
                            exif.setAttribute(tag, null)
                            fieldsRemoved++
                        }
                    }
                }
            }

            exif.saveAttributes()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return fieldsRemoved
    }

    private fun saveToMediaStore(context: Context, file: File, metadata: PhotoMetadata): Uri? {
        return try {
            val filename = generateCleanFilename(metadata.filename)
            val mimeType = "image/jpeg"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ExifArmor")
                }
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues,
            ) ?: return null

            context.contentResolver.openOutputStream(uri)?.use { output ->
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateCleanFilename(original: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val extension = original.substringAfterLast(".", "jpg")
        return "EXIF_CLEAN_${timestamp}.$extension"
    }
}
