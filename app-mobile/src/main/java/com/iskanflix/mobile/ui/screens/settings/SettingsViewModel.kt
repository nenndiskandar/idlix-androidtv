package com.iskanflix.mobile.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.disk.DiskCache
import com.iskanflix.data.api.ApiClient
import com.iskanflix.data.BuildConfig
import com.iskanflix.data.repository.IskanFlixRepository
import com.iskanflix.data.repository.SubdlRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SettingsUiState(
    val backendUrl: String = "",
    val subdlKey: String = "",
    val isTestingBackend: Boolean = false,
    val backendTestResult: String? = null,
    val isTestingSubdl: Boolean = false,
    val subdlTestResult: String? = null,
    val cacheCleared: Boolean = false
)

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = IskanFlixRepository()

    /**
     * Release builds pin the backend URL and SubDL key. The Settings screen hides
     * the editable fields and the repository ignores stored overrides.
     */
    val settingsLocked: Boolean = BuildConfig.SETTINGS_LOCKED

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            backendUrl = ApiClient.getBaseUrl(),
            subdlKey = SubdlRepository.getApiKey()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateBackendUrl(url: String) {
        if (settingsLocked) return
        _uiState.value = _uiState.value.copy(backendUrl = url, backendTestResult = null)
    }

    fun updateSubdlKey(key: String) {
        if (settingsLocked) return
        _uiState.value = _uiState.value.copy(subdlKey = key, subdlTestResult = null)
    }

    fun saveBackendUrl() {
        if (settingsLocked) return
        ApiClient.setBaseUrl(_uiState.value.backendUrl)
    }

    fun saveSubdlKey() {
        if (settingsLocked) return
        SubdlRepository.setApiKey(_uiState.value.subdlKey)
    }

    fun testBackend() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTestingBackend = true, backendTestResult = null)
            try {
                val start = System.currentTimeMillis()
                val response = repository.getMovies(page = 1, limit = 1)
                val duration = System.currentTimeMillis() - start
                _uiState.value = _uiState.value.copy(
                    isTestingBackend = false,
                    backendTestResult = "Connected successfully (${duration}ms, ${response.data.size} items)"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isTestingBackend = false,
                    backendTestResult = "Failed: ${e.localizedMessage}"
                )
            }
        }
    }

    fun testSubdl() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTestingSubdl = true, subdlTestResult = null)
            try {
                val result = SubdlRepository.searchAllSubtitles(title = "Avatar", type = "movie")
                _uiState.value = _uiState.value.copy(
                    isTestingSubdl = false,
                    subdlTestResult = "SubDL API key is valid (${result.size} subtitles found)"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isTestingSubdl = false,
                    subdlTestResult = "Failed: ${e.localizedMessage}"
                )
            }
        }
    }

    fun clearImageCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            try {
                val cacheDir = context.cacheDir.resolve("image_cache")
                if (cacheDir.exists()) {
                    cacheDir.deleteRecursively()
                }
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(cacheCleared = true)
                }
            } catch (_: Exception) {}
        }
    }
}
