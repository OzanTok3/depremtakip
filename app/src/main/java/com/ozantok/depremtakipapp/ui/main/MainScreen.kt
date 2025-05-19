package com.ozantok.depremtakipapp.ui.main


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.ozantok.depremtakipapp.R
import com.ozantok.depremtakipapp.ui.screens.BannerAdView
import com.ozantok.depremtakipapp.ui.screens.EarthquakeListScreen
import com.ozantok.depremtakipapp.ui.screens.EarthquakeMapScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    openMap: Boolean,
    quakeLat: Double,
    quakeLon: Double,
    viewModel: EarthquakeViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val earthquakes by viewModel.earthquakes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableStateOf(if (openMap) 0 else 1) }
    val tabs = listOf(
        TabItem(
            title = stringResource(R.string.map_view),
            icon = Icons.Default.Map,
            route = "map"
        ),
        TabItem(
            title = stringResource(R.string.list_view),
            icon = Icons.Default.List,
            route = "list"
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Banner Reklam - En üstte
        BannerAdView(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )

        // Ana uygulama içeriği
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "map",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("map") {
                    EarthquakeMapScreen(
                        earthquakes = earthquakes,
                        isLoading = isLoading,
                        error = error,
                        // Banner'ı mainScreen'e taşıdığımız için showBanner = false gönderiyoruz
                        showBanner = false,
                        focusedLat = if (quakeLat != -1.0) quakeLat else null,
                        focusedLon = if (quakeLon != -1.0) quakeLon else null
                    )
                }
                composable("list") {
                    EarthquakeListScreen(
                        earthquakes = earthquakes,
                        isLoading = isLoading,
                        error = error,
                        onRefresh = { viewModel.getEarthquakes() }
                    )
                }
            }
        }
    }
}

data class TabItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)