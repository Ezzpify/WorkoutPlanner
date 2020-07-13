package com.casper.workouts.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.casper.workouts.room.dao.DayDao
import com.casper.workouts.room.dao.ExerciseDao
import com.casper.workouts.room.dao.WeekDao
import com.casper.workouts.room.dao.WorkoutDao
import com.casper.workouts.room.models.*
import com.casper.workouts.room.models.junctions.DayExerciseCrossRef
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Workout::class, Week::class, Day::class, Exercise::class, DayExerciseCrossRef::class], exportSchema = false, version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun weekDao(): WeekDao
    abstract fun dayDao(): DayDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var instance: MyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MyDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                Companion.instance = instance
                // return instance
                instance
            }
        }
    }
}