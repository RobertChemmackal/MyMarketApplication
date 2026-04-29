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
            val remoteEntities = response.map { it.toListingEntity() }
            
            // Get all local listings to preserve favorite status even if synced
            // Since the mock server doesn't persist changes, we trust local favorites
            val localListings = dao.getAllListingsList().associateBy { it.id }
            
            val finalEntities = remoteEntities.map { remote ->
                localListings[remote.id]?.let { local ->
                    // Favor local favorite status and maintain syncStatus if it's still 0
                    remote.copy(
                        isFavorite = local.isFavorite, 
                        syncStatus = if (local.syncStatus == 0) 0 else remote.syncStatus
                    )
                } ?: remote
            }
            
            dao.upsertListings(finalEntities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createListing(marketItemListing: MarketItemListing): Result<Unit> {
        return try {
            // Save locally first with syncStatus = 0 (Unsynced)
            val localListing = marketItemListing.copy(syncStatus = 0)
            dao.upsertListing(localListing.toListingEntity())
            
            // Try to sync with remote
            val response = api.createListing(marketItemListing.toListingDto())
            // If successful, update local with syncStatus = 1
            dao.upsertListing(response.toListing().toListingEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            // Stay unsynced (syncStatus = 0) in local DB
            Result.failure(e)
        }
    }

    override suspend fun updateListing(marketItemListing: MarketItemListing): Result<Unit> {
        return try {
            val localListing = marketItemListing.copy(syncStatus = 0)
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
            // Try updating first, if it fails maybe it needs to be created
            val response = try {
                api.updateListing(marketItemListing.id, marketItemListing.toListingDto())
            } catch (e: Exception) {
                api.createListing(marketItemListing.toListingDto())
            }
            dao.upsertListing(response.toListing().toListingEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncPendingListings(): Result<Unit> {
        return try {
            val unsynced = dao.getUnsyncedListings()
            if (unsynced.isEmpty()) return Result.success(Unit)

            val response = api.syncListings(unsynced.map { it.toListing().toListingDto() })
            dao.upsertListings(response.map { it.toListingEntity() })
            
            Result.success(Unit)
        } catch (_: Exception) {
            try {
                val unsynced = dao.getUnsyncedListings()
                unsynced.forEach { entity ->
                    syncListing(entity.toListing())
                }
                Result.success(Unit)
            } catch (innerException: Exception) {
                Result.failure(innerException)
            }
        }
    }

    override suspend fun toggleFavorite(marketItemListing: MarketItemListing): Result<Unit> {
        return try {
            val updatedListing = marketItemListing.copy(
                isFavorite = !marketItemListing.isFavorite,
                syncStatus = 0
            )
            dao.upsertListing(updatedListing.toListingEntity())
            
            // Try to sync the favorite status to remote
            val response = api.updateListing(updatedListing.id, updatedListing.toListingDto())
            dao.upsertListing(response.toListing().toListingEntity())
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }
}
