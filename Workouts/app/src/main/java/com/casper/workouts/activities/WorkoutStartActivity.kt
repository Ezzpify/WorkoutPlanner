package com.casper.workouts.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.casper.workouts.R
import com.casper.workouts.activities.WorkoutHomeActivity.Companion.EXTRA_WORKOUT_DAY
import com.casper.workouts.activities.WorkoutHomeActivity.Companion.EXTRA_WORKOUT_WORKOUT
import com.casper.workouts.callbacks.InputDialogCallback
import com.casper.workouts.data.UserData
import com.casper.workouts.dialogs.InputDialog
import com.casper.workouts.room.models.Day
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.models.Workout
import com.casper.workouts.room.viewmodels.ExerciseViewModel
import com.casper.workouts.room.viewmodels.WorkoutViewModel
import com.casper.workouts.utils.FileUtils
import com.casper.workouts.utils.WorkoutUtils
import com.ncorti.slidetoact.SlideToActView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_workout_exercises_list.*
import kotlinx.android.synthetic.main.activity_workout_start.*

class WorkoutStartActivity: AppCompatActivity(), SlideToActView.OnSlideCompleteListener {
    private lateinit var workout: Workout
    private lateinit var day: Day

    private lateinit var exerciseViewModel: ExerciseViewModel
    private lateinit var workoutViewModel: WorkoutViewModel

    // Exercise variables
    private var currentExerciseIndex = 0
    private lateinit var exercises: List<Exercise>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_start)

        // Set workout information from intent bundle
        workout = intent.getSerializableExtra(EXTRA_WORKOUT_WORKOUT) as Workout
        day = intent.getSerializableExtra(EXTRA_WORKOUT_DAY) as Day

        // View models for updating workout and exercise data
        workoutViewModel = ViewModelProvider(this).get(WorkoutViewModel::class.java)
        exerciseViewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)

        // Set up our exercises
        exerciseViewModel.getExercises(day.dayId).observe(this, Observer { day ->
            // Get the sorting index from the junction table and add that value to all exercise objects so we can sort them
            for ((index, value) in day.extras.withIndex()) {
                day.exercises[index].sortingIndex = value.sortingIndex
            }

            exercises = day.exercises.sortedBy { it.sortingIndex }
            displayNextExercise()
        })

        timer_slider.onSlideCompleteListener = this
    }

    private fun displayNextExercise() {
        // Get our current exercise
        val exercise = exercises[currentExerciseIndex]

        // Update UI with basic information
        exercise_name.text = exercise.name
        exercise.description?.let {
            if (it.isNotEmpty()) {
                exercise_description.text = it
            }
            else {
                exercise_description.text = getString(R.string.no_description)
            }
        }

        exercise.imageName?.let { imageName ->
            if (imageName.isNotEmpty()) {
                FileUtils().getWorkoutImage(this, imageName)?.let { image ->
                    Picasso.get().load(image).into(exercise_image)
                }
            }
            else {
                Picasso.get().load(R.drawable.default_workout_image).into(exercise_image)
            }
        }

        // Update UI weight information, hide view if weight is null
        if (exercise.weight != null) {
            exercise_weight.text = getString(R.string.activity_workout_start_weight_format, exercise.weight, exercise.weightUnit)
            weight_parent.visibility = View.VISIBLE
        }
        else {
            weight_parent.visibility = View.GONE
        }

        // Update info panel with reps and sets
        if (exercise.reps == null && exercise.sets == null) {
            info_parent.visibility = View.GONE
        }
        else {
            info_parent.visibility = View.VISIBLE

            // Reps text
            if (exercise.reps != null) {
                exercise_reps.text = getString(R.string.activity_workout_start_reps_format, exercise.reps)
                exercise_reps.visibility = View.VISIBLE
            }
            else {
                exercise_reps.visibility = View.GONE
            }

            // Sets text
            if (exercise.sets != null) {
                exercise_sets.text = getString(R.string.activity_workout_start_sets_format, exercise.sets)
                exercise_sets.visibility = View.VISIBLE
            }
            else {
                exercise_sets.visibility = View.GONE
            }
        }

        // Enable previous button if possible
        previous_exercise_button.visibility = if (currentExerciseIndex > 0) View.VISIBLE else View.GONE

        // Show next or done button if we have another exercise
        val nextExerciseIndex = currentExerciseIndex + 1
        if (exercises.size - 1 < nextExerciseIndex) {
            // No exercises after this one, change next button to done
            next_exercise_button.text = getString(R.string.activity_workout_start_complete_workout)
            next_exercise_button.tag = EXERCISE_BUTTON_COMPLETE
        }
        else {
            next_exercise_button.text = getString(R.string.activity_workout_start_next_exercise)
            next_exercise_button.tag = EXERCISE_BUTTON_NEXT
        }
    }

    fun onPreviousExerciseButtonClicked(view: View) {
        currentExerciseIndex -= 1
        displayNextExercise()
    }

    fun onNextExerciseButtonClicked(view: View) {
        if (view.tag == EXERCISE_BUTTON_NEXT) {
            currentExerciseIndex += 1
            displayNextExercise()
        }
        else {
            exerciseCompleted()
        }
    }

    private fun exerciseCompleted() {
        // Update last workout unix date
        UserData(this).lastWorkoutUnixDate = System.currentTimeMillis()

        //Update workout data and move to next day
        workout.currentWorkoutDay += 1
        workoutViewModel.update(workout)

        // Show splash screen
        val intent = Intent(this, WorkoutCompleteActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onUpdateWeightButtonClicked(view: View) {
        val currentExercise = exercises[currentExerciseIndex]

        InputDialog(this,
            getString(R.string.activity_workout_start_update_weight_dialog_title),
            getString(R.string.activity_workout_start_update_weight_dialog_desc),
            currentExercise.weight.toString(),
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            object: InputDialogCallback {
                override fun result(value: String) {
                    // If new value is same as old then we don't need to change anything
                    if (currentExercise.weight.toString() == value) {
                        return
                    }

                    val newValue = value.toDouble()
                    currentExercise.weight = newValue
                    currentExercise.updateDate()

                    // Update UI and Data set
                    exercise_weight.text = getString(R.string.activity_workout_start_weight_format, newValue, currentExercise.weightUnit)
                    exerciseViewModel.update(currentExercise)
                }
            }).show()
    }

    override fun onSlideComplete(view: SlideToActView) {
        val intent = Intent(this, TimerActivity::class.java)
        startActivity(intent)
        timer_slider.resetSlider()
    }

    companion object {
        const val EXERCISE_BUTTON_NEXT = "EXERCISE_NEXT"
        const val EXERCISE_BUTTON_COMPLETE = "EXERCISE_COMPLETE"
    }
}