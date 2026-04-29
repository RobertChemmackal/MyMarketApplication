package com.robert.mymarketplace.data.repository

import com.robert.mymarketplace.data.local.dao.ListingDao
import com.robert.mymarketplace.data.mapper.toListing
import com.robert.mymarketplace.data.mapper.toListingDto
import com.robert.mymarketplace.data.mapper.toListingEntity
import com.robert.mymarketplace.data.remote.MarketPlaceApi
import com.robert.mymarketplace.domain.model.MarketItemListing
import com.robert.mymarketplace.domain.repository.MarketPlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MarketPlaceRepositoryImpl @Inject constructor(
    private val api: MarketPlaceApi,
    private val dao: ListingDao
) : MarketPlaceRepository {

    override fun getListings(): Flow<List<MarketItemListing>> {
        return dao.getAllListings().map { entities ->
            entities.map { it.toListing() }
        }
    }

    override suspend fun refreshListings(): Result<Unit> {
        return try {
            val response = api.getListings()
            dao.upsertListings(response.map { it.toListingEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createListing(marketItemListing: MarketItemListing): Result<Unit> {
        return try {
            // Save locally first with isSynced = false
            val localListing = marketItemListing.copy(isSynced = false)
            dao.upsertListing(localListing.toListingEntity())
            
            // Try to sync with remote
            val response = api.createListing(marketItemListing.toListingDto())
            // If successful, update local with isSynced = true and the real ID if changed
            dao.upsertListing(response.toListing().toListingEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            // Stay unsynced in local DB
            Result.failure(e)
        }
    }

    override suspend fun updateListing(marketItemListing: MarketItemListing): Result<Unit> {
        return try {
            val localListing = marketItemListing.copy(isSynced = false)
            dao.upsertListing(localListing.toListingEntity())
            
            val response = api.updateListing(marketItemListing.id, marketItemListing.toListingDto())
            dao.upsertListing(response.toListing().toListingEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncListing(marketItemListing: MarketItemListing): Result<Unit> {
        return try {
            val response = api.createListing(marketItemListing.toListingDto())
            dao.upsertListing(response.toListing().toListingEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncPendingListings(): Result<Unit> {
        return try {
            val unsynced = dao.getUnsyncedListings()
            unsynced.forEach { entity ->
                val response = api.createListing(entity.toListing().toListingDto())
                dao.upsertListing(response.toListing().toListingEntity())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(marketItemListing: MarketItemListing): Result<Unit> {
        return try {
            val updatedListing = marketItemListing.copy(isFavorite = !marketItemListing.isFavorite)
            dao.upsertListing(updatedListing.toListingEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
