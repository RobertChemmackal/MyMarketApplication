package com.robert.mymarketplace.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.robert.mymarketplace.domain.repository.MarketPlaceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: MarketPlaceRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val result = repository.syncPendingListings()
        return if (result.isSuccess) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
