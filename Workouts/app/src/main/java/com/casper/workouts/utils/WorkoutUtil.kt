package com.casper.workouts.utils

import com.casper.workouts.room.models.Week
import com.casper.workouts.room.models.junctions.FullWorkout
import com.casper.workouts.room.models.junctions.FullWorkoutDay
import com.casper.workouts.room.models.junctions.FullWorkoutWeek
import java.io.Serializable

class WorkoutUtil(private val fullWorkout: FullWorkout) {
    data class WorkoutDayHolder(val week: Week, val workoutDay: FullWorkoutDay): Serializable

    fun getList(): List<WorkoutDayHolder> {
        val toReturn = ArrayList<WorkoutDayHolder>()

        for (week in fullWorkout.weeks) {
            // Only add days that actually have exercises
            for (day in week.days.filter { it.exercises.isNotEmpty() }) {
                toReturn.add(WorkoutDayHolder(week.week, day))
            }
        }

        return toReturn
    }
}