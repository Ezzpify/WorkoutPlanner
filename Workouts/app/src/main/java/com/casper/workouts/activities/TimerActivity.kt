package com.casper.workouts.activities

import android.content.Context
import android.os.*
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.casper.workouts.R
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity: AppCompatActivity(), Chronometer.OnChronometerTickListener {
    private lateinit var countdownTimer: CountDownTimer

    // Chronometer
    private var timerRunning = false
    private var timeWhenPaused = 0L

    // Vibration
    private var vibrationTimeMs = 15000
    private var vibrationIntervalMs = 15000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorAccent)

        setContentView(R.layout.activity_timer)

        timer.onChronometerTickListener = this
        startCountDown()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // Cancel so it doesn't try to start timer if user went back
        if (this::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }
    }

    fun onPlayButtonClicked(view: View) {
        toggleTimer()
    }

    fun onResetButtonClicked(view: View) {
        startCountDown()
    }

    fun onCancelButtonClicked(view: View) {
        super.onBackPressed()
    }

    override fun onChronometerTick(chronometer: Chronometer) {
        val elapsedTime = SystemClock.elapsedRealtime() - chronometer.base

        if (elapsedTime >= vibrationTimeMs) {
            vibrationTimeMs += vibrationIntervalMs
            vibrate()
        }
    }

    private fun startCountDown() {
        countdown_text.visibility = View.VISIBLE
        timer_parent.visibility = View.GONE
        cancel_button.visibility = View.GONE

        // Reset all values
        play.setImageResource(R.drawable.ic_baseline_pause_24)
        vibrationTimeMs = vibrationIntervalMs
        timeWhenPaused = 0L
        timerRunning = false

        // Animation for showing cool countdown effect :DDDDDDDDDDD
        val animation = AnimationUtils.loadAnimation(this, R.anim.countdown_animation)

        countdownTimer = object: CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdown_text.text = (millisUntilFinished / 1000 + 1).toString()
                countdown_text.startAnimation(animation)
            }

            override fun onFinish() {
                countdown_text.visibility = View.GONE
                timer_parent.visibility = View.VISIBLE
                cancel_button.visibility = View.VISIBLE

                // Reset the start time of the timer
                timer.base = SystemClock.elapsedRealtime()
                timer.start()
                timerRunning = true
                vibrate()
            }
        }
        countdownTimer.start()
    }

    private fun toggleTimer() {
        if (timerRunning) {
            timer.stop()
            timerRunning = false
            play.setImageResource(R.drawable.ic_baseline_play_arrow_24)

            // Save time when paused so we can resume at same time
            timeWhenPaused = timer.base - SystemClock.elapsedRealtime()
        }
        else {
            timer.start()
            timerRunning = true
            play.setImageResource(R.drawable.ic_baseline_pause_24)

            // Start at the same time on which we paused
            timer.base = SystemClock.elapsedRealtime() + timeWhenPaused
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}