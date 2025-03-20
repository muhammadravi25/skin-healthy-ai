package com.ravi.skinhealthyai.data.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.ravi.skinhealthyai.data.model.History

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(history: History)

    @RawQuery(observedEntities = [History::class])
    fun getAllHistory(query: SupportSQLiteQuery): DataSource.Factory<Int, History>

    @Query("SELECT * FROM history ORDER BY createdAt DESC LIMIT 3")
    fun getLastThreeHistory(): LiveData<List<History>>

    @Query("SELECT * FROM History WHERE id = :id")
    suspend fun getHistoryId(id: Int): History
}