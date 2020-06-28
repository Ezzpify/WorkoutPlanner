package com.casper.workouts.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.casper.workouts.R
import com.casper.workouts.adapters.WorkoutListAdapter.WorkoutHolder.Companion.EXTRA_WORKOUT_WORKOUT
import com.casper.workouts.custom.getFilePath
import com.casper.workouts.custom.loadUrl
import com.casper.workouts.room.models.Workout
import com.casper.workouts.room.viewmodels.WorkoutViewModel
import com.casper.workouts.utils.FileUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_workout_edit.*

class EditWorkoutActivity: AppCompatActivity() {
    private lateinit var workout: Workout

    private lateinit var workoutViewModel: WorkoutViewModel

    private var workoutImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_edit)

        // Set day information from intent bundle
        workout = intent.getSerializableExtra(EXTRA_WORKOUT_WORKOUT) as Workout

        // Set all UI data
        workout_name.setText(workout.name)
        workout_description.setText(workout.description)
        workout.imageName?.let {
            if (it.isNotEmpty()) {
                FileUtils().getWorkoutImage(this, it)?.let { image ->
                    Picasso.get().load(image).into(workout_image)
                }
            }
            else {
                Picasso.get().load(R.drawable.default_workout_image).into(workout_image)
            }
        }

        // Day view model for updating data set
        workoutViewModel = ViewModelProvider(this).get(WorkoutViewModel::class.java)
    }

    fun onSelectImageClicked(view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectImage()
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                workoutImagePath = intent.getFilePath(this)
                workout_image.loadUrl(intent.data)

                // Remove image overlay
                workout_image_overlay.visibility = View.GONE
                workout_image.foreground = null
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

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    fun onSaveWorkoutButtonClicked(view: View) {
        var error = false
        val errorText = getString(R.string.required)

        val workoutName = workout_name.text.toString().trim() // Required
        val workoutDescription = workout_description.text.toString().trim()

        if (workoutName.isEmpty()) {
            workout_name.error = errorText
            error = true
        }

        if (error) {
            return
        }

        // Update exercise object
        workout.name = workoutName
        workout.description = workoutDescription
        workout.updateDate()

        // Save image and update imageName
        var workoutImageName = ""
        workoutImagePath?.let { path ->
            workoutImageName = FileUtils().saveWorkoutImage(this, path)
            workout.imageName = workoutImageName
        }

        // Update data set and finish
        workoutViewModel.update(workout)
        finish()
    }

    companion object {
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 0
        private const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1
    }
}