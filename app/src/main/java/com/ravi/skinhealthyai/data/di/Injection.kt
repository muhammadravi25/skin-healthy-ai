package com.ravi.skinhealthyai.data.di

import android.content.Context
import com.ravi.skinhealthyai.data.database.HistoryDatabase
import com.ravi.skinhealthyai.data.repository.HistoryRepository

object Injection {
    fun provideHistoryRepository(context: Context): HistoryRepository {
        val database = HistoryDatabase.getDatabase(context)
        return HistoryRepository(database.historyDao())
    }
}