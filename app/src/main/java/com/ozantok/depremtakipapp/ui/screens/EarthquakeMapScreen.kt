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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    error: String?,
    showBanner: Boolean = true,
    focusedLat: Double? = null,
    focusedLon: Double? = null
) {
    val context = LocalContext.current
    val defaultLatLng = LatLng(39.9208, 32.8541) // Ankara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPositionState(
            position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(defaultLatLng, 6f)
        ).position
    }
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
                    // Kullanıcı konumuna daha yakın zoom yapılıyor (7f yerine 10f kullanıldı)
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLatLng, 10f))
                    locationZoomDone = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(focusedLat, focusedLon) {
        if (focusedLat != null && focusedLon != null) {
            val target = LatLng(focusedLat, focusedLon)
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(target, 10f))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Banner Reklam (Üstte) - eğer showBanner true ise göster
        if (showBanner) {
            BannerAdView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }

        // Harita
        Box(modifier = Modifier.weight(1f)) {
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

                    if (focusedLat != null && focusedLon != null) {
                        val focusedPosition = LatLng(focusedLat, focusedLon)
                        Marker(
                            state = MarkerState(position = focusedPosition),
                            title = "Deprem Noktası",
                            snippet = "Bildirime neden olan deprem burada meydana geldi.",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                    }
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
}

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                // Test Ad Unit ID - Gerçek uygulama için kendi AdMob ID'nizi kullanın
                adUnitId = "ca-app-pub-7783245066273815/9643715403"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}