package com.casper.workouts.room.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.casper.workouts.room.MyDatabase
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.models.junctions.DayExerciseCrossRef
import com.casper.workouts.room.models.junctions.DayWithExercises
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

    fun delete(exercise: Exercise)  = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(exercise)
    }

    fun deleteJunction(dayId: Long, exerciseId: Long) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteJunction(dayId, exerciseId)
    }

    fun updateExtras(exerciseExtras: List<DayExerciseCrossRef>) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateExtras(exerciseExtras)
    }

    fun getExercises(dayId: Long): LiveData<DayWithExercises> {
        return repository.getExercises(dayId)
    }
}