package com.ozantok.depremtakipapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import com.ozantok.depremtakipapp.data.repository.EarthquakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EarthquakeViewModel @Inject constructor(
    private val repository: EarthquakeRepository
) : ViewModel() {

    private val _earthquakes = MutableStateFlow<List<EarthquakeResponse>>(emptyList())
    val earthquakes: StateFlow<List<EarthquakeResponse>> = _earthquakes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        getEarthquakes()
    }

    fun getEarthquakes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.getLastEarthquakes().collectLatest { earthquakeList ->
                    _earthquakes.value = earthquakeList
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Deprem verileri yüklenirken bir hata oluştu"
            } finally {
                _isLoading.value = false
            }
        }
    }
}