package com.ozantok.depremtakipapp.util


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object LocationUtils {

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(context: Context): Location? {
        val fusedClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        return suspendCancellableCoroutine { continuation ->
            fusedClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }

    fun calculateDistance(
        userLat: Double,
        userLon: Double,
        quakeLat: Double,
        quakeLon: Double
    ): Float {
        val userLocation = Location("").apply {
            latitude = userLat
            longitude = userLon
        }

        val quakeLocation = Location("").apply {
            latitude = quakeLat
            longitude = quakeLon
        }

        return userLocation.distanceTo(quakeLocation) / 1000f // km cinsinden
    }
}