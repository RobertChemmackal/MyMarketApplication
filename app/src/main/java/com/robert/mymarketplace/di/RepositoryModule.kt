package com.robert.mymarketplace.di

import com.robert.mymarketplace.data.repository.MarketPlaceRepositoryImpl
import com.robert.mymarketplace.domain.repository.MarketPlaceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMarketPlaceRepository(
        marketPlaceRepositoryImpl: MarketPlaceRepositoryImpl
    ): MarketPlaceRepository
}
