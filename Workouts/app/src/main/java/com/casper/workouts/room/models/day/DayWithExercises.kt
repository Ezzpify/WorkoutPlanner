package com.casper.workouts.room.models.day

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.casper.workouts.room.models.Day
import com.casper.workouts.room.models.Exercise

data class DayWithExercises (
    @Embedded val day: Day,

    @Relation(
        parentColumn = "DayID",
        entityColumn = "ExerciseID",
        associateBy = Junction(value = DayExerciseCrossRef::class,
            parentColumn = "DayID",
            entityColumn = "ExerciseID")
    )
    val exercises: List<Exercise>
)