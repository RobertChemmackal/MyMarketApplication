package com.robert.mymarketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.robert.mymarketplace.presentation.navigation.Screen
import com.robert.mymarketplace.presentation.screens.favList.FavoritesScreen
import com.robert.mymarketplace.presentation.screens.listScreen.MarketPlaceListingScreen
import com.robert.mymarketplace.presentation.screens.MarketPlaceViewModel
import com.robert.mymarketplace.presentation.screens.addListingScreen.AddListingScreen
import com.robert.mymarketplace.presentation.screens.detailScreen.ListingDetailScreen
import com.robert.mymarketplace.presentation.screens.listScreen.ListingEvent
import com.robert.mymarketplace.presentation.screens.splashScreen.SplashScreen
import com.robert.mymarketplace.ui.theme.MyMarketPlaceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyMarketPlaceTheme {
                MainContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    val navController = rememberNavController()
    val viewModel: MarketPlaceViewModel = hiltViewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(Screen.Listing.route, Screen.Favorites.route)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showBottomBar) {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.main_title),
                            style = TextStyle.Default,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Black,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(ListingEvent.SyncAllPending) }) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = stringResource(R.string.sync_all_content_description),
                                tint = Black
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple(Screen.Listing, Icons.AutoMirrored.Filled.List, R.string.nav_list),
                        Triple(Screen.Favorites, Icons.Default.Favorite, R.string.nav_favorites)
                    )
                    items.forEach { (screen, icon, labelRes) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = stringResource(labelRes)) },
                            label = { Text(stringResource(labelRes)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(navController)
            }
            composable(Screen.Listing.route) {
                MarketPlaceListingScreen(navController, innerPadding, viewModel)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(navController, innerPadding, viewModel)
            }
            composable(Screen.AddEdit.route) {
                AddListingScreen(navController, viewModel)
            }
            composable(Screen.Detail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                ListingDetailScreen(navController, viewModel, id)
            }
        }
    }
}

