package com.robert.mymarketplace.presentation.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.robert.mymarketplace.R
import com.robert.mymarketplace.domain.model.MarketItemListing
import com.robert.mymarketplace.presentation.navigation.Screen
import com.robert.mymarketplace.presentation.screens.listScreen.ListingEvent
import com.robert.mymarketplace.presentation.screens.listScreen.ListingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPlaceListingScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    viewModel: MarketPlaceViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler {
        (context as? Activity)?.finish()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddEdit.route) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_listing_content_description))
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.onEvent(ListingEvent.RefreshListings) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(innerPadding)
        ) {
            if (uiState.isLoading && uiState.marketItemListings.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null && uiState.marketItemListings.isEmpty()) {
                Text(
                    text = uiState.error ?: stringResource(R.string.unknown_error),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.marketItemListings, key = { it.id }) { marketItem ->
                        ListingItem(
                            uiState,
                            marketItemListing = marketItem,
                            onFavoriteClick = { viewModel.onEvent(ListingEvent.ToggleFavorite(marketItem)) },
                            onItemClick = {  },
                            onSyncClick = { viewModel.onEvent(ListingEvent.SyncIndividualMarketCard(marketItem)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListingItem(
    uiState: ListingUiState,
    marketItemListing: MarketItemListing,
    onFavoriteClick: () -> Unit,
    onItemClick: () -> Unit,
    onSyncClick: () -> Unit
) {
    Card(
        onClick = onItemClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = marketItemListing.imageUrl,
                    contentDescription = marketItemListing.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_place_holder),
                    error = painterResource(R.drawable.ic_place_holder)
                )
                
                Surface(
                    onClick = { if (marketItemListing.syncStatus == 0) onSyncClick() },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    shape = MaterialTheme.shapes.small,
                    color = Color.Black.copy(alpha = 0.6f),
                    enabled = marketItemListing.syncStatus == 0
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (marketItemListing.syncStatus == 1) Icons.Default.Sync else Icons.Default.SyncDisabled,
                            contentDescription = stringResource(R.string.sync_status_content_description),
                            tint = if (marketItemListing.syncStatus == 1) Color.Green else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        if (marketItemListing.syncStatus == 0) {
                            Text(
                                text = stringResource(R.string.sync_now),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }


                if (uiState.isLoading && marketItemListing.syncStatus == 1) {
                     // Optionally show a small loader here if refreshing
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(
                        imageVector = if (marketItemListing.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_content_description),
                        tint = if (marketItemListing.isFavorite) Color.Red else Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = marketItemListing.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$${marketItemListing.price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = marketItemListing.description,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
