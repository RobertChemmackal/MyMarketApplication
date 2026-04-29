package com.robert.mymarketplace.domain.model

data class MarketItemListing(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val syncStatus: Int = 1
)
