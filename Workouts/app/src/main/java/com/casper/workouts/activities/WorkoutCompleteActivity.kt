package com.casper.workouts.activities

import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.casper.workouts.R
import com.casper.workouts.data.UserData

class WorkoutCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorAccent)

        setContentView(R.layout.activity_workout_complete)

        Handler().postDelayed({
            finish()
        }, 1500)
    }
}