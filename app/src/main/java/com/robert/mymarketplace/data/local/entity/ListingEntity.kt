package com.robert.mymarketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a marketplace listing stored in the local Room database.
 *
 * @property id Unique identifier for the listing.
 * @property title The title or name of the item being sold.
 * @property description A detailed description of the listing.
 * @property price The cost of the item.
 * @property imageUrl URL pointing to the item's image.
 * @property isFavorite Indicates if the user has marked this listing as a favorite.
 * @property createdAt Timestamp of when the listing was created.
 * @property syncStatus Status of synchronization with the remote server.
 */
@Entity(tableName = "listings")
data class ListingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val syncStatus: Int
)
