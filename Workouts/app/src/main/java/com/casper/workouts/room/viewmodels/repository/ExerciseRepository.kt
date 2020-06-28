package com.casper.workouts.room.viewmodels.repository

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.casper.workouts.room.dao.DayDao
import com.casper.workouts.room.dao.ExerciseDao
import com.casper.workouts.room.models.Day
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.models.day.DayExerciseCrossRef
import com.casper.workouts.room.models.day.DayWithExercises

class ExerciseRepository(private val exerciseDao: ExerciseDao) {
    val allExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises()
    val allExerciseTags: LiveData<List<String>> = exerciseDao.getAllExerciseTags()

    @WorkerThread
    suspend fun insert(exercise: Exercise): Long {
        return exerciseDao.insert(exercise)
    }

    @WorkerThread
    suspend fun insert(dayExerciseCrossRef: DayExerciseCrossRef) {
        exerciseDao.insert(dayExerciseCrossRef)
    }

    @WorkerThread
    suspend fun update(exercise: Exercise) {
        exerciseDao.update(exercise)
    }

    @WorkerThread
    suspend fun update(exercises: List<Exercise>) {
        exerciseDao.update(exercises)
    }

    fun getExercises(dayId: Long) : LiveData<DayWithExercises> {
        return exerciseDao.getExercises(dayId)
    }
}