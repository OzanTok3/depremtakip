package com.ozantok.depremtakipapp.data.repository


import android.util.Log
import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import com.ozantok.depremtakipapp.data.remote.EarthquakeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EarthquakeRepository @Inject constructor(
    private val earthquakeApiService: EarthquakeApiService
) {
    private val TAG = "EarthquakeRepository"

    // AFAD API'sinden deprem verilerini alacak yeni fonksiyon
    fun getEarthquakesFromAfad(): Flow<List<EarthquakeResponse>> = flow {
        try {
            Log.d(TAG, "AFAD API'sinden veri alma işlemi başlatılıyor...")

            // AFAD API'sinden deprem verilerini al
            // Türkiye'yi kapsayan koordinatlar
            val earthquakes = earthquakeApiService.getEarthquakesFromAfad(
                minLat = 35.0,
                maxLat = 43.0,
                minLon = 25.0,
                maxLon = 45.0,
                orderBy = "magnitude"
            )

            Log.d(TAG, "AFAD API'sinden veri başarıyla alındı, deprem sayısı: ${earthquakes.size}")
            if (earthquakes.isNotEmpty()) {
                Log.d(TAG, "İlk deprem: ${earthquakes.first().location}, ${earthquakes.first().magnitude}")
            }

            emit(earthquakes)
        } catch (e: Exception) {
            Log.e(TAG, "AFAD API'sinden deprem verileri alınırken hata oluştu: ${e.message}")
            Log.e(TAG, "Hata detayı:", e)
            emit(emptyList())
        }
    }

    // Eski Kandilli Rasathanesi veri alma fonksiyonu (yedek olarak tutulabilir)
    fun getLastEarthquakes(): Flow<List<EarthquakeResponse>> = flow {
        try {
            Log.d(TAG, "Kandilli Rasathanesi'nden veri alma işlemi başlatılıyor...")

            // HTML içeriğini al
            val response = earthquakeApiService.getLastEarthquakesHtml()
            val htmlContent = response.string()

            // IO thread'de HTML parsing işlemi yap
            val earthquakes = withContext(Dispatchers.IO) {
                parseHtmlContent(htmlContent)
            }

            Log.d(TAG, "Veri başarıyla alındı, deprem sayısı: ${earthquakes.size}")
            if (earthquakes.isNotEmpty()) {
                Log.d(TAG, "İlk deprem: ${earthquakes.first().location}, ${earthquakes.first().magnitude}")
            }

            emit(earthquakes)
        } catch (e: Exception) {
            Log.e(TAG, "Deprem verileri alınırken hata oluştu: ${e.message}")
            Log.e(TAG, "Hata detayı:", e)
            emit(emptyList())
        }
    }

    private fun parseHtmlContent(htmlContent: String): List<EarthquakeResponse> {
        val earthquakes = mutableListOf<EarthquakeResponse>()

        try {
            // Jsoup ile HTML içeriğini parse et
            val document = Jsoup.parse(htmlContent)

            // Tablo satırlarını bul (Kandilli Rasathanesi'nin sayfası pre tag'i içinde tablo formatında veri sunuyor)
            val preElement = document.select("pre").firstOrNull()

            preElement?.let { pre ->
                // pre içindeki metni satırlara böl
                val lines = pre.text().split("\n")

                // Header satırlarını atla (genellikle ilk 6 satır)
                val dataLines = lines.drop(6)

                dataLines.forEach { line ->
                    if (line.trim().isNotEmpty()) {
                        EarthquakeResponse.fromHtmlRow(line)?.let { earthquake ->
                            earthquakes.add(earthquake)
                        }
                    }
                }
            }

            // Büyüklüğe göre sırala (azalan)
            return earthquakes.sortedByDescending { it.magnitude }
        } catch (e: Exception) {
            Log.e(TAG, "HTML parsing hatası: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }
}