package com.example.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters


class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = UserDatabase.getDatabase(applicationContext)
        val userDataDao = database.userDataDao()
        val userDataList = userDataDao.getAll()

        for (userData in userDataList) {
            val success = logToConsole(userData)
            if (success) {
                userDataDao.delete(userData)
            } else {
                return Result.retry()
            }
        }

        return Result.success()
    }

    private suspend fun logToConsole(userData: UserData): Boolean {
        // Log the user data to the console
        Log.d("UploadWorker", "Name: ${userData.name}, Age: ${userData.age}")
        return true
    }

}
