package com.casper.workouts.room.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.casper.workouts.room.MyDatabase
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.models.day.DayExerciseCrossRef
import com.casper.workouts.room.models.day.DayWithExercises
import com.casper.workouts.room.viewmodels.repository.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExerciseRepository
    val allExercises: LiveData<List<Exercise>>
    val allExerciseTags: LiveData<List<String>>

    init {
        val exerciseDao = MyDatabase.getDatabase(application, viewModelScope).exerciseDao()
        repository = ExerciseRepository(exerciseDao)
        allExercises = repository.allExercises
        allExerciseTags = repository.allExerciseTags
    }

    fun insert(exercise: Exercise): LiveData<Long> {
        var liveData = MutableLiveData<Long>()
        viewModelScope.launch(Dispatchers.IO) {
            liveData.postValue(repository.insert(exercise))
        }
        return liveData
    }

    fun insert(dayExerciseCrossRef: DayExerciseCrossRef) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(dayExerciseCrossRef)
    }

    fun update(exercise: Exercise) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(exercise)
    }

    fun update(exercises: List<Exercise>) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(exercises)
    }

    fun getExercises(dayId: Long): LiveData<DayWithExercises> {
        return repository.getExercises(dayId)
    }
}