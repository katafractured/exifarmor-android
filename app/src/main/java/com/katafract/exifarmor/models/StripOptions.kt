package com.katafract.exifarmor.models

data class StripOptions(
    val removeLocation: Boolean = true,
    val removeDateTime: Boolean = true,
    val removeDeviceInfo: Boolean = true,
    val removeCameraSettings: Boolean = false,
    val removeAll: Boolean = false,
) {
    companion object {
        val ALL = StripOptions(
            removeLocation = true,
            removeDateTime = true,
            removeDeviceInfo = true,
            removeCameraSettings = true,
            removeAll = true,
        )

        val LOCATION_ONLY = StripOptions(
            removeLocation = true,
            removeDateTime = false,
            removeDeviceInfo = false,
            removeCameraSettings = false,
            removeAll = false,
        )

        val PRIVACY_FOCUSED = StripOptions(
            removeLocation = true,
            removeDateTime = true,
            removeDeviceInfo = true,
            removeCameraSettings = false,
            removeAll = false,
        )
    }
}
