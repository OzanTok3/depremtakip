package com.ozantok.depremtakipapp.data.remote

import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import okhttp3.ResponseBody
import retrofit2.http.GET

interface EarthquakeApiService {
    // Kandilli Rasathanesi'nin web sayfasını raw HTML olarak alalım
    @GET("scripts/sondepremler.asp")
    suspend fun getLastEarthquakesHtml(): ResponseBody
}