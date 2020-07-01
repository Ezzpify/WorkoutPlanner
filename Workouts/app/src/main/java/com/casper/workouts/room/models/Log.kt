package com.casper.workouts.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Logs", indices = [Index("LogID")])
data class Log (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "LogID") val logId: Long,

    @ColumnInfo(name = "WorkoutID") val workoutId: Long,

    @ColumnInfo(name = "WeekId") val weekId: Long,

    @ColumnInfo(name = "DayID") val dayId: Long,

    @ColumnInfo(name = "StartTimestamp") val startTimestamp: Long,

    @ColumnInfo(name = "EndTimestamp") val endTimestamp: Long,

    @ColumnInfo(name = "Rating") val rating: Int
)