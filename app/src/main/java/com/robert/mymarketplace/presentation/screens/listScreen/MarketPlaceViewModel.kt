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
import com.robert.mymarketplace.util.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val networkObserver: NetworkObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListingUiState())
    val uiState = _uiState.asStateFlow()

    private val _isOffline = MutableStateFlow(false)

    init {
        observeNetwork()
        observeListings()
        onEvent(ListingEvent.RefreshListings)
    }

    private fun observeNetwork() {
        networkObserver.observe().onEach { status ->
            val offline = status != NetworkObserver.Status.Available
            val wasOffline = _isOffline.value
            _isOffline.value = offline
            
            _uiState.update { it.copy(
                isOffline = offline,
                showOfflineDialog = offline
            ) }
            
            if (!offline && wasOffline) {
                // Network just came back
                val hasPending = _uiState.value.marketItemListings.any { it.syncStatus == 0 }
                if (hasPending) {
                    syncPending()
                } else {
                    _uiState.update { it.copy(message = "Connection restored") }
                }
            }
        }.launchIn(viewModelScope)
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
                createListing(event.title, event.description, event.price, event.imageUri, event.phoneNumber, event.ownerName)
            }
            is ListingEvent.DismissOfflineDialog -> {
                _uiState.update { it.copy(showOfflineDialog = false) }
            }
            is ListingEvent.ClearMessage -> {
                _uiState.update { it.copy(message = null) }
            }
        }
    }

    private fun observeListings() {
        viewModelScope.launch {
            getListingsUseCase().collect { listings ->
                _uiState.update { it.copy(
                    marketItemListings = listings,
                    isLoading = false
                ) }
                
                if (!_isOffline.value && listings.any { it.syncStatus == 0 } && !_uiState.value.isSyncing) {
                    syncPending()
                }
            }
        }
    }

    private fun refreshListings() {
        if (_isOffline.value) {
            _uiState.update { it.copy(
                isRefreshing = false,
                message = "Cannot refresh while offline"
            ) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            refreshListingsUseCase()
                .onFailure { error ->
                    _uiState.update { it.copy(
                        error = error.message ?: "Unknown error"
                    ) }
                }
            performSync()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun syncPending() {
        viewModelScope.launch {
            performSync()
        }
    }

    private suspend fun performSync() {
        if (_isOffline.value) return
        
        var hasItemsToSync = false
        _uiState.update { state ->
            hasItemsToSync = state.marketItemListings.any { it.syncStatus == 0 }
            if (hasItemsToSync) {
                state.copy(isSyncing = true, message = "Syncing all pending items...")
            } else if (!state.isOffline && state.message == null) {
                // If nothing to sync, show connection restored instead
                state.copy(message = "Connection restored")
            } else {
                state
            }
        }
        
        if (hasItemsToSync) {
            syncPendingListingsUseCase()
                .onSuccess {
                    _uiState.update { it.copy(isSyncing = false, message = "Sync complete") }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        error = error.message ?: "Sync failed",
                        isSyncing = false,
                        message = null
                    ) }
                }
        }
    }

    private fun syncListing(marketItemListing: MarketItemListing) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, message = "Syncing ${marketItemListing.title}...") }
            syncListingUseCase(marketItemListing)
                .onSuccess {
                    _uiState.update { it.copy(isSyncing = false, message = "Sync complete") }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isSyncing = false,
                        error = error.message ?: "Sync failed"
                    ) }
                }
        }
    }

    private fun toggleFavorite(marketItemListing: MarketItemListing) {
        viewModelScope.launch {
            toggleFavoriteUseCase(marketItemListing)
        }
    }

    private fun createListing(title: String, description: String, price: Double, imageUri: String?, phoneNumber: String, ownerName: String) {
        viewModelScope.launch {
            val newListing = MarketItemListing(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                price = price,
                imageUrl = imageUri ?: "",
                isFavorite = false,
                createdAt = System.currentTimeMillis(),
                syncStatus = 0,
                phoneNumber = phoneNumber,
                ownerName = ownerName
            )
            createListingUseCase(newListing)
        }
    }
}
