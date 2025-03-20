package com.ravi.skinhealthyai.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ravi.skinhealthyai.data.database.HistoryDao
import com.ravi.skinhealthyai.data.database.QueryHelper
import com.ravi.skinhealthyai.data.model.History

class HistoryRepository(private val historyDao: HistoryDao) {
    fun getAllHistory(start: Long?, end: Long?): LiveData<PagedList<History>> {
        val query = QueryHelper.getAllHistoryBetween(start, end)
        val history = historyDao.getAllHistory(query)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(8)
            .setPageSize(8)
            .build()

        return LivePagedListBuilder(history, config).build()
    }

    suspend fun getHistoryById(id: Int): History? {
        return historyDao.getHistoryId(id)
    }

    fun getLastThreeHistory(): LiveData<List<History>> {
        return historyDao.getLastThreeHistory()
    }

    suspend fun insertHistory(history: History) {
        historyDao.insertHistory(history)
    }
}