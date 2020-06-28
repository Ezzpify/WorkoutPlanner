package com.casper.workouts.room.models

import androidx.room.*
import java.io.Serializable
import java.sql.Date

@Entity(tableName = "Weeks", indices = [Index("WeekID")])
data class Week (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "WeekID") val weekId: Long,

    @ColumnInfo(name = "WorkoutID") val workoutId: Long,

    @ColumnInfo(name = "SortingIndex") var sortingIndex: Int,

    @ColumnInfo(name = "Name") var name: String,

    @ColumnInfo(name = "Description") var description: String?,

    @ColumnInfo(name = "LastUpdated") var lastUpdated: Long = System.currentTimeMillis()
) : Serializable {
    fun updateDate() {
        this.lastUpdated = System.currentTimeMillis()
    }
}