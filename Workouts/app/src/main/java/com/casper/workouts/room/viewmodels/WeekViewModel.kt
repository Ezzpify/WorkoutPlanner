package com.casper.workouts.room.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.casper.workouts.room.MyDatabase
import com.casper.workouts.room.models.Week
import com.casper.workouts.room.viewmodels.repository.WeekRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeekViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WeekRepository
    val allWeeks: LiveData<List<Week>>

    init {
        val weekDao = MyDatabase.getDatabase(application, viewModelScope).weekDao()
        repository =
            WeekRepository(
                weekDao
            )
        allWeeks = repository.allWeeks
    }

    fun insert(week: Week) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(week)
    }

    fun update(week: Week) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(week)
    }

    fun update(weeks: List<Week>) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(weeks)
    }

    fun getWeeks(workoutId: Long): LiveData<List<Week>> {
        return repository.getWeeks(workoutId)
    }
}