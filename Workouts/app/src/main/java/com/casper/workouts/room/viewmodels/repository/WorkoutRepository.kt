package com.casper.workouts.room.viewmodels.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.casper.workouts.room.dao.WorkoutDao
import com.casper.workouts.room.models.FullWorkout
import com.casper.workouts.room.models.Workout

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    val allWorkouts: LiveData<List<Workout>> = workoutDao.getWorkouts()

    @WorkerThread
    suspend fun insert(workout: Workout) {
        workoutDao.insert(workout)
    }

    @WorkerThread
    suspend fun update(workout: Workout) {
        workoutDao.update(workout)
    }

    @WorkerThread
    suspend fun delete(workout: Workout) {
        workoutDao.delete(workout)
    }

    @WorkerThread
    suspend fun update(workouts: List<Workout>) {
        workoutDao.update(workouts)
    }

    fun getFullWorkout(workoutId: Long) : LiveData<FullWorkout> {
        return workoutDao.getFullWorkout(workoutId)
    }
}