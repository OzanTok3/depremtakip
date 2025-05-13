package com.ozantok.depremtakipapp.data.repository



import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import com.ozantok.depremtakipapp.data.remote.EarthquakeApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EarthquakeRepository @Inject constructor(
    private val earthquakeApiService: EarthquakeApiService
) {
    fun getLastEarthquakes(): Flow<List<EarthquakeResponse>> = flow {
        try {
            val earthquakes = earthquakeApiService.getLastEarthquakes()
            emit(earthquakes)
        } catch (e: Exception) {
            // Hatayı loglayalım
            e.printStackTrace()
            // Boş liste döndürelim
            emit(emptyList())
        }
    }
}