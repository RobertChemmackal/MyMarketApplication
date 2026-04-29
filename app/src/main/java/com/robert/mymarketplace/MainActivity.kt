package com.robert.mymarketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.robert.mymarketplace.presentation.navigation.Screen
import com.robert.mymarketplace.presentation.screens.MarketPlaceListingScreen
import com.robert.mymarketplace.presentation.screens.MarketPlaceViewModel
import com.robert.mymarketplace.presentation.screens.addListingScreen.AddListingScreen
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
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentRoute == Screen.Listing.route) {
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
        }) { innerPadding ->
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
            composable(Screen.AddEdit.route) {
                AddListingScreen(navController, viewModel)
            }
        }
    }
}
