package com.ozantok.depremtakipapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import com.ozantok.depremtakipapp.ui.theme.HighMagnitude
import com.ozantok.depremtakipapp.ui.theme.LowMagnitude
import com.ozantok.depremtakipapp.ui.theme.MediumMagnitude
import kotlinx.coroutines.tasks.await

@Composable
fun EarthquakeMapScreen(
    earthquakes: List<EarthquakeResponse>,
    isLoading: Boolean,
    error: String?
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var locationZoomDone by remember { mutableStateOf(false) }

    // 1. Konum izni kontrolü
    LaunchedEffect(Unit) {
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 2. Eğer izin verildiyse kullanıcı konumuna zoom yap
    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted && !locationZoomDone) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
                ).await()

                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLatLng, 7f))
                    locationZoomDone = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionGranted,
                mapType = MapType.NORMAL
            )
        ) {
            earthquakes.forEach { earthquake ->
                val position = LatLng(earthquake.latitude, earthquake.longitude)
                val markerColor = when {
                    earthquake.magnitude >= 5.0 -> HighMagnitude
                    earthquake.magnitude >= 4.0 -> MediumMagnitude
                    else -> LowMagnitude
                }

                Marker(
                    state = MarkerState(position = position),
                    title = "${earthquake.magnitude} - ${earthquake.location}",
                    snippet = "Derinlik: ${earthquake.depth} km, Tarih: ${earthquake.date} ${earthquake.time}",
                    icon = BitmapDescriptorFactory.defaultMarker(
                        when (markerColor) {
                            HighMagnitude -> BitmapDescriptorFactory.HUE_RED
                            MediumMagnitude -> BitmapDescriptorFactory.HUE_ORANGE
                            else -> BitmapDescriptorFactory.HUE_GREEN
                        }
                    )
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}