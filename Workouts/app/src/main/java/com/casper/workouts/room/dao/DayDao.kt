package com.casper.workouts.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.casper.workouts.room.models.Day
import com.casper.workouts.room.models.Week

@Dao
interface DayDao {
    @Query("SELECT * FROM Days")
    fun getDays(): LiveData<List<Day>>

    @Query("SELECT * FROM Days WHERE WeekID = :weekId ORDER BY SortingIndex")
    fun getDays(weekId: Long): LiveData<List<Day>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(day: Day)

    @Update
    fun update(day: Day)

    @Update
    fun update(days: List<Day>)
}