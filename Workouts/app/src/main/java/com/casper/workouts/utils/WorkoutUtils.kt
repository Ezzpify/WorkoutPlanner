package com.casper.workouts.utils

import com.casper.workouts.room.models.junctions.FullWorkout
import com.casper.workouts.room.models.junctions.FullWorkoutDay
import com.casper.workouts.room.models.junctions.FullWorkoutWeek
import com.casper.workouts.room.models.Workout
import java.io.Serializable

/*class WorkoutUtils(private val fullWorkout: FullWorkout) {
    data class WorkoutInfo(val workout: Workout, val workoutWeek: FullWorkoutWeek, val workoutDay: FullWorkoutDay, val weekIndex: Int, val dayIndex: Int): Serializable

    fun moveDayForward() {
        fullWorkout.workout.currentWorkoutDay += 1
    }

    fun getWorkout(): Workout = fullWorkout.workout

    fun getWorkoutInfo(): WorkoutInfo {
        var newWeek = getNextWeek()
        while (newWeek.days.size - 1 < fullWorkout.workout.currentWorkoutDay) {
            // Ran out of days, move to next week and day one
            fullWorkout.workout.currentWorkoutDay = 0
            fullWorkout.workout.currentWorkoutWeek += 1

            // Get new week
            newWeek = getNextWeek()
        }

        // List of sorted days, same as weeks
        val days = newWeek.days.sortedBy { it.day.sortingIndex }

        // Get our potential new day
        val newDay = days[fullWorkout.workout.currentWorkoutDay]
        if (newDay.exercises.isEmpty()) {
            // No exercises in this day, move to next day
            fullWorkout.workout.currentWorkoutDay += 1
            return getWorkoutInfo()
        }

        // Get indexes for new week and day
        // Could evaluate these during the loop, but I'm lazy now
        val newWeekIndex = fullWorkout.weeks.indexOfFirst { it.week.weekId == newWeek.week.weekId }
        val newDayIndex = days.indexOfFirst { it.day.dayId == newDay.day.dayId }

        // Returns our new workout day
        return WorkoutInfo(getWorkout(), newWeek, newDay, newWeekIndex, newDayIndex)
    }

    private fun getNextWeek(): FullWorkoutWeek {
        // First we need to sort by sorting index since this isn't done in the SQL query
        val weeks = fullWorkout.weeks.sortedBy { it.week.sortingIndex }

        if (weeks.size - 1 < fullWorkout.workout.currentWorkoutWeek) {
            // We ran out of weeks, reset to week one
            fullWorkout.workout.currentWorkoutWeek = 0
            return weeks.first()
        }
        else {
            // Get our potential new week
            val newWeek = weeks[fullWorkout.workout.currentWorkoutWeek]
            if (newWeek.days.isEmpty()) {
                // This week does not have any days, so skip this one
                fullWorkout.workout.currentWorkoutWeek += 1
                return getNextWeek()
            }
            return newWeek
        }
    }
}*/