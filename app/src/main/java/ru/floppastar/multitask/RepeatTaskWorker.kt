package ru.floppastar.multitask

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.floppastar.multitask.db.DatabaseHelper
import ru.floppastar.multitask.db.DatabaseRepository
import java.util.Calendar

class RepeatTaskWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return try {
            val repository = DatabaseRepository(DatabaseHelper(applicationContext))

            val calendar = Calendar.getInstance()

            repository.resetDailyTasks()

            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                repository.resetWeeklyTasks()
            }

            if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
                repository.resetMonthlyTasks()
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("RepeatTaskWorker", "Ошибка сброса задач: ", e)
            Result.failure()
        }
    }
}