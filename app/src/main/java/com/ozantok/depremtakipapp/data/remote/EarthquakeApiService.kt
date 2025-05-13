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
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("orderby") orderBy: String = "timedesc",
        @Query("limit") limit: Int = 2000
    ): List<EarthquakeResponse>
}