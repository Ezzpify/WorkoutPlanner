package com.casper.workouts.room.models

import androidx.room.*
import com.casper.workouts.room.models.dayjunctions.DayExerciseCrossRef
import java.io.Serializable

data class FullWorkout(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "WorkoutID",
        entityColumn = "WorkoutID",
        entity = Week::class
    )
    val weeks: List<FullWorkoutWeek>
)

data class FullWorkoutWeek(
    @Embedded val week: Week,
    @Relation(
        parentColumn = "WeekID",
        entityColumn = "WeekID",
        entity = Day::class
    )
    val days: List<FullWorkoutDay>
): Serializable

data class FullWorkoutDay(
    @Embedded val day: Day,
    @Relation(
        parentColumn = "DayID",
        entityColumn = "ExerciseID",
        associateBy = Junction(value = DayExerciseCrossRef::class,
            parentColumn = "DayID",
            entityColumn = "ExerciseID")
    )
    val exercises: List<Exercise>
): Serializable