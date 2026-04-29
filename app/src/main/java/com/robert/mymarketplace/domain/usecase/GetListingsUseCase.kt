package com.robert.mymarketplace.domain.usecase

import com.robert.mymarketplace.domain.model.MarketItemListing
import com.robert.mymarketplace.domain.repository.MarketPlaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListingsUseCase @Inject constructor(
    private val repository: MarketPlaceRepository
) {
    operator fun invoke(): Flow<List<MarketItemListing>> {
        return repository.getListings()
    }
}
