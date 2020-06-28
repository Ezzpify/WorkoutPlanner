package com.casper.workouts.room.viewmodels.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.casper.workouts.room.dao.ExerciseDao
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.models.dayjunctions.DayExerciseCrossRef
import com.casper.workouts.room.models.dayjunctions.DayWithExercises

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

    @WorkerThread
    suspend fun updateExtras(exerciseExtras: List<DayExerciseCrossRef>) {
        exerciseDao.updateExtras(exerciseExtras)
    }

    fun getExercises(dayId: Long) : LiveData<DayWithExercises> {
        return exerciseDao.getExercises(dayId)
    }
}