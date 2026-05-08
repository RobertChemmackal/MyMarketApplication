package com.robert.mymarketplace.di

import android.content.Context
import com.robert.mymarketplace.util.ConnectivityObserver
import com.robert.mymarketplace.util.NetworkObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNetworkObserver(@ApplicationContext context: Context): NetworkObserver {
        return ConnectivityObserver(context)
    }
}
