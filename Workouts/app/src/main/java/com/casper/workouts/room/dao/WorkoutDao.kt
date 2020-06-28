package com.casper.workouts.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.casper.workouts.room.models.FullWorkout
import com.casper.workouts.room.models.Workout

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM Workouts ORDER BY SortingIndex")
    fun getWorkouts(): LiveData<List<Workout>>

    @Transaction
    @Query("SELECT * FROM Workouts WHERE WorkoutID = :workoutId")
    fun getFullWorkout(workoutId: Long): LiveData<FullWorkout>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(workout: Workout)

    @Update
    fun update(workout: Workout)

    @Update
    fun update(workouts: List<Workout>)
}