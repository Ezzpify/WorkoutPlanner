package com.casper.workouts.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.casper.workouts.R
import com.casper.workouts.dialogs.ErrorDialog
import com.casper.workouts.room.viewmodels.WorkoutViewModel
import com.casper.workouts.utils.WorkoutUtils
import kotlinx.android.synthetic.main.activity_workout_home.*

class WorkoutHomeActivity : AppCompatActivity() {
    private var workoutId: Long = -1L
    private var workoutName: String? = null
    private lateinit var workoutViewModel: WorkoutViewModel

    // Store current full workout
    private lateinit var workoutUtils: WorkoutUtils
    private lateinit var workoutInfo: WorkoutUtils.WorkoutInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_home)

        // Set workout information from intent bundle
        workoutName = intent.getStringExtra(EXTRA_WORKOUT_NAME)
        workoutId = intent.getLongExtra(EXTRA_WORKOUT_UID, -1)
        if (workoutId == -1L) {
            ErrorDialog(this, getString(R.string.error), getString(R.string.general_error_oops)).show()
        }

        // Hide view initially
        home_workout.visibility = View.GONE
        home_no_schedule.visibility = View.GONE

        // Set view data
        workout_title.text = workoutName

        // Init workout view model to get full workout data
        workoutViewModel = ViewModelProvider(this).get(WorkoutViewModel::class.java)
        workoutViewModel.getFullWorkout(workoutId).observe(this, Observer { fullWorkout ->
            // Hide main views
            home_workout.visibility = View.GONE
            home_no_schedule.visibility = View.GONE

            // Check so workout have proper data before attempting to set up workout
            if (fullWorkout.hasNecessaryData()) {
                home_workout.visibility = View.VISIBLE

                this.workoutUtils = WorkoutUtils(fullWorkout)
                setNextWorkoutDay()
            }
            else {
                home_no_schedule.visibility = View.VISIBLE
            }
        })
    }

    private fun setNextWorkoutDay() {
        workoutInfo = workoutUtils.getWorkoutInfo()

        // Set UI information
        workout_week_name.text = workoutInfo.workoutWeek.week.name
        workout_day_name.text = workoutInfo.workoutDay.day.name
    }

    override fun onPause() {
        updateWorkout()
        super.onPause()
    }

    override fun onDestroy() {
        updateWorkout()
        super.onDestroy()
    }

    private fun updateWorkout() {
        if (this::workoutUtils.isInitialized) {
            val workout = workoutUtils.getWorkout()
            workout.currentWorkoutWeek = workoutInfo.weekIndex
            workout.currentWorkoutDay = workoutInfo.dayIndex
            workout.updateDate()
            workoutViewModel.update(workout)
        }
    }

    fun onEditWorkoutButtonClicked(view: View) {
        goToEditWorkout()
    }

    fun onStartWorkoutButtonClicked(view: View) {
        val intent = Intent(this, WorkoutStartActivity::class.java)
        intent.putExtra(EXTRA_WORKOUT_START, workoutInfo)
        startActivity(intent)
    }

    fun onSkipWorkoutButtonClicked(view: View) {
        workoutUtils.moveDayForward()
        setNextWorkoutDay()
    }

    private fun goToEditWorkout() {
        val intent = Intent(this, WorkoutWeekListActivity::class.java)
        intent.putExtra(EXTRA_WORKOUT_UID, workoutId)
        intent.putExtra(EXTRA_WORKOUT_NAME, workoutName)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_WORKOUT_UID = "EXTRA_UID"
        const val EXTRA_WORKOUT_NAME = "EXTRA_NAME"
        const val EXTRA_WORKOUT_START = "EXTRA_NAME"
    }
}