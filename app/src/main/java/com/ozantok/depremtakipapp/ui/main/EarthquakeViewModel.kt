package com.ozantok.depremtakipapp.ui.main


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import com.ozantok.depremtakipapp.data.repository.EarthquakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EarthquakeViewModel @Inject constructor(
    private val repository: EarthquakeRepository
) : ViewModel() {
    private val TAG = "EarthquakeViewModel"

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

            Log.d(TAG, "AFAD API'sinden deprem verileri yükleniyor...")

            // AFAD API'sini kullanacak şekilde güncellendi
            repository.getEarthquakesFromAfad()
                .catch { exception ->
                    val errorMsg = exception.message ?: "Deprem verileri yüklenirken bir hata oluştu"
                    Log.e(TAG, "AFAD veri yüklemede hata: $errorMsg", exception)
                    _error.value = errorMsg
                    _isLoading.value = false

                    // AFAD API'si başarısız olursa, yedek olarak Kandilli'yi dene
                    Log.d(TAG, "AFAD API başarısız oldu, Kandilli yedek kaynağına geçiliyor...")
                    tryKandilliBackup()
                }
                .collectLatest { earthquakeList ->
                    Log.d(TAG, "AFAD deprem listesi alındı, boyut: ${earthquakeList.size}")
                    _earthquakes.value = earthquakeList.sortedByDescending { it.magnitude }
                    _isLoading.value = false
                }
        }
    }

    // AFAD API'si başarısız olursa Kandilli'den veri almayı deneyen yedek fonksiyon
    private fun tryKandilliBackup() {
        viewModelScope.launch {
            repository.getLastEarthquakes()
                .catch { exception ->
                    val errorMsg = exception.message ?: "Yedek kaynak da başarısız oldu"
                    Log.e(TAG, "Kandilli yedek verisi yüklemede hata: $errorMsg", exception)
                    _error.value = errorMsg
                    _isLoading.value = false
                }
                .collectLatest { earthquakeList ->
                    if (earthquakeList.isNotEmpty()) {
                        Log.d(TAG, "Kandilli yedek deprem listesi alındı, boyut: ${earthquakeList.size}")
                        _earthquakes.value = earthquakeList.sortedByDescending { it.magnitude }
                        _error.value = null // Yedek başarılı oldu, hatayı temizle
                    } else {
                        _error.value = "Her iki veri kaynağından da deprem verileri alınamadı"
                    }
                    _isLoading.value = false
                }
        }
    }
}