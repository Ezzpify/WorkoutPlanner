package com.casper.workouts.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.casper.workouts.R
import com.casper.workouts.custom.getFilePath
import com.casper.workouts.custom.loadUrl
import kotlinx.android.synthetic.main.activity_create_workout.*

class CreateWorkoutActivity : AppCompatActivity() {
    private var workoutImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_workout)
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

    fun onSaveWorkoutButtonClicked(view: View) {
        var error = false
        val errorText = getString(R.string.required)

        val workoutName = workout_name.text.toString().trim()
        val workoutDesc = workout_description.text.toString().trim()

        if (workoutName.isEmpty()) {
            workout_name.error = errorText
            error = true
        }

        if (error) {
            return
        }

        val replyIntent = Intent()
        replyIntent.putExtra(EXTRA_REPLY_NAME, workoutName)
        replyIntent.putExtra(EXTRA_REPLY_DESC, workoutDesc)
        replyIntent.putExtra(EXTRA_REPLY_IMAGE, workoutImagePath)

        setResult(Activity.RESULT_OK, replyIntent)
        finish()
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

    companion object {
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 0
        private const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1

        const val EXTRA_REPLY_NAME = "REPLY_NAME"
        const val EXTRA_REPLY_DESC = "REPLY_DESC"
        const val EXTRA_REPLY_IMAGE = "REPLY_IMAGE"
    }
}