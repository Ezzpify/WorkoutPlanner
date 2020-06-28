package com.casper.workouts.callbacks

import com.casper.workouts.room.models.Exercise

interface SearchExerciseSelectedCallback {
    fun onExerciseSelected(exercise: Exercise)
}