package com.letsgotoperfection.kino.feature.recurringtasks.internal.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase.GenerateTaskInstanceUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

/**
 * Worker that materializes a single recurring task occurrence into a task on the board.
 *
 * Triggered by [RecurringTaskAlarmReceiver] when an alarm fires. All business logic
 * lives in [GenerateTaskInstanceUseCase]; this worker only handles WorkManager
 * concerns (input parsing, retries).
 */
@HiltWorker
class TaskInstanceCreatorWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val generateTaskInstance: GenerateTaskInstanceUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val recurringTaskId = inputData.getString(KEY_RECURRING_TASK_ID)
            ?: return Result.failure()
        val scheduledDateEpochDay = inputData.getLong(KEY_SCHEDULED_DATE, -1)
        if (scheduledDateEpochDay == -1L) return Result.failure()

        val scheduledDate = LocalDate.ofEpochDay(scheduledDateEpochDay)
        Log.i(TAG, "Generating instance for $recurringTaskId at $scheduledDate")

        return generateTaskInstance(recurringTaskId, scheduledDate).fold(
            onSuccess = { Result.success() },
            onFailure = { error ->
                Log.e(TAG, "Failed to generate instance for $recurringTaskId", error)
                if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
            }
        )
    }

    companion object {
        private const val TAG = "TaskInstanceCreator"
        private const val MAX_RETRIES = 3
        const val KEY_RECURRING_TASK_ID = "recurring_task_id"
        const val KEY_SCHEDULED_DATE = "scheduled_date"
    }
}
