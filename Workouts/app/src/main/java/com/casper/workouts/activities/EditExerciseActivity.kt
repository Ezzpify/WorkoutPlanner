package com.casper.workouts.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.casper.workouts.R
import com.casper.workouts.adapters.WorkoutExerciseAdapter.Companion.EXTRA_EXERCISE
import com.casper.workouts.custom.getFilePath
import com.casper.workouts.custom.loadUrl
import com.casper.workouts.data.UserData
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.viewmodels.ExerciseViewModel
import com.casper.workouts.utils.FileUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_exercise_edit.exercise_description
import kotlinx.android.synthetic.main.activity_exercise_edit.exercise_image
import kotlinx.android.synthetic.main.activity_exercise_edit.exercise_image_overlay
import kotlinx.android.synthetic.main.activity_exercise_edit.exercise_name
import kotlinx.android.synthetic.main.activity_exercise_edit.exercise_sets
import kotlinx.android.synthetic.main.activity_exercise_edit.exercise_tag
import kotlinx.android.synthetic.main.activity_exercise_edit.exercise_weight
import kotlinx.android.synthetic.main.activity_exercise_edit.exercise_weight_reps
import kotlinx.android.synthetic.main.activity_exercise_edit.exercise_weight_unit
import kotlinx.android.synthetic.main.activity_exercise_edit.timer_checkbox
import kotlinx.android.synthetic.main.activity_exercise_edit.timer_seconds
import java.io.File

class EditExerciseActivity : AppCompatActivity() {
    private lateinit var exercise: Exercise
    private lateinit var exerciseViewModel: ExerciseViewModel

    private var exerciseImageLocalPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_edit)

        // Set exercise information from intent bundle
        exercise = intent.getSerializableExtra(EXTRA_EXERCISE) as Exercise

        // Set all UI data
        exercise_name.setText(exercise.name)
        exercise_tag.setText(exercise.muscleWorked)
        exercise_description.setText(exercise.description)

        if (exercise.weightUnit.isNullOrEmpty() && UserData(this).lastUsedWeightUnit.isNotEmpty()) {
            exercise_weight_unit.setText(UserData(this).lastUsedWeightUnit)
        }
        else {
            exercise_weight_unit.setText(exercise.weightUnit)
        }

        exercise.weight?.let { exercise_weight.setText(it.toString()) }
        exercise.reps?.let { exercise_weight_reps.setText(it.toString()) }
        exercise.sets?.let { exercise_sets.setText(it.toString()) }

        timer_seconds.setText(exercise.timerSeconds.toString())
        timer_checkbox.isChecked = exercise.timerEnabled

        exercise.imageUrl?.let {
            if (FileUtils().isLocalFile(it)) {
                Picasso.get().load(File(it)).placeholder(R.drawable.default_workout_image).into(exercise_image)
            }
            else {
                Picasso.get().load(it).placeholder(R.drawable.default_workout_image).into(exercise_image)
            }
        }

        // Get all exercise tags so we can autocomplete tag EditText
        exerciseViewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)
        exerciseViewModel.allExerciseTags.observe(this, Observer { tags ->
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tags)
            exercise_tag.setAdapter(arrayAdapter)
        })
    }

    fun onSaveButtonClicked(view: View) {
        var error = false
        val errorText = getString(R.string.required)

        val exerciseName = exercise_name.text.toString().trim() // Required
        val exerciseTag = exercise_tag.text.toString().trim() // Required
        val exerciseDesc = exercise_description.text.toString().trim()
        val exerciseWeight = exercise_weight.text.toString().trim()
        val exerciseUnit = exercise_weight_unit.text.toString().trim()
        val exerciseSets = exercise_sets.text.toString().trim()
        val exerciseReps = exercise_weight_reps.text.toString().trim()
        val timerSeconds = timer_seconds.text.toString().trim()
        val timerEnabled = timer_checkbox.isChecked

        if (exerciseName.isEmpty()) {
            exercise_name.error = errorText
            error = true
        }

        if (exerciseTag.isEmpty()) {
            exercise_tag.error = errorText
            error = true
        }

        if (error) {
            return
        }

        // We will save the weight unit (if one was submitted) for future use
        if (exerciseUnit.isNotEmpty()) {
            UserData(this).lastUsedWeightUnit = exerciseUnit
        }

        // Convert some values to appropriate data type
        val exerciseWeightDouble = exerciseWeight.toDoubleOrNull()
        val exerciseSetsInt = exerciseSets.toIntOrNull()
        val exerciseRepsInt = exerciseReps.toIntOrNull()
        val timerSecondsInt = timerSeconds.toIntOrNull()

        // Update exercise object
        exercise.name = exerciseName
        exercise.muscleWorked = exerciseTag
        exercise.description = exerciseDesc
        exercise.weight = exerciseWeightDouble
        exercise.weightUnit = exerciseUnit
        exercise.sets = exerciseSetsInt
        exercise.reps = exerciseRepsInt
        exercise.timerSeconds = timerSecondsInt ?: 0
        exercise.timerEnabled = timerEnabled
        exercise.hasBeenSetUp = true
        exercise.updateDate()

        // Save image and update imagePath
        exerciseImageLocalPath?.let { path ->
            exercise.imageUrl = FileUtils().saveWorkoutImage(this, path)
        }

        // Update exercise data set and finish activity
        exerciseViewModel.update(exercise)
        finish()
    }

    fun onSelectImageClicked(view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectImage()
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                exerciseImageLocalPath = intent.getFilePath(this)
                exercise_image.loadUrl(intent.data)

                // Remove image overlay
                exercise_image_overlay.visibility = View.GONE
                exercise_image.foreground = null
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            }
        }
    }

    companion object {
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 0
        private const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1
    }
}