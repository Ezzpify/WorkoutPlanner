package com.casper.workouts.room.models.dayjunctions

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(primaryKeys = ["DayID", "ExerciseID"], indices = [Index("DayID", "ExerciseID")])
data class DayExerciseCrossRef(
    @ColumnInfo(name = "DayID") val dayId: Long,
    @ColumnInfo(name = "ExerciseID", index = true) val exerciseId: Long,
    @ColumnInfo(name = "ExerciseSortingIndex") var sortingIndex: Int
)