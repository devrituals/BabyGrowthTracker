package com.example.babygrowthtracker.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.babygrowthtracker.data.local.LogDao
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val logDao: LogDao,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Get logs from Room that haven't been synced
            val unsyncedLogs = logDao.getUnsyncedLogs()

            // 2. Loop and push to Firestore
            unsyncedLogs.forEach { log ->
                val logMap = hashMapOf(
                    "id" to log.id,
                    "babyId" to log.babyId,
                    "type" to log.type,
                    "value" to log.value,
                    "timestamp" to log.timestamp
                )
                
                // Upload to Firestore
                firestore.collection("logs")
                    .document(log.id)
                    .set(logMap)
                    .await()

                // 3. Mark as synced locally
                logDao.markAsSynced(log.id)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}