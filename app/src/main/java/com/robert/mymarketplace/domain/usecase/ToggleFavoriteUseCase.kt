package com.robert.mymarketplace.domain.usecase

import com.robert.mymarketplace.domain.model.MarketItemListing
import com.robert.mymarketplace.domain.repository.MarketPlaceRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MarketPlaceRepository
) {
    suspend operator fun invoke(marketItemListing: MarketItemListing): Result<Unit> {
        return repository.toggleFavorite(marketItemListing)
    }
}
