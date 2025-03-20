package com.ravi.skinhealthyai.data.database

import androidx.sqlite.db.SimpleSQLiteQuery
import java.lang.StringBuilder

object QueryHelper {
    fun getAllHistoryBetween(start: Long?, end: Long?): SimpleSQLiteQuery {
        val query = StringBuilder().append("SELECT * FROM history")

        if (start != null && end != null) {
            query.append(" WHERE createdAt BETWEEN ? AND ? ORDER BY createdAt DESC")
            return SimpleSQLiteQuery(query.toString(), arrayOf(start, end))
        }
        query.append(" ORDER BY createdAt DESC")

        return SimpleSQLiteQuery(query.toString())
    }
}