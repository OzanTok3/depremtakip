package com.ozantok.depremtakipapp.data.remote

import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import retrofit2.http.GET

interface EarthquakeApiService {
    @GET("last-earthquakes.html")
    suspend fun getLastEarthquakes(): List<EarthquakeResponse>
}