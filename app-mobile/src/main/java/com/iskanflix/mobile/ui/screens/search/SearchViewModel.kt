package com.iskanflix.mobile.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskanflix.data.model.SearchResultItem
import com.iskanflix.data.repository.IskanFlixRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<SearchResultItem> = emptyList(),
    val error: String? = null
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: IskanFlixRepository = IskanFlixRepository()
) : ViewModel() {

    private val _queryFlow = MutableStateFlow("")
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        _queryFlow
            .debounce(400)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.trim().length >= 2) {
                    performSearch(query.trim())
                } else {
                    _uiState.value = _uiState.value.copy(isSearching = false, results = emptyList())
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(newQuery: String) {
        _queryFlow.value = newQuery
        _uiState.value = _uiState.value.copy(query = newQuery)
    }

    fun clearQuery() {
        onQueryChange("")
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, error = null)
            try {
                val results = repository.search(query)
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    results = results,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = e.localizedMessage ?: "Search failed"
                )
            }
        }
    }
}
