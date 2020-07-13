package com.casper.workouts.room.models.junctions

import androidx.room.*
import com.casper.workouts.room.models.Day
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.models.Week
import com.casper.workouts.room.models.Workout
import java.io.Serializable

data class FullWorkout(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "WorkoutID",
        entityColumn = "WorkoutID",
        entity = Week::class
    )
    var weeks: List<FullWorkoutWeek>
) {
    fun hasNecessaryData(): Boolean {
        return weeks.isNotEmpty() && weeks.any { week -> week.days.isNotEmpty() && week.days.any { day -> day.exercises.isNotEmpty() } }
    }
}

data class FullWorkoutWeek(
    @Embedded val week: Week,
    @Relation(
        parentColumn = "WeekID",
        entityColumn = "WeekID",
        entity = Day::class
    )
    var days: List<FullWorkoutDay>
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
    var exercises: List<Exercise>
): Serializable