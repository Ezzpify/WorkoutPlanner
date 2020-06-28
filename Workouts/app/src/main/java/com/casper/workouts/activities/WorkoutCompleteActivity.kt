package com.casper.workouts.activities

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.casper.workouts.R
import com.casper.workouts.data.UserData

class WorkoutCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_complete)

        Handler().postDelayed({
            finish()
        }, 1500)
    }
}