package com.casper.workouts.room.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.casper.workouts.room.MyDatabase
import com.casper.workouts.room.models.Day
import com.casper.workouts.room.models.Week
import com.casper.workouts.room.viewmodels.repository.DayRepository
import com.casper.workouts.room.viewmodels.repository.WeekRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DayViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DayRepository
    val allDays: LiveData<List<Day>>

    init {
        val dayDao = MyDatabase.getDatabase(application, viewModelScope).dayDao()
        repository =
            DayRepository(
                dayDao
            )
        allDays = repository.allDays
    }

    fun insert(day: Day) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(day)
    }

    fun update(day: Day) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(day)
    }

    fun delete(day: Day) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(day)
    }

    fun update(days: List<Day>) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(days)
    }

    fun getDays(weekId: Long): LiveData<List<Day>> {
        return repository.getDays(weekId)
    }
}