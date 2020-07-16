package com.casper.workouts.fragments

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.casper.workouts.R
import com.casper.workouts.activities.TimerActivity
import com.casper.workouts.adapters.WorkoutListAdapter
import com.casper.workouts.callbacks.InputDialogCallback
import com.casper.workouts.dialogs.InputDialog
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.viewmodels.ExerciseViewModel
import com.casper.workouts.utils.FileUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_exercise.*

class ExerciseFragment: Fragment() {
    private lateinit var exerciseViewModel: ExerciseViewModel

    companion object {
        private const val ARG_EXERCISE = "ARG_EXERCISE"

        fun newInstance(exercise: Exercise) = ExerciseFragment().apply {
            arguments = bundleOf(
                ARG_EXERCISE to exercise)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? = inflater.inflate(R.layout.fragment_exercise, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val exercise = it.getSerializable(ARG_EXERCISE) as Exercise

            exerciseViewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)

            // Basic exercise information
            exercise_name.text = exercise.name
            exercise.description?.let {
                exercise_description.text = if (it.isEmpty()) getString(R.string.no_description) else it
            }

            // Exercise image
            exercise.imageName?.let { imageName ->
                if (imageName.isNotEmpty()) {
                    FileUtils().getWorkoutImage(context!!, imageName)?.let { image ->
                        Picasso.get().load(image).into(exercise_image)
                    }
                }
                else {
                    Picasso.get().load(R.drawable.default_workout_image).into(exercise_image)
                }
            }

            // Exercise weight
            if (exercise.weight != null) {
                exercise_weight.text = getString(R.string.activity_workout_start_weight_format, exercise.weight, exercise.weightUnit)
                weight_parent.visibility = View.VISIBLE

                update_weight_button.setOnClickListener {
                    InputDialog(context!!,
                        getString(R.string.activity_workout_start_update_weight_dialog_title),
                        getString(R.string.activity_workout_start_update_weight_dialog_desc),
                        exercise.weight.toString(),
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
                        object: InputDialogCallback {
                            override fun result(value: String) {
                                // If new value is same as old then we don't need to change anything
                                if (exercise.weight.toString() == value) {
                                    return
                                }

                                val newValue = value.toDouble()
                                exercise.weight = newValue
                                exercise.updateDate()

                                // Update UI and Data set
                                exercise_weight.text = getString(R.string.activity_workout_start_weight_format, newValue, exercise.weightUnit)
                                exerciseViewModel.update(exercise)
                            }
                        }).show()
                }
            }
            else {
                weight_parent.visibility = View.GONE
            }

            // Exercise reps & sets
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

            // Exercise timer
            if (exercise.timerEnabled && exercise.timerSeconds > 0) {
                start_timer_button.text = getString(R.string.activity_workout_start_start_timer, exercise.timerSeconds)
                start_timer_button.setOnClickListener {
                    val intent = Intent(activity, TimerActivity::class.java)
                    intent.putExtra(TimerActivity.EXTRA_COUNTDOWN_TIME, exercise.timerSeconds)
                    startActivity(intent)
                }
            }
            else {
                timer_parent.visibility = View.GONE
            }
        }
    }
}