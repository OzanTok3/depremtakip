package com.ozantok.depremtakipapp.data.remote



import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Locale

class EarthquakeAdapter : JsonDeserializer<List<EarthquakeResponse>> {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale("tr"))
    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale("tr"))

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<EarthquakeResponse> {
        val earthquakes = mutableListOf<EarthquakeResponse>()


        val jsonArray = when {
            json?.isJsonArray == true -> json.asJsonArray
            json?.isJsonObject == true && json.asJsonObject.has("result") ->
                json.asJsonObject.get("result").asJsonArray
            json?.isJsonObject == true && json.asJsonObject.has("data") ->
                json.asJsonObject.get("data").asJsonArray
            else -> null
        }

        jsonArray?.forEach { element ->
            if (element.isJsonObject) {
                val obj = element.asJsonObject
                try {

                    val eventId = obj.get("eventID")?.asString
                        ?: obj.get("id")?.asString
                        ?: obj.get("earthquake_id")?.asString
                        ?: ""

                    val location = obj.get("lokasyon")?.asString
                        ?: obj.get("location")?.asString
                        ?: obj.get("title")?.asString
                        ?: obj.get("region")?.asString
                        ?: ""

                    val latitude = obj.get("lat")?.asDouble
                        ?: obj.get("latitude")?.asDouble
                        ?: obj.get("geolocation")?.asJsonObject?.get("lat")?.asDouble
                        ?: 0.0

                    val longitude = obj.get("lng")?.asDouble
                        ?: obj.get("longitude")?.asDouble
                        ?: obj.get("lon")?.asDouble
                        ?: obj.get("geolocation")?.asJsonObject?.get("lng")?.asDouble
                        ?: 0.0

                    val depth = obj.get("depth")?.asDouble
                        ?: obj.get("derinlik")?.asDouble
                        ?: obj.get("depht")?.asDouble
                        ?: 0.0

                    val magnitude = obj.get("mag")?.asDouble
                        ?: obj.get("magnitude")?.asDouble
                        ?: obj.get("ml")?.asDouble
                        ?: 0.0

                    // Tarih ve zaman alanlarını kontrol edelim
                    val date = obj.get("date")?.asString
                        ?: obj.get("tarih")?.asString
                        ?: obj.get("date_time")?.asString?.split(" ")?.getOrNull(0)
                        ?: ""

                    val time = obj.get("time")?.asString
                        ?: obj.get("saat")?.asString
                        ?: obj.get("date_time")?.asString?.split(" ")?.getOrNull(1)
                        ?: ""

                    val earthquake = EarthquakeResponse(
                        eventId = eventId,
                        location = location,
                        latitude = latitude,
                        longitude = longitude,
                        depth = depth,
                        magnitude = magnitude,
                        date = date,
                        time = time
                    )
                    earthquakes.add(earthquake)
                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }
        }

        return earthquakes.sortedByDescending { it.magnitude }
    }
}