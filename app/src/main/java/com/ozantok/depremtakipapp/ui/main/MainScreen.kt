package com.ozantok.depremtakipapp.ui.main


import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ozantok.depremtakipapp.R
import com.ozantok.depremtakipapp.ui.screens.EarthquakeListScreen
import com.ozantok.depremtakipapp.ui.screens.EarthquakeMapScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: EarthquakeViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val earthquakes by viewModel.earthquakes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
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
                    error = error
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

data class TabItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)