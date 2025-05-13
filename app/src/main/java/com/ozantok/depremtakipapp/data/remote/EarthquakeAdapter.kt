package com.ozantok.depremtakipapp.data.remote


import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import java.lang.reflect.Type

class EarthquakeAdapter : JsonDeserializer<List<EarthquakeResponse>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<EarthquakeResponse> {
        // AFAD API'sinden gelen JSON'ı işleyerek EarthquakeResponse listesine çevirmek için
        // Gerçek API cevabını inceleyerek burayı daha kesin olarak yapılandırabiliriz
        val earthquakes = mutableListOf<EarthquakeResponse>()
        val jsonArray = json?.asJsonArray

        jsonArray?.forEach { element ->
            val obj = element.asJsonObject
            val earthquake = EarthquakeResponse(
                eventId = obj.get("eventID")?.asString ?: "",
                location = obj.get("location")?.asString ?: "",
                latitude = obj.get("latitude")?.asDouble ?: 0.0,
                longitude = obj.get("longitude")?.asDouble ?: 0.0,
                depth = obj.get("depth")?.asDouble ?: 0.0,
                magnitude = obj.get("magnitude")?.asDouble ?: 0.0,
                date = obj.get("date")?.asString ?: "",
                time = obj.get("time")?.asString ?: ""
            )
            earthquakes.add(earthquake)
        }

        return earthquakes
    }
}