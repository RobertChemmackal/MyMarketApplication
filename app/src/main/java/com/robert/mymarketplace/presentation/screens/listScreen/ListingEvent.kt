package com.robert.mymarketplace.presentation.screens.listScreen

import com.robert.mymarketplace.domain.model.MarketItemListing

sealed class ListingEvent {
    object RefreshListings : ListingEvent()
    data class SyncIndividualMarketCard(val marketItemListing: MarketItemListing) : ListingEvent()
    object SyncAllPending : ListingEvent()
    data class ToggleFavorite(val marketItemListing: MarketItemListing) : ListingEvent()
    data class CreateListing(
        val title: String,
        val description: String,
        val price: Double,
        val imageUri: String?,
        val phoneNumber: String,
        val ownerName: String
    ) : ListingEvent()
    object DismissOfflineDialog : ListingEvent()
    object ClearMessage : ListingEvent()
}
