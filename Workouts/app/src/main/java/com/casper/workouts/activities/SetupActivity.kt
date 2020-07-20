package com.casper.workouts.activities

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.casper.workouts.R
import com.casper.workouts.data.UserData
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.viewmodels.ExerciseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class SetupActivity: AppCompatActivity() {
    data class ExternalExercise(val imageUrl: String, val exerciseName: String, val muscleWorked: String, val equipmentType: String, val exerciseLink: String, val exerciseDescription: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorAccent)

        setContentView(R.layout.activity_setup)

        GlobalScope.launch {
            saveExternalExercisesToDatabase()
        }
    }

    private  fun saveExternalExercisesToDatabase() {
        // For saving the exercises
        val exerciseViewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)

        // Get the json and deserialize it
        val jsonFileString = getExerciseJsonFromAsset(applicationContext, "exercises.json")
        val listExerciseType = object : TypeToken<List<ExternalExercise>>(){}.type
        val exercises: List<ExternalExercise> = Gson().fromJson(jsonFileString, listExerciseType)

        // Go through each object and save it
        for (exercise in exercises) {
            val newExercise = Exercise(0,
                exercise.exerciseName,
                exercise.muscleWorked,
                exercise.exerciseDescription,
                null,
                null,
                null,
                null,
                0,
                false,
                exercise.imageUrl,
                false)

            exerciseViewModel.insert(newExercise)
        }

        UserData(this).firstTimeSetupCompleted = true

        // Finish in a second in case operation was too fast
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 1000)
    }

    private fun getExerciseJsonFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}