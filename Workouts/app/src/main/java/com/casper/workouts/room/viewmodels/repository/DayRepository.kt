package com.casper.workouts.room.viewmodels.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.casper.workouts.room.dao.DayDao
import com.casper.workouts.room.dao.WeekDao
import com.casper.workouts.room.models.Day
import com.casper.workouts.room.models.Week

class DayRepository(private val dayDao: DayDao) {
    val allDays: LiveData<List<Day>> = dayDao.getDays()

    fun getDays(weekId: Long): LiveData<List<Day>> {
        return dayDao.getDays(weekId)
    }

    @WorkerThread
    suspend fun insert(day: Day) {
        dayDao.insert(day)
    }

    @WorkerThread
    suspend fun update(day: Day) {
        dayDao.update(day)
    }

    @WorkerThread
    suspend fun update(days: List<Day>) {
        dayDao.update(days)
    }
}