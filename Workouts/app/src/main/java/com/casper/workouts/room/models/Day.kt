package com.casper.workouts.room.models

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.io.Serializable
import java.sql.Date

@Entity(tableName = "Days",
    foreignKeys = [ForeignKey(entity = Week::class,
        parentColumns = arrayOf("WeekID"),
        childColumns = arrayOf("WeekID"),
        onDelete = ForeignKey.CASCADE)],
    indices = [Index("DayID")])
data class Day (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DayID") val dayId: Long,

    @ColumnInfo(name = "WeekID") val weekId: Long,

    @ColumnInfo(name = "SortingIndex") var sortingIndex: Int,

    @ColumnInfo(name = "Name") var name: String,

    @ColumnInfo(name = "Description") var description: String?,

    @ColumnInfo(name = "LastUpdated") var lastUpdated: Long = System.currentTimeMillis()
) : Serializable {
    fun updateDate() {
        this.lastUpdated = System.currentTimeMillis()
    }
}