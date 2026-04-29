package com.robert.mymarketplace.data.local.dao

import androidx.room.*
import com.robert.mymarketplace.data.local.entity.ListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Query("SELECT * FROM listings ORDER BY createdAt DESC")
    fun getAllListings(): Flow<List<ListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertListings(listings: List<ListingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertListing(listing: ListingEntity)

    @Query("SELECT * FROM listings WHERE isSynced = 0")
    suspend fun getUnsyncedListings(): List<ListingEntity>

    @Query("SELECT * FROM listings WHERE id = :id")
    suspend fun getListingById(id: String): ListingEntity?

    @Query("DELETE FROM listings")
    suspend fun clearAll()
}
