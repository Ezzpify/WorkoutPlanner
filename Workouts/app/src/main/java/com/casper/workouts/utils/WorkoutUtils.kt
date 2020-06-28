package com.casper.workouts.utils

import com.casper.workouts.room.models.FullWorkout
import com.casper.workouts.room.models.FullWorkoutDay
import com.casper.workouts.room.models.FullWorkoutWeek
import com.casper.workouts.room.models.Workout
import java.io.Serializable

class WorkoutUtils(private val fullWorkout: FullWorkout) {
    data class WorkoutInfo(val workoutWeek: FullWorkoutWeek, val workoutDay: FullWorkoutDay, val weekIndex: Int, val dayIndex: Int): Serializable

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

        // Get our potential new day
        val newDay = newWeek.days[fullWorkout.workout.currentWorkoutDay]
        if (newDay.exercises.isEmpty()) {
            // No exercises in this day, move to next day
            fullWorkout.workout.currentWorkoutDay += 1
            return getWorkoutInfo()
        }

        // Get indexes for new week and day
        // Could evaluate these during the loop, but I'm lazy now
        val newWeekIndex = fullWorkout.weeks.indexOfFirst { it.week.weekId == newWeek.week.weekId }
        val newDayIndex = newWeek.days.indexOfFirst { it.day.dayId == newDay.day.dayId }

        // Returns our new workout day
        return WorkoutInfo(newWeek, newDay, newWeekIndex, newDayIndex)
    }

    private fun getNextWeek(): FullWorkoutWeek {
        if (fullWorkout.weeks.size - 1 < fullWorkout.workout.currentWorkoutWeek) {
            // We ran out of weeks, reset to week one
            fullWorkout.workout.currentWorkoutWeek = 0
            return fullWorkout.weeks.first()
        }
        else {
            // Get our potential new week
            val newWeek = fullWorkout.weeks[fullWorkout.workout.currentWorkoutWeek]
            if (newWeek.days.isEmpty()) {
                // This week does not have any days, so skip this one
                fullWorkout.workout.currentWorkoutWeek += 1
                return getNextWeek()
            }
            return newWeek
        }
    }
}