package com.ozantok.depremtakipapp.worker

import android.content.Context
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ozantok.depremtakipapp.data.remote.EarthquakeApiService
import com.ozantok.depremtakipapp.util.LocationUtils
import com.ozantok.depremtakipapp.util.NotificationUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@HiltWorker
class EarthquakeWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val api: EarthquakeApiService
) : CoroutineWorker(context, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val userLocation: Location? = LocationUtils.getLastKnownLocation(context)

            if (userLocation != null) {
                val now = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val start = now.minusHours(6).format(formatter)
                val end = now.format(formatter)

                val earthquakes = api.getEarthquakesFromAfad(start, end)

                earthquakes.forEach { earthquake ->
                    val distance = LocationUtils.calculateDistance(
                        userLocation.latitude,
                        userLocation.longitude,
                        earthquake.latitude,
                        earthquake.longitude
                    )

                    if (distance <= 300f) {
                        val message = """
                            ${earthquake.magnitude} büyüklüğünde bir deprem meydana geldi.
                            📍 Konum: ${earthquake.location}
                            📏 Uzaklık: ${distance.toInt()} km
                            🕒 Tarih: ${earthquake.date} ${earthquake.time}
                        """.trimIndent()

                        NotificationUtil.showEarthquakeNotification(context, message, earthquake)
                        return@withContext Result.success()
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
