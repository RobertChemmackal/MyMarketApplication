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
        syncStatus = 1
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
        createdAt = createdAt
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
        syncStatus = syncStatus
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
        syncStatus = syncStatus
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
        syncStatus = 1
    )
}
