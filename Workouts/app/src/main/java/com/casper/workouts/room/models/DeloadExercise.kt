package com.casper.workouts.room.models

import androidx.room.*

@Entity(tableName = "DeloadExercises",
    foreignKeys = [ForeignKey(entity = Exercise::class,
        parentColumns = arrayOf("ExerciseID"),
        childColumns = arrayOf("ExerciseID"),
        onDelete = ForeignKey.CASCADE)],
    indices = [Index("ExerciseID")]
)
data class DeloadExercise (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DeloadExerciseID") val deloadExerciseID: Long,

    @ColumnInfo(name = "ExerciseID") val exerciseId: Long
)