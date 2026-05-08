package com.robert.mymarketplace.presentation.screens.detailScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.robert.mymarketplace.R
import com.robert.mymarketplace.presentation.screens.MarketPlaceViewModel
import com.robert.mymarketplace.presentation.screens.listScreen.ListingEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    navController: NavController,
    viewModel: MarketPlaceViewModel,
    listingId: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val listing = uiState.marketItemListings.find { it.id == listingId }
    val context = LocalContext.current

    if (listing == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_listing))
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.listing_details)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ListingEvent.ToggleFavorite(listing)) }) {
                        Icon(
                            imageVector = if (listing.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (listing.isFavorite) Color.Red else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = listing.imageUrl,
                contentDescription = listing.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_place_holder),
                error = painterResource(R.drawable.ic_place_holder)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = listing.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (listing.syncStatus == 1) {
                            if (uiState.isOffline) Color.LightGray.copy(alpha = 0.2f) else Color.Green.copy(alpha = 0.2f)
                        } else Color.Red.copy(alpha = 0.2f),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (listing.syncStatus == 1) Icons.Default.CloudDone else Icons.Default.Sync,
                                contentDescription = null,
                                tint = if (listing.syncStatus == 1) {
                                    if (uiState.isOffline) Color.Gray else Color(0xFF2E7D32)
                                } else Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = when {
                                    listing.syncStatus == 1 && uiState.isOffline -> stringResource(R.string.cached)
                                    listing.syncStatus == 1 -> stringResource(R.string.synced)
                                    else -> stringResource(R.string.unsync)
                                },
                                color = if (listing.syncStatus == 1) {
                                    if (uiState.isOffline) Color.Gray else Color(0xFF2E7D32)
                                } else Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "$${listing.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Owner Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Name: ${listing.ownerName}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 16.sp,
                            color = Black
                        )
                        Text(
                            text = "Contact: ${listing.phoneNumber}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 16.sp,
                            color = Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.desc),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = listing.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${listing.phoneNumber}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.call_owner), fontSize = 18.sp)
                }
            }
        }
    }
}
