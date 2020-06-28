package com.casper.workouts.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.casper.workouts.R
import com.casper.workouts.adapters.WorkoutDayAdapter.DayHolder.Companion.EXTRA_DAY_DAY
import com.casper.workouts.room.models.Day
import com.casper.workouts.room.viewmodels.DayViewModel
import kotlinx.android.synthetic.main.activity_day_edit.*

class EditDayActivity : AppCompatActivity() {
    private lateinit var day: Day

    private lateinit var dayViewModel: DayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_edit)

        // Set day information from intent bundle
        day = intent.getSerializableExtra(EXTRA_DAY_DAY) as Day

        // Set all UI data
        day_name.setText(day.name)
        day_description.setText(day.description)

        // Day view model for updating data set
        dayViewModel = ViewModelProvider(this).get(DayViewModel::class.java)
    }

    fun onSaveDayButtonClicked(view: View) {
        var error = false
        val errorText = getString(R.string.required)

        val dayName = day_name.text.toString().trim() // Required
        val dayDescription = day_description.text.toString().trim()

        if (dayName.isEmpty()) {
            day_name.error = errorText
            error = true
        }

        if (error) {
            return
        }

        // Update exercise object
        day.name = dayName
        day.description = dayDescription
        day.updateDate()

        // Update data set and finish
        dayViewModel.update(day)
        finish()
    }
}