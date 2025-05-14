package com.ozantok.depremtakipapp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ozantok.depremtakipapp.R
import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import com.ozantok.depremtakipapp.ui.theme.HighMagnitude
import com.ozantok.depremtakipapp.ui.theme.LowMagnitude
import com.ozantok.depremtakipapp.ui.theme.MediumMagnitude

@Composable
fun EarthquakeListScreen(
    earthquakes: List<EarthquakeResponse>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isLoading)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (earthquakes.isEmpty() && !isLoading) {
                EmptyState(error)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(earthquakes) { earthquake ->
                        EarthquakeItem(earthquake)
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun EmptyState(error: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error ?: stringResource(R.string.no_earthquakes_found),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarthquakeItem(earthquake: EarthquakeResponse) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Badge(
                modifier = Modifier.size(48.dp),
                containerColor = when {
                    earthquake.magnitude >= 5.0 -> HighMagnitude
                    earthquake.magnitude >= 4.0 -> MediumMagnitude
                    else -> LowMagnitude
                }
            ) {
                Text(
                    text = earthquake.magnitude.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = earthquake.location,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Derinlik: ${earthquake.depth} km",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tarih: ${earthquake.getFormattedDateTime()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Deprem tarih ve saatini formatlar
fun formatEarthquakeDateTime(earthquake: EarthquakeResponse): String {
    try {
        var dateStr = earthquake.date
        var timeStr = earthquake.time

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
        if (dateComponents.size != 3) return "${earthquake.date} ${earthquake.time}"

        val year = dateComponents[0]
        val month = dateComponents[1]
        val day = dateComponents[2]

        // Saat bileşenlerini ayır
        val timeComponents = timeStr.split(":")
        if (timeComponents.isEmpty()) return "${earthquake.date} ${earthquake.time}"

        var hour = timeComponents[0].toIntOrNull() ?: 0
        val minute = if (timeComponents.size > 1) timeComponents[1] else "00"

        // Saate 3 saat ekle
        hour = (hour + 3) % 24

        // Formatlanmış tarih ve saati döndür
        return "$day/$month/$year $hour:$minute"
    } catch (e: Exception) {
        // Hata durumunda orijinal tarih ve saati döndür
        return "${earthquake.date} ${earthquake.time}"
    }
}