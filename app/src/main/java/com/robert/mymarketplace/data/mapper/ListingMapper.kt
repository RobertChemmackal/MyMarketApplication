package com.robert.mymarketplace.data.mapper

import com.robert.mymarketplace.data.local.entity.ListingEntity
import com.robert.mymarketplace.data.remote.dto.ListingDto
import com.robert.mymarketplace.domain.model.MarketItemListing

fun ListingDto.toListing(): MarketItemListing {
    return MarketItemListing(
        id = id,
        title = title,
        description = description,
        price = price,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        createdAt = createdAt,
        syncStatus = 1,
        phoneNumber = phoneNumber ?: "+254700000000",
        ownerName = ownerName ?: "Unknown Owner"
    )
}

fun MarketItemListing.toListingDto(): ListingDto {
    return ListingDto(
        id = id,
        title = title,
        description = description,
        price = price,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        createdAt = createdAt,
        phoneNumber = phoneNumber,
        ownerName = ownerName
    )
}

fun ListingEntity.toListing(): MarketItemListing {
    return MarketItemListing(
        id = id,
        title = title,
        description = description,
        price = price,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        createdAt = createdAt,
        syncStatus = syncStatus,
        phoneNumber = phoneNumber,
        ownerName = ownerName
    )
}

fun MarketItemListing.toListingEntity(): ListingEntity {
    return ListingEntity(
        id = id,
        title = title,
        description = description,
        price = price,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        createdAt = createdAt,
        syncStatus = syncStatus,
        phoneNumber = phoneNumber,
        ownerName = ownerName
    )
}

fun ListingDto.toListingEntity(): ListingEntity {
    return ListingEntity(
        id = id,
        title = title,
        description = description,
        price = price,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        createdAt = createdAt,
        syncStatus = 1,
        phoneNumber = phoneNumber ?: "+254700000000",
        ownerName = ownerName ?: "Unknown Owner"
    )
}
