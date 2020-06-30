package com.casper.workouts.room.viewmodels.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.casper.workouts.room.dao.WeekDao
import com.casper.workouts.room.models.Week
import com.casper.workouts.room.models.Workout

class WeekRepository(private val weekDao: WeekDao) {
    val allWeeks: LiveData<List<Week>> = weekDao.allWeeks()

    @WorkerThread
    suspend fun insert(week: Week) {
        weekDao.insert(week)
    }

    @WorkerThread
    suspend fun update(week: Week) {
        weekDao.update(week)
    }

    @WorkerThread
    suspend fun delete(week: Week) {
        weekDao.delete(week)
    }

    @WorkerThread
    suspend fun update(weeks: List<Week>) {
        weekDao.update(weeks)
    }

    fun getWeeks(workoutId: Long): LiveData<List<Week>> {
        return weekDao.getWeeks(workoutId)
    }
}