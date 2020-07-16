package com.casper.workouts.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.casper.workouts.R
import com.casper.workouts.dialogs.ErrorDialog
import com.casper.workouts.fragments.adapters.ExerciseFragmentAdapter
import com.casper.workouts.fragments.adapters.WorkoutFragmentAdapter
import com.casper.workouts.room.models.junctions.FullWorkout
import com.casper.workouts.room.viewmodels.WorkoutViewModel
import com.casper.workouts.utils.WorkoutUtil
import com.casper.workouts.utils.WorkoutUtils
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_workout_home.*
import kotlinx.android.synthetic.main.activity_workout_home.view_pager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class WorkoutHomeActivity : AppCompatActivity(), ViewPager2.PageTransformer {
    private var workoutId: Long = -1L
    private var workoutName: String? = null
    private lateinit var workoutViewModel: WorkoutViewModel

    // Store current full workout
    private lateinit var fullWorkout: FullWorkout
    private lateinit var workoutDays: List<WorkoutUtil.WorkoutDayHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_home)

        // Set workout information from intent bundle
        workoutName = intent.getStringExtra(EXTRA_WORKOUT_NAME)
        workoutId = intent.getLongExtra(EXTRA_WORKOUT_UID, -1)
        if (workoutId == -1L) {
            ErrorDialog(this, getString(R.string.error), getString(R.string.general_error_oops)).show()
        }

        // Hide view initially
        home_workout.visibility = View.GONE
        home_no_schedule.visibility = View.GONE

        // Set view data
        workout_title.text = workoutName

        // Init workout view model to get full workout data
        workoutViewModel = ViewModelProvider(this).get(WorkoutViewModel::class.java)
        workoutViewModel.getFullWorkout(workoutId).observe(this, Observer { fullWorkout ->
            // Don't want to fire this if activity is not active
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                return@Observer
            }

            // Hide main views
            home_workout.visibility = View.GONE
            home_no_schedule.visibility = View.GONE

            // Check so workout have proper data before attempting to set up workout
            if (fullWorkout.hasNecessaryData()) {
                home_workout.visibility = View.VISIBLE

                // Get the list of workout days
                this.fullWorkout = fullWorkout
                workoutDays = WorkoutUtil(fullWorkout).getList()

                // Set up the adapter and view pager with all the days
                val workoutAdapter = WorkoutFragmentAdapter(this, workoutDays)
                view_pager.adapter = workoutAdapter

                // Set default index for viewpager
                var index = fullWorkout.workout.currentWorkoutIndex
                if (index > workoutDays.size - 1) index = 0

                // Allow for fragments to be set up before setting default item
                GlobalScope.launch(Dispatchers.Main) {
                    delay(350)
                    view_pager.setCurrentItem(index, true)
                }

                TabLayoutMediator(tab_layout, view_pager) { _, _ ->}.attach()
            }
            else {
                home_no_schedule.visibility = View.VISIBLE
            }
        })

        view_pager.offscreenPageLimit = 5
        view_pager.setPageTransformer(this)
        view_pager.registerOnPageChangeCallback(viewPagerOnPageChangeCallback)

        val dropAnimation = AnimationUtils.loadAnimation(this, R.anim.item_fall_up)
        action_parent.startAnimation(dropAnimation)
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

    private var viewPagerOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            // If we reach the end of the viewpager we will show the slide to finish workout
            /*if (position == exercises.size - 1) {
                showCompleteSlider()
                atEndOfSlide = true
            }
            else if (atEndOfSlide) {
                showNextExerciseButton()
                atEndOfSlide = false
            }*/
            //fullWorkout.workout.currentWorkoutIndex = position
        }

        private var onLastPage = false
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            // Check if we're on the last page
            onLastPage = if (position == workoutDays.size - 1) {
                // If we tried scrolling further, go back to page one
                if (onLastPage) {
                    view_pager.setCurrentItem(0, true)
                }
                true
            }
            else {
                false
            }
        }
    }

    fun onStartWorkoutButtonClicked(view: View) {
        val intent = Intent(this, WorkoutStartActivity::class.java)
        intent.putExtra(EXTRA_WORKOUT_WORKOUT, fullWorkout.workout)
        intent.putExtra(EXTRA_WORKOUT_DAY, workoutDays[view_pager.currentItem].workoutDay.day)
        startActivity(intent)
    }

    fun onSkipWorkoutButtonClicked(view: View) {
        val nextIndex = view_pager.currentItem + 1

        // If we're at last page we'll start over from the beginning
        if (nextIndex > workoutDays.size - 1) {
            view_pager.setCurrentItem(0, true)
        }
        else {
            view_pager.setCurrentItem(nextIndex, true)
        }
    }

    fun onEditWorkoutButtonClicked(view: View) {
        goToEditWorkout()
    }

    private fun goToEditWorkout() {
        val intent = Intent(this, WorkoutWeekListActivity::class.java)
        intent.putExtra(EXTRA_WORKOUT_UID, workoutId)
        intent.putExtra(EXTRA_WORKOUT_NAME, workoutName)
        startActivity(intent)
    }

    override fun onPause() {
        fullWorkout.workout.currentWorkoutIndex = view_pager.currentItem
        workoutViewModel.update(fullWorkout.workout)
        super.onPause()
    }

    companion object {
        const val EXTRA_WORKOUT_UID = "EXTRA_UID"
        const val EXTRA_WORKOUT_NAME = "EXTRA_NAME"
        const val EXTRA_WORKOUT_WORKOUT = "EXTRA_WORKOUT"
        const val EXTRA_WORKOUT_DAY = "EXTRA_DAY"
    }
}