package com.casper.workouts.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.models.dayjunctions.DayExerciseCrossRef
import com.casper.workouts.room.models.dayjunctions.DayWithExercises

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM Exercises ORDER BY Name ASC")
    fun getAllExercises(): LiveData<List<Exercise>>

    @Query("SELECT DISTINCT Tag FROM Exercises ORDER BY Name ASC")
    fun getAllExerciseTags(): LiveData<List<String>>

    @Transaction
    @Query("SELECT * FROM Days WHERE DayID = :dayId")
    fun getExercises(dayId: Long): LiveData<DayWithExercises>

    @Update
    fun update(exercise: Exercise)

    @Update
    fun update(exercises: List<Exercise>)

    @Update
    fun updateExtras(exerciseExtras: List<DayExerciseCrossRef>)

    @Query("DELETE FROM DayExerciseCrossRef WHERE DayID = :dayId AND ExerciseID = :exerciseId")
    fun deleteJunction(dayId: Long, exerciseId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(dayExerciseCrossRef: DayExerciseCrossRef)
}