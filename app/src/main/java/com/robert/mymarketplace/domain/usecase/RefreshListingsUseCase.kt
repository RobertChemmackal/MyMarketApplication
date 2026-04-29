package com.robert.mymarketplace.domain.usecase

import com.robert.mymarketplace.domain.repository.MarketPlaceRepository
import javax.inject.Inject

class RefreshListingsUseCase @Inject constructor(
    private val repository: MarketPlaceRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshListings()
    }
}
