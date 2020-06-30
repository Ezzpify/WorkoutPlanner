package com.casper.workouts.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.casper.workouts.R
import com.casper.workouts.adapters.WorkoutWeekAdapter.Companion.EXTRA_WEEK_WEEK
import com.casper.workouts.room.models.Week
import com.casper.workouts.room.viewmodels.WeekViewModel
import kotlinx.android.synthetic.main.activity_week_edit.*

class EditWeekActivity: AppCompatActivity() {
    private lateinit var week: Week

    private lateinit var weekViewModel: WeekViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_edit)

        // Set day information from intent bundle
        week = intent.getSerializableExtra(EXTRA_WEEK_WEEK) as Week

        // Set all UI data
        week_name.setText(week.name)
        week_description.setText(week.description)

        // Day view model for updating data set
        weekViewModel = ViewModelProvider(this).get(WeekViewModel::class.java)
    }

    fun onSaveWeekButtonClicked(view: View) {
        var error = false
        val errorText = getString(R.string.required)

        val weekName = week_name.text.toString().trim() // Required
        val weekDescription = week_description.text.toString().trim()

        if (weekName.isEmpty()) {
            week_name.error = errorText
            error = true
        }

        if (error) {
            return
        }

        // Update exercise object
        week.name = weekName
        week.description = weekDescription
        week.updateDate()

        // Update data set and finish
        weekViewModel.update(week)
        finish()
    }
}