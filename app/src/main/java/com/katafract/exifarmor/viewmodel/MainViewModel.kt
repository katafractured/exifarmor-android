package com.katafract.exifarmor.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.katafract.exifarmor.models.PhotoMetadata
import com.katafract.exifarmor.models.StripOptions
import com.katafract.exifarmor.models.StripResult
import com.katafract.exifarmor.services.MetadataService
import com.katafract.exifarmor.services.StripService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    PREVIEW,
    PROCESSING,
    DONE,
}

class MainViewModel(application: android.app.Application) : AndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    private val _screen = MutableStateFlow(Screen.HOME)
    val screen: StateFlow<Screen> = _screen

    private val _photoList = MutableStateFlow<List<PhotoMetadata>>(emptyList())
    val photoList: StateFlow<List<PhotoMetadata>> = _photoList

    private val _stripResults = MutableStateFlow<List<StripResult>>(emptyList())
    val stripResults: StateFlow<List<StripResult>> = _stripResults

    private val _stripOptions = MutableStateFlow(StripOptions.PRIVACY_FOCUSED)
    val stripOptions: StateFlow<StripOptions> = _stripOptions

    private val _processingProgress = MutableStateFlow(0f)
    val processingProgress: StateFlow<Float> = _processingProgress

    fun loadPhotos(uris: List<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            val metadata = uris.mapNotNull { uri ->
                MetadataService.readMetadata(context, uri)
            }
            _photoList.emit(metadata)
            _screen.emit(Screen.PREVIEW)
        }
    }

    fun updateOptions(options: StripOptions) {
        viewModelScope.launch {
            _stripOptions.emit(options)
        }
    }

    fun startStrip() {
        viewModelScope.launch(Dispatchers.IO) {
            _screen.emit(Screen.PROCESSING)
            _processingProgress.emit(0f)

            val photos = _photoList.value
            val options = _stripOptions.value
            val results = mutableListOf<StripResult>()

            for ((index, metadata) in photos.withIndex()) {
                val result = StripService.strip(context, metadata.uri, metadata, options)
                results.add(result)
                _processingProgress.emit((index + 1f) / photos.size)
            }

            _stripResults.emit(results)
            _screen.emit(Screen.DONE)
        }
    }

    fun shareResults(context: Context) {
        // TODO: Implement sharing of cleaned images
    }

    fun reset() {
        viewModelScope.launch {
            _photoList.emit(emptyList())
            _stripResults.emit(emptyList())
            _screen.emit(Screen.HOME)
            _processingProgress.emit(0f)
        }
    }
}
