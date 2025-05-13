package com.ozantok.depremtakipapp.data.remote


import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface EarthquakeApiService {
    // Kandilli Rasathanesi'nin web sayfasını raw HTML olarak alalım (Eski yöntem)
    @GET("scripts/sondepremler.asp")
    suspend fun getLastEarthquakesHtml(): ResponseBody

    // AFAD API entegrasyonu
    @GET("apiv2/event/filter")
    suspend fun getEarthquakesFromAfad(
        @Query("minlat") minLat: Double = 35.0,
        @Query("maxlat") maxLat: Double = 43.0,
        @Query("minlon") minLon: Double = 25.0,
        @Query("maxlon") maxLon: Double = 45.0,
        @Query("orderby") orderBy: String = "magnitude"
    ): List<EarthquakeResponse>
}