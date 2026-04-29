package com.robert.mymarketplace.domain.repository

import com.robert.mymarketplace.domain.model.MarketItemListing
import kotlinx.coroutines.flow.Flow

interface MarketPlaceRepository {
    fun getListings(): Flow<List<MarketItemListing>>
    suspend fun refreshListings(): Result<Unit>
    suspend fun createListing(marketItemListing: MarketItemListing): Result<Unit>
    suspend fun updateListing(marketItemListing: MarketItemListing): Result<Unit>
    suspend fun syncListing(marketItemListing: MarketItemListing): Result<Unit>
    suspend fun syncPendingListings(): Result<Unit>
    suspend fun toggleFavorite(marketItemListing: MarketItemListing): Result<Unit>
}
