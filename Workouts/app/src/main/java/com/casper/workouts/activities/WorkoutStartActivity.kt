package com.casper.workouts.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.casper.workouts.R
import com.casper.workouts.activities.WorkoutHomeActivity.Companion.EXTRA_WORKOUT_DAY
import com.casper.workouts.activities.WorkoutHomeActivity.Companion.EXTRA_WORKOUT_WORKOUT
import com.casper.workouts.data.UserData
import com.casper.workouts.fragments.adapters.ExerciseFragmentAdapter
import com.casper.workouts.room.models.Day
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.models.Workout
import com.casper.workouts.room.viewmodels.ExerciseViewModel
import com.casper.workouts.room.viewmodels.WorkoutViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.ncorti.slidetoact.SlideToActView
import kotlinx.android.synthetic.main.activity_workout_start.*
import kotlin.math.abs
import kotlin.math.max


class WorkoutStartActivity: AppCompatActivity(), SlideToActView.OnSlideCompleteListener,
    ViewPager2.PageTransformer {
    private lateinit var workout: Workout
    private lateinit var day: Day

    private lateinit var exerciseViewModel: ExerciseViewModel
    private lateinit var workoutViewModel: WorkoutViewModel

    // Exercise variables
    private lateinit var exercises: List<Exercise>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_start)

        // Set workout information from intent bundle
        workout = intent.getSerializableExtra(EXTRA_WORKOUT_WORKOUT) as Workout
        day = intent.getSerializableExtra(EXTRA_WORKOUT_DAY) as Day

        // View models for updating workout and exercise data
        workoutViewModel = ViewModelProvider(this).get(WorkoutViewModel::class.java)
        exerciseViewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)

        // Set up our exercises
        exerciseViewModel.getExercises(day.dayId).observe(this, Observer { day ->
            // Get the sorting index from the junction table and add that value to all exercise objects so we can sort them
            for ((index, value) in day.extras.withIndex()) {
                day.exercises[index].sortingIndex = value.sortingIndex
            }

            exercises = day.exercises.sortedBy { it.sortingIndex }

            val exerciseAdapter = ExerciseFragmentAdapter(this, exercises)
            view_pager.adapter = exerciseAdapter

            TabLayoutMediator(tab_layout, view_pager) { _, _ ->}.attach()
        })

        view_pager.offscreenPageLimit = 5
        view_pager.setPageTransformer(this)
        view_pager.registerOnPageChangeCallback(viewPagerOnPageChangeCallback)

        timer_slider.onSlideCompleteListener = this
    }

    override fun transformPage(page: View, position: Float) {
        val minScale = 0.75f
        page.apply {
            val pageWidth = width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    alpha = 1 - position

                    // Counteract the default slide transition
                    translationX = pageWidth * -position
                    // Move it behind the left page
                    translationZ = -1f

                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = (minScale + (1 - minScale) * (1 - abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }

    override fun onSlideComplete(view: SlideToActView) {
        // Update last workout unix date
        UserData(this).lastWorkoutUnixDate = System.currentTimeMillis()

        //Update workout data and move to next day
        workout.currentWorkoutIndex += 1
        workoutViewModel.update(workout)

        // Show splash screen
        val intent = Intent(this, WorkoutCompleteActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onNextExerciseButtonClicked(view: View) {
        view_pager.setCurrentItem(view_pager.currentItem + 1, true)
    }

    private var atEndOfSlide: Boolean = false
    private var viewPagerOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            // If we reach the end of the viewpager we will show the slide to finish workout
            if (position == exercises.size - 1) {
                showCompleteSlider()
                atEndOfSlide = true
            }
            else if (atEndOfSlide) {
                showNextExerciseButton()
                atEndOfSlide = false
            }
        }
    }

    private fun showCompleteSlider() {
        val fallDownAnim = AnimationUtils.loadAnimation(this@WorkoutStartActivity, R.anim.button_fall_down)
        val fallUpAnim = AnimationUtils.loadAnimation(this@WorkoutStartActivity, R.anim.button_fall_up)
        fallDownAnim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                next_exercise_button.visibility = View.GONE
                timer_slider.visibility = View.VISIBLE
                timer_slider.startAnimation(fallUpAnim)
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })
        next_exercise_button.startAnimation(fallDownAnim)
    }

    private fun showNextExerciseButton() {
        val fallDownAnim = AnimationUtils.loadAnimation(this@WorkoutStartActivity, R.anim.button_fall_down)
        val fallUpAnim = AnimationUtils.loadAnimation(this@WorkoutStartActivity, R.anim.button_fall_up)
        fallDownAnim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                timer_slider.visibility = View.GONE
                next_exercise_button.visibility = View.VISIBLE
                next_exercise_button.startAnimation(fallUpAnim)
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })
        timer_slider.startAnimation(fallDownAnim)
    }
}
