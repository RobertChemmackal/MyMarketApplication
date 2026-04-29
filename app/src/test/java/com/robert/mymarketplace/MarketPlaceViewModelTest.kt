package com.robert.mymarketplace

import com.robert.mymarketplace.presentation.screens.MarketPlaceViewModel


import com.robert.mymarketplace.domain.model.MarketItemListing
import com.robert.mymarketplace.domain.usecase.*
import com.robert.mymarketplace.presentation.screens.listScreen.ListingEvent
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class MarketPlaceViewModelTest {

    private val getListingsUseCase = mockk<GetListingsUseCase>()
    private val refreshListingsUseCase = mockk<RefreshListingsUseCase>(relaxed = true)
    private val syncListingUseCase = mockk<SyncListingUseCase>(relaxed = true)
    private val syncPendingListingsUseCase = mockk<SyncPendingListingsUseCase>()
    private val createListingUseCase = mockk<CreateListingUseCase>(relaxed = true)
    private val toggleFavoriteUseCase = mockk<ToggleFavoriteUseCase>(relaxed = true)

    private lateinit var viewModel: MarketPlaceViewModel

    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { getListingsUseCase() } returns flowOf(emptyList())
        coEvery { syncPendingListingsUseCase() } returns Result.success(Unit)

        viewModel = MarketPlaceViewModel(
            getListingsUseCase,
            refreshListingsUseCase,
            syncListingUseCase,
            syncPendingListingsUseCase,
            createListingUseCase,
            toggleFavoriteUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load listings`() = runTest {
        assertNotNull(viewModel.uiState.value.marketItemListings)
    }

    @Test
    fun `refresh listings should call refresh and sync pending`() = runTest {
        coEvery { refreshListingsUseCase() } returns Result.success(Unit)

        viewModel.onEvent(ListingEvent.RefreshListings)
        advanceUntilIdle()

        coVerify { refreshListingsUseCase() }
    }

    @Test
    fun `sync pending should update success message`() = runTest {
        coEvery { syncPendingListingsUseCase() } returns Result.success(Unit)

        viewModel.syncPending()
        advanceUntilIdle()

        coVerify { syncPendingListingsUseCase() }
        assertEquals("Sync complete", viewModel.uiState.value.message)
    }

    @Test
    fun `toggle favorite should call use case`() = runTest {
        val item = MarketItemListing(
            id = "1",
            title = "Test",
            description = "desc",
            price = 10.0,
            imageUrl = "",
            isFavorite = false,
            createdAt = System.currentTimeMillis(),
            syncStatus = 0
        )

        viewModel.onEvent(ListingEvent.ToggleFavorite(item))

        coVerify { toggleFavoriteUseCase(item) }
    }

    @Test
    fun `create listing should call use case`() = runTest {
        viewModel.onEvent(
            ListingEvent.CreateListing(
                title = "Phone",
                description = "Good phone",
                price = 100.0,
                imageUri = null
            )
        )

        advanceUntilIdle()
        coVerify { createListingUseCase(any()) }
    }
}