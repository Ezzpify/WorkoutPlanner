package com.casper.workouts.room.models

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.sql.Date

@Entity(tableName = "Workouts", indices = [Index("WorkoutID")])
data class Workout (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "WorkoutID") val workoutId: Long,

    @ColumnInfo(name = "SortingIndex") var sortingIndex: Int,

    @ColumnInfo(name = "Name") var name: String,

    @ColumnInfo(name = "Description") var description: String?,

    @ColumnInfo(name = "ImageName") var imageName: String?,

    @ColumnInfo(name = "CurrentWorkoutWeek") var currentWorkoutWeek: Int, // UNUSED, DELETE SOON

    @ColumnInfo(name = "CurrentWorkoutDay") var currentWorkoutDay: Int, // UNUSED, DELETE SOON

    @ColumnInfo(name = "CurrentWorkoutIndex") var currentWorkoutIndex: Int,

    @ColumnInfo(name = "LastUpdated") var lastUpdated: Long = System.currentTimeMillis()
) : Serializable {
    fun updateDate() {
        this.lastUpdated = System.currentTimeMillis()
    }
}