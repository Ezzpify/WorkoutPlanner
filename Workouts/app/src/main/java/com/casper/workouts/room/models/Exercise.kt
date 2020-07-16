package com.casper.workouts.room.models

import androidx.room.*
import java.io.Serializable
import java.sql.Date

@Entity(tableName = "Exercises", indices = [Index("ExerciseID")])
data class Exercise (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ExerciseID") val exerciseId: Long,

    @ColumnInfo(name = "Name") var name: String,

    @ColumnInfo(name = "Tag") var tag: String,

    @ColumnInfo(name = "Description") var description: String?,

    @ColumnInfo(name = "Weight") var weight: Double?,

    @ColumnInfo(name = "WeightUnit") var weightUnit: String?,

    @ColumnInfo(name = "Sets") var sets: Int?,

    @ColumnInfo(name = "Reps") var reps: Int?,

    @ColumnInfo(name = "Timer") var timer: Boolean,

    @ColumnInfo(name = "ImageName") var imageName: String?,

    @ColumnInfo(name = "LastUpdated") var lastUpdated: Long = System.currentTimeMillis()
) : Serializable {
    // This is set after getting sorting indexes from the junction table
    @Ignore
    var sortingIndex: Int = 0

    fun updateDate() {
        this.lastUpdated = System.currentTimeMillis()
    }
}