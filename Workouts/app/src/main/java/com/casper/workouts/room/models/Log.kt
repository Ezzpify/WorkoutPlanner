package com.casper.workouts.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Logs", indices = [Index("LogID")])
data class Log (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "LogID") val logId: Long
)