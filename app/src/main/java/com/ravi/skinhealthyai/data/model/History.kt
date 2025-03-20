package com.ravi.skinhealthyai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var nameSkinDisease: String,
    var photo: String,
    var accuracy: Float,
    var createdAt: Long
)
