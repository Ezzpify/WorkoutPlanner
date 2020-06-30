package com.casper.workouts.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.casper.workouts.room.models.Week

@Dao
interface WeekDao {
    @Query("SELECT * FROM Weeks")
    fun allWeeks(): LiveData<List<Week>>

    @Query("SELECT * FROM Weeks WHERE WorkoutID = :workoutId ORDER BY SortingIndex")
    fun getWeeks(workoutId: Long): LiveData<List<Week>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(week: Week)

    @Update
    fun update(week: Week)

    @Delete
    fun delete(week: Week)

    @Update
    fun update(weeks: List<Week>)
}