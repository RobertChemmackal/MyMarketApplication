package com.robert.mymarketplace.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.robert.mymarketplace.data.local.dao.ListingDao
import com.robert.mymarketplace.data.local.entity.ListingEntity

@Database(entities = [ListingEntity::class], version = 1, exportSchema = false)
abstract class MarketPlaceDatabase : RoomDatabase() {
    abstract fun listingDao(): ListingDao
}
