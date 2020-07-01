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
import com.casper.workouts.custom.getFilePath
import com.casper.workouts.custom.loadUrl
import com.casper.workouts.data.UserData
import com.casper.workouts.room.viewmodels.ExerciseViewModel
import kotlinx.android.synthetic.main.activity_create_exercise.*
import kotlinx.android.synthetic.main.activity_create_workout.*

class CreateExerciseActivity : AppCompatActivity() {
    private var exerciseImagePath: String? = null

    private lateinit var exerciseViewModel: ExerciseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_exercise)

        // Set weight unit if we have one saved
        val savedWeightUnit = UserData(this).lastUsedWeightUnit
        if (savedWeightUnit.isNotEmpty()) {
            exercise_weight_unit.setText(savedWeightUnit)
        }

        // Get all exercise tags so we can autocomplete tag EditText
        exerciseViewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)
        exerciseViewModel.allExerciseTags.observe(this, Observer { tags ->
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tags)
            exercise_tag.setAdapter(arrayAdapter)
        })
    }

    fun onSelectImageClicked(view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectImage()
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
        }
    }

    fun onSaveExerciseButtonClicked(view: View) {
        var error = false
        val errorText = getString(R.string.required)

        val exerciseName = exercise_name.text.toString().trim() // Required
        val exerciseTag = exercise_tag.text.toString().trim() // Required
        val exerciseDesc = exercise_description.text.toString().trim()
        val exerciseWeight = exercise_weight.text.toString().trim()
        val exerciseUnit = exercise_weight_unit.text.toString().trim()
        val exerciseSets = exercise_sets.text.toString().trim()
        val exerciseReps = exercise_weight_reps.text.toString().trim()
        val deloadPercentage = deload_percentage.text.toString().trim()

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
        val deloadPercentageInt = deloadPercentage.toIntOrNull()

        // Send back our reply intent with all the data
        val replyIntent = Intent()
        replyIntent.putExtra(EXTRA_REPLY_NAME, exerciseName)
        replyIntent.putExtra(EXTRA_REPLY_TAG, exerciseTag)
        replyIntent.putExtra(EXTRA_REPLY_DESC, exerciseDesc)
        replyIntent.putExtra(EXTRA_REPLY_WEIGHT, exerciseWeightDouble)
        replyIntent.putExtra(EXTRA_REPLY_UNIT, exerciseUnit)
        replyIntent.putExtra(EXTRA_REPLY_SETS, exerciseSetsInt)
        replyIntent.putExtra(EXTRA_REPLY_REPS, exerciseRepsInt)
        replyIntent.putExtra(EXTRA_REPLY_DELOAD, deloadPercentageInt)
        replyIntent.putExtra(EXTRA_REPLY_IMAGE, exerciseImagePath)

        setResult(Activity.RESULT_OK, replyIntent)
        finish()
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
                exerciseImagePath = intent.getFilePath(this)
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

        const val EXTRA_REPLY_NAME = "REPLY_NAME"
        const val EXTRA_REPLY_TAG = "REPLY_TAG"
        const val EXTRA_REPLY_DESC = "REPLY_DESC"
        const val EXTRA_REPLY_WEIGHT = "REPLY_WEIGHT"
        const val EXTRA_REPLY_UNIT = "REPLY_UNIT"
        const val EXTRA_REPLY_SETS = "REPLY_SETS"
        const val EXTRA_REPLY_REPS = "REPLY_REPS"
        const val EXTRA_REPLY_DELOAD = "REPLY_DELOAD"
        const val EXTRA_REPLY_IMAGE = "REPLY_IMAGE"
    }
}