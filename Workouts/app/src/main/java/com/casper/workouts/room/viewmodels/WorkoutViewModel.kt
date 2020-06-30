package com.casper.workouts.room.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.casper.workouts.room.MyDatabase
import com.casper.workouts.room.models.FullWorkout
import com.casper.workouts.room.models.Workout
import com.casper.workouts.room.viewmodels.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WorkoutRepository
    val allWorkouts: LiveData<List<Workout>>

    init {
        val workoutDao = MyDatabase.getDatabase(application, viewModelScope).workoutDao()
        repository =
            WorkoutRepository(
                workoutDao
            )
        allWorkouts = repository.allWorkouts
    }

    fun insert(workout: Workout) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(workout)
    }

    fun update(workout: Workout) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(workout)
    }

    fun delete(workout: Workout) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(workout)
    }

    fun update(workouts: List<Workout>) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(workouts)
    }

    fun getFullWorkout(workoutId: Long): LiveData<FullWorkout> {
        return repository.getFullWorkout(workoutId)
    }
}