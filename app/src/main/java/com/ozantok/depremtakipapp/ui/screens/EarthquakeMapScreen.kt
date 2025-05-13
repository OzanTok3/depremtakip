package com.ozantok.depremtakipapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import com.ozantok.depremtakipapp.ui.theme.HighMagnitude
import com.ozantok.depremtakipapp.ui.theme.LowMagnitude
import com.ozantok.depremtakipapp.ui.theme.MediumMagnitude

@Composable
fun EarthquakeMapScreen(
    earthquakes: List<EarthquakeResponse>,
    isLoading: Boolean,
    error: String?
) {
    val turkeyLatLng = LatLng(39.0, 35.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(turkeyLatLng, 5f)
    }
    var selectedEarthquake by remember { mutableStateOf<EarthquakeResponse?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false,
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
                    onClick = {
                        selectedEarthquake = earthquake
                        true
                    },
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

        selectedEarthquake?.let { earthquake ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Büyüklük: ${earthquake.magnitude}",
                        style = MaterialTheme.typography.titleMedium,
                        color = when {
                            earthquake.magnitude >= 5.0 -> HighMagnitude
                            earthquake.magnitude >= 4.0 -> MediumMagnitude
                            else -> LowMagnitude
                        }
                    )
                    Text(text = "Konum: ${earthquake.location}")
                    Text(text = "Derinlik: ${earthquake.depth} km")
                    Text(text = "Tarih: ${earthquake.date} ${earthquake.time}")
                }
            }
        }
    }
}
