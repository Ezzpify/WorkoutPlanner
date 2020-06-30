package com.casper.workouts.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.casper.workouts.R
import com.casper.workouts.activities.ExercisesSearchActivity.Companion.EXTRA_REPLY_EXERCISE
import com.casper.workouts.adapters.WorkoutDayAdapter
import com.casper.workouts.adapters.WorkoutExerciseAdapter
import com.casper.workouts.callbacks.DeleteItemCallback
import com.casper.workouts.callbacks.OptionDialogCallback
import com.casper.workouts.custom.ListItemDecoration
import com.casper.workouts.dialogs.ErrorDialog
import com.casper.workouts.dialogs.OptionDialog
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.models.dayjunctions.DayExerciseCrossRef
import com.casper.workouts.room.models.dayjunctions.DayWithExercises
import com.casper.workouts.room.viewmodels.ExerciseViewModel
import com.casper.workouts.utils.FileUtils
import kotlinx.android.synthetic.main.activity_workout_exercises_list.*
import kotlinx.coroutines.launch

class WorkoutExerciseListActivity : AppCompatActivity(), DeleteItemCallback {
    private var dayId: Long = -1L;
    private var dayName: String? = null

    private lateinit var dayWithExercises: DayWithExercises

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: WorkoutExerciseAdapter
    private lateinit var exerciseViewModel: ExerciseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_exercises_list)

        // Get all intent data for week
        dayName = intent.getStringExtra(WorkoutDayAdapter.EXTRA_DAY_NAME)
        dayId = intent.getLongExtra(WorkoutDayAdapter.EXTRA_DAY_UID, -1L)
        if (dayId == -1L) {
            ErrorDialog(this, getString(R.string.error), getString(R.string.general_error_oops)).show()
        }

        // Set view data
        day_title.text = dayName

        // Set up recyclerview for displaying days
        linearLayoutManager = LinearLayoutManager(this)
        exercises_list.layoutManager = linearLayoutManager
        adapter = WorkoutExerciseAdapter(this)
        exercises_list.adapter = adapter
        exercises_list.addItemDecoration(ListItemDecoration(resources.getDimension(R.dimen.list_item_spacing).toInt()))

        // ViewModel for getting exercise data
        exerciseViewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)
        exerciseViewModel.getExercises(dayId).observe(this, Observer { day ->
            this.dayWithExercises = day

            // Get the sorting index from the junction table and add that value to all exercise objects so we can sort them
            for ((index, value) in day.extras.withIndex()) {
                day.exercises[index].sortingIndex = value.sortingIndex
            }

            val sortedList = day.exercises.sortedBy { it.sortingIndex }
            adapter.setItems(sortedList)
        })

        // Allow for drag to reorder
        val touchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(UP or DOWN, 0) {
            override fun onMove( recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
                adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.8f
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
            }
        })
        touchHelper.attachToRecyclerView(exercises_list)
    }

    override fun onPause() {
        updateItemsSortingIndexes()
        super.onPause()
    }

    override fun onDestroy() {
        updateItemsSortingIndexes()
        super.onDestroy()
    }

    private fun updateItemsSortingIndexes() {
        if (!adapter.itemPositionsChanged)
            return

        // Reset value
        adapter.itemPositionsChanged = false

        // Update all junction entries with new sorting indexes
        val items = adapter.getItems()
        for ((index, value) in items.withIndex()) {
            dayWithExercises.extras.first { it.exerciseId == value.exerciseId }.sortingIndex = index
        }

        exerciseViewModel.updateExtras(dayWithExercises.extras)
    }

    fun onCreateExerciseButtonClicked(view: View) {
        OptionDialog(this,
            getString(R.string.activity_workout_exercise_add_dialog_title),
            getString(R.string.activity_workout_exercise_add_dialog_desc),
            getString(R.string.activity_workout_exercise_add_dialog_option_one),
            getString(R.string.activity_workout_exercise_add_dialog_option_two),
            object: OptionDialogCallback {
                override fun optionOneClicked() {
                    // Add existing
                    val intent = Intent(applicationContext, ExercisesSearchActivity::class.java)
                    startActivityForResult(intent, RESULT_ADD_EXISTING_EXERCISE)
                }

                override fun optionTwoClicked() {
                    // Add new
                    val intent = Intent(applicationContext, CreateExerciseActivity::class.java)
                    startActivityForResult(intent, RESULT_CREATE_EXERCISE)
                }
            }).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CREATE_EXERCISE && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                // Get all data for exercise
                val exerciseName = intent.getStringExtra(CreateExerciseActivity.EXTRA_REPLY_NAME) as String
                val exerciseTag = intent.getStringExtra(CreateExerciseActivity.EXTRA_REPLY_TAG) as String
                val exerciseDesc = intent.getStringExtra(CreateExerciseActivity.EXTRA_REPLY_DESC)
                val exerciseUnit = intent.getStringExtra(CreateExerciseActivity.EXTRA_REPLY_UNIT)
                val exerciseImagePath = intent.getStringExtra(CreateExerciseActivity.EXTRA_REPLY_IMAGE)
                val exerciseWeight = intent.getDoubleExtra(CreateExerciseActivity.EXTRA_REPLY_WEIGHT, -1.0)
                val exerciseSets = intent.getIntExtra(CreateExerciseActivity.EXTRA_REPLY_SETS, -1)
                val exerciseReps = intent.getIntExtra(CreateExerciseActivity.EXTRA_REPLY_REPS, -1)

                // If we have a photo, copy this to our application folder
                var exerciseImageName = ""
                exerciseImagePath?.let { path ->
                    exerciseImageName = FileUtils().saveWorkoutImage(this, path)
                }

                // Create new exercise object
                val exercise = Exercise(0,
                    exerciseName,
                    exerciseTag,
                    exerciseDesc,
                    if (exerciseWeight == -1.0) null else exerciseWeight,
                    exerciseUnit,
                    if (exerciseSets == -1) null else exerciseSets,
                    if (exerciseReps == -1) null else exerciseReps,
                    exerciseImageName)

                // Now insert the exercise object and wait for UID to be returned so we can insert relation
                lifecycleScope.launch {
                    // Instead of setting sorting index in Exercise object we have to do it in the Day+Exercise crossref table
                    val sortingIndex = adapter.itemCount

                    val id = exerciseViewModel.insert(exercise)
                    id.observe(this@WorkoutExerciseListActivity, Observer {
                        // We got the ID of the object
                        // Now we will create a new relationship object between day and exercise
                        val relation = DayExerciseCrossRef(dayId, it, sortingIndex)
                        exerciseViewModel.insert(relation)
                    })
                }
            }
        }

        if (requestCode == RESULT_ADD_EXISTING_EXERCISE && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                // Get the exercise selected
                val exercise = intent.getSerializableExtra(EXTRA_REPLY_EXERCISE) as Exercise

                // Check so we don't already have this exercise
                if (adapter.doesContainExerciseId(exercise.exerciseId)) {
                    ErrorDialog(this, getString(R.string.error), getString(R.string.activity_workout_exercise_already_exists_error)).show()
                    return
                }

                // Add relation for day and selected exercise
                val sortingIndex = adapter.itemCount
                val relation = DayExerciseCrossRef(dayId, exercise.exerciseId, sortingIndex)
                exerciseViewModel.insert(relation)
            }
        }
    }

    override fun onDeleted(item: Any) {
        if (item is Exercise) {
            exerciseViewModel.deleteJunction(dayId, item.exerciseId)
        }
    }

    companion object {
        private const val RESULT_CREATE_EXERCISE = 0
        private const val RESULT_ADD_EXISTING_EXERCISE = 1
    }
}