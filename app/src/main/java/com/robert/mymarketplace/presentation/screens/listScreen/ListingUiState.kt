package com.robert.mymarketplace.presentation.screens.listScreen

import com.robert.mymarketplace.domain.model.MarketItemListing

data class ListingUiState(
    val marketItemListings: List<MarketItemListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val error: String? = null,
    val message: String? = null
)
