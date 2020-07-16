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
import com.casper.workouts.adapters.WorkoutWeekAdapter
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity: AppCompatActivity(), Chronometer.OnChronometerTickListener {
    private lateinit var countdownTimer: CountDownTimer

    // Chronometer
    private var timerRunning = false
    private var timeWhenPaused = 0L
    private var countDownTime = 0
    private var timerFinished = false

    // Vibration
    private var vibrationTimeMs = 0
    private var vibrationInterval = 15000
    private val intervalVibrationMs = 350L
    private val completeVibrationMs = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorAccent)

        setContentView(R.layout.activity_timer)

        // Set countdown time and initial vibration time (countDownTime - first vibration)
        countDownTime = intent.getIntExtra(EXTRA_COUNTDOWN_TIME, 0) * 1000
        vibrationTimeMs = countDownTime - vibrationInterval

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
        if (timerFinished) {
            return
        }

        toggleTimer()
    }

    fun onResetButtonClicked(view: View) {
        startCountDown()
    }

    fun onCancelButtonClicked(view: View) {
        super.onBackPressed()
    }

    override fun onChronometerTick(chronometer: Chronometer) {
        // Get the elapsed time in ms from when clock was started
        val elapsedTime = chronometer.base - SystemClock.elapsedRealtime()

        // If timer ran out
        if (elapsedTime <= 0) {
            timerFinished = true
            timer.stop()
            vibrate(completeVibrationMs)
        }
        else {
            // If elapsed time exceeded our vibration time, append the vibration interval
            // for next time it should vibrate, then vibrate the device.
            // We add 1000ms to vibrationTime so it vibrates on example 50 rather than 49 (which would be actually, but doesn't look good)
            if (elapsedTime <= vibrationTimeMs + 1000) {
                vibrationTimeMs -= vibrationInterval
                vibrate(intervalVibrationMs)
            }
        }
    }

    private fun startCountDown() {
        countdown_text.visibility = View.VISIBLE
        timer_parent.visibility = View.GONE
        cancel_button.visibility = View.GONE

        // Reset all values
        play.setImageResource(R.drawable.ic_baseline_pause_24)
        vibrationTimeMs = countDownTime - vibrationInterval
        timeWhenPaused = 0L
        timerRunning = false
        timerFinished = false

        // Animation for showing cool countdown effect :DDDDDDDDDDD
        val animation = AnimationUtils.loadAnimation(this, R.anim.countdown_animation)
        countdownTimer = object: CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdown_text.text = (millisUntilFinished / 1000 + 1).toString()
                countdown_text.startAnimation(animation)
            }

            override fun onFinish() {
                countdown_text.visibility = View.GONE
                timer_parent.visibility = View.VISIBLE
                cancel_button.visibility = View.VISIBLE

                // Reset the start time of the timer
                timer.base = SystemClock.elapsedRealtime() + countDownTime
                timer.start()
                timerRunning = true

                // Vibrate to indicate that it started
                vibrate(intervalVibrationMs)

                // Animate the control buttons
                val fallDownAnimation = AnimationUtils.loadAnimation(this@TimerActivity, R.anim.item_animation_fall_down)
                control_parent.startAnimation(fallDownAnimation)
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

    private fun vibrate(milliseconds: Long) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    companion object {
        const val EXTRA_COUNTDOWN_TIME = "COUNTDOWN_TIME"
    }
}