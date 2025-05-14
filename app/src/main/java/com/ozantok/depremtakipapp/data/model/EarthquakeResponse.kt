package com.ozantok.depremtakipapp.data.model


import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class EarthquakeResponse(
    @SerializedName("eventID")
    val eventId: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("depth")
    val depth: Double,
    @SerializedName("magnitude")
    val magnitude: Double,
    @SerializedName("date")
    val date: String,
    @SerializedName("time")
    val time: String
) {
    // Formatlanmış tarih ve saat döndüren fonksiyon
    fun getFormattedDateTime(): String {
        try {
            var dateStr = date
            var timeStr = time

            // ISO formatı kontrolü (2025-05-14T07:07:39)
            if (dateStr.contains("T")) {
                val parts = dateStr.split("T")
                if (parts.size == 2) {
                    dateStr = parts[0]
                    timeStr = parts[1]
                }
            }

            // Tarih bileşenlerini ayır
            val dateComponents = dateStr.replace(".", "-").split("-")
            if (dateComponents.size != 3) return "$date $time"

            val year = dateComponents[0]
            val month = dateComponents[1]
            val day = dateComponents[2]

            // Saat bileşenlerini ayır
            val timeComponents = timeStr.split(":")
            if (timeComponents.isEmpty()) return "$date $time"

            var hour = timeComponents[0].toIntOrNull() ?: 0
            val minute = if (timeComponents.size > 1) timeComponents[1] else "00"

            // Saate 3 saat ekle
            hour = (hour + 3) % 24

            // Formatlanmış tarih ve saati döndür
            return "$day/$month/$year $hour:$minute"
        } catch (e: Exception) {
            // Hata durumunda orijinal tarih ve saati döndür
            return "$date $time"
        }
    }

    companion object {
        // HTML tablosundan deprem verisi oluşturmak için yardımcı fonksiyon
        fun fromHtmlRow(row: String): EarthquakeResponse? {
            try {
                // HTML tablosu satırını parçalara ayır
                val parts = row.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
                if (parts.size < 9) return null

                // Format: Tarih Saat Enlem Boylam Derinlik MD ML MW Yer
                val dateStr = parts[0]
                val timeStr = parts[1]
                val latitude = parts[2].toDoubleOrNull() ?: 0.0
                val longitude = parts[3].toDoubleOrNull() ?: 0.0
                val depth = parts[4].toDoubleOrNull() ?: 0.0

                // Büyüklükler (ML kullanılacak, eğer yoksa MD)
                val magnitudeIndex = 6 // ML genellikle 6. indekste
                val magnitude = parts[magnitudeIndex].toDoubleOrNull() ?:
                parts[magnitudeIndex-1].toDoubleOrNull() ?: 0.0

                // Yer bilgisi (geriye kalan tüm parçalar)
                val location = parts.subList(8, parts.size).joinToString(" ")

                return EarthquakeResponse(
                    eventId = "$dateStr-$timeStr-$latitude-$longitude",
                    location = location,
                    latitude = latitude,
                    longitude = longitude,
                    depth = depth,
                    magnitude = magnitude,
                    date = dateStr,
                    time = timeStr
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}