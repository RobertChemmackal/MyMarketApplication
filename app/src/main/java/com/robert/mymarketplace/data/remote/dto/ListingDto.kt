package com.robert.mymarketplace.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ListingDto(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isFavorite: Boolean,
    val createdAt: Long
)
