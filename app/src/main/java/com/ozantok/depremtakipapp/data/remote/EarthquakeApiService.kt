package com.ozantok.depremtakipapp.data.remote

import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EarthquakeApiService {
    @GET("eqlist.php")  // Kandilli Rasathanesi benzeri bir API endpoint'i
    suspend fun getLastEarthquakes(
        @Query("limit") limit: Int = 100  // Son 100 depremi alalım
    ): List<EarthquakeResponse>
}
