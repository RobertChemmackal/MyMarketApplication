package com.robert.mymarketplace.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robert.mymarketplace.domain.model.MarketItemListing
import com.robert.mymarketplace.domain.usecase.CreateListingUseCase
import com.robert.mymarketplace.domain.usecase.GetListingsUseCase
import com.robert.mymarketplace.domain.usecase.RefreshListingsUseCase
import com.robert.mymarketplace.domain.usecase.SyncListingUseCase
import com.robert.mymarketplace.domain.usecase.SyncPendingListingsUseCase
import com.robert.mymarketplace.domain.usecase.ToggleFavoriteUseCase
import com.robert.mymarketplace.presentation.screens.listScreen.ListingEvent
import com.robert.mymarketplace.presentation.screens.listScreen.ListingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MarketPlaceViewModel @Inject constructor(
    private val getListingsUseCase: GetListingsUseCase,
    private val refreshListingsUseCase: RefreshListingsUseCase,
    private val syncListingUseCase: SyncListingUseCase,
    private val syncPendingListingsUseCase: SyncPendingListingsUseCase,
    private val createListingUseCase: CreateListingUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeListings()
        onEvent(ListingEvent.RefreshListings)
    }

    fun onEvent(event: ListingEvent) {
        when (event) {
            is ListingEvent.RefreshListings -> {
                refreshListings()
            }
            is ListingEvent.SyncIndividualMarketCard -> {
                syncListing(event.marketItemListing)
            }
            is ListingEvent.SyncAllPending -> {
                syncPending()
            }
            is ListingEvent.ToggleFavorite -> {
                toggleFavorite(event.marketItemListing)
            }
            is ListingEvent.CreateListing -> {
                createListing(event.title, event.description, event.price, event.imageUri)
            }
        }
    }

    private fun observeListings() {
        viewModelScope.launch {
            getListingsUseCase().collectLatest { listings ->
                _uiState.value = _uiState.value.copy(
                    marketItemListings = listings,
                    isLoading = false
                )
            }
        }
    }

    private fun refreshListings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            refreshListingsUseCase()
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Unknown error"
                    )
                }
            syncPending()
            _uiState.value = _uiState.value.copy(isRefreshing = false, isLoading = false)
        }
    }

    fun syncPending() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = "Syncing all pending items...")
            syncPendingListingsUseCase()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "Sync complete")
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Sync failed",
                        isLoading = false,
                        message = null
                    )
                }
        }
    }

    private fun syncListing(marketItemListing: MarketItemListing) {
        viewModelScope.launch {
            syncListingUseCase(marketItemListing)
        }
    }

    private fun toggleFavorite(marketItemListing: MarketItemListing) {
        viewModelScope.launch {
            toggleFavoriteUseCase(marketItemListing)
        }
    }

    private fun createListing(title: String, description: String, price: Double, imageUri: String?) {
        viewModelScope.launch {
            val newListing = MarketItemListing(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                price = price,
                imageUrl = imageUri ?: "",
                isFavorite = false,
                createdAt = System.currentTimeMillis(),
                isSynced = false
            )
            createListingUseCase(newListing)
        }
    }
}
