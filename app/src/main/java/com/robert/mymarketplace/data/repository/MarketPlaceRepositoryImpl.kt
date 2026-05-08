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
            
            // Get all local listings
            val localListings = dao.getAllListingsList().associateBy { it.id }
            
            // Items from server, merged with local data (favorites, phone, etc.)
            val mergedRemoteEntities = remoteEntities.map { remote ->
                localListings[remote.id]?.let { local ->
                    remote.copy(
                        isFavorite = local.isFavorite, 
                        syncStatus = if (local.syncStatus == 0) 0 else remote.syncStatus,
                        phoneNumber = if (remote.phoneNumber == "+254700000000" || remote.phoneNumber.isEmpty()) 
                            local.phoneNumber else remote.phoneNumber,
                        ownerName = if (remote.ownerName == "Unknown Owner" || remote.ownerName == "Demo User" || remote.ownerName.isEmpty())
                            local.ownerName else remote.ownerName
                    )
                } ?: remote
            }
            
            // We DON'T want to delete local items that haven't been synced yet
            // Room's upsert doesn't delete, so this is safe.
            dao.upsertListings(mergedRemoteEntities)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createListing(marketItemListing: MarketItemListing): Result<Unit> {
        return try {
            // Save locally first with syncStatus = 0 (Unsynced)
            val localEntity = marketItemListing.copy(syncStatus = 0, network = 0).toListingEntity()
            dao.upsertListing(localEntity)
            
            // Try to sync with remote
            val responseDto = api.createListing(marketItemListing.toListingDto())
            val remoteListing = responseDto.toListing()
            
            // If the server returns a different ID, we should remove the local temporary one
            if (remoteListing.id != marketItemListing.id) {
                dao.deleteListingById(marketItemListing.id)
            }

            // Update local with server response, but preserve our local details if server is missing them
            val finalListing = remoteListing.copy(
                syncStatus = 1,
                network = 1,
                phoneNumber = if (remoteListing.phoneNumber == "+254700000000" || remoteListing.phoneNumber.isEmpty()) 
                    marketItemListing.phoneNumber else remoteListing.phoneNumber,
                ownerName = if (remoteListing.ownerName == "Unknown Owner" || remoteListing.ownerName.isEmpty()) 
                    marketItemListing.ownerName else remoteListing.ownerName
            )
            
            dao.upsertListing(finalListing.toListingEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            // Stay unsynced in local DB if remote call fails
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
            dao.upsertListing(response.toListing().copy(syncStatus = 1, network = 1).toListingEntity())
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
            
            val responseEntities = response.map { dto ->
                dto.toListingEntity().copy(syncStatus = 1, network = 1)
            }
            
            // Using upsert instead of delete+insert to maintain database integrity
            dao.upsertListings(responseEntities)
            
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
                syncStatus = 0,
                network = 0
            )
            dao.upsertListing(updatedListing.toListingEntity())
            
            // Try to sync the favorite status to remote
            val response = api.updateListing(updatedListing.id, updatedListing.toListingDto())
            dao.upsertListing(response.toListing().copy(syncStatus = 1, network = 1).toListingEntity())
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }
}
