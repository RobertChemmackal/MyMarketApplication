package com.robert.mymarketplace.presentation.screens.favList

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.robert.mymarketplace.R
import com.robert.mymarketplace.presentation.navigation.Screen
import com.robert.mymarketplace.presentation.screens.listScreen.ListingItem
import com.robert.mymarketplace.presentation.screens.MarketPlaceViewModel
import com.robert.mymarketplace.presentation.screens.listScreen.ListingEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    viewModel: MarketPlaceViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites = uiState.marketItemListings.filter { it.isFavorite }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        if (favorites.isEmpty()) {
            Text(
                text = stringResource(R.string.no_favorites),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favorites, key = { it.id }) { marketItem ->
                    ListingItem(
                        uiState,
                        marketItemListing = marketItem,
                        onFavoriteClick = { viewModel.onEvent(ListingEvent.ToggleFavorite(marketItem)) },
                        onItemClick = { navController.navigate(Screen.Detail.createRoute(marketItem.id)) },
                        onSyncClick = {
                            viewModel.onEvent(
                                ListingEvent.SyncIndividualMarketCard(
                                    marketItem
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
