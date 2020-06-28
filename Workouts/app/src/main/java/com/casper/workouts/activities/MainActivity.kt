package com.casper.workouts.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.casper.workouts.R
import com.casper.workouts.adapters.WorkoutListAdapter
import com.casper.workouts.custom.ListItemDecoration
import com.casper.workouts.data.UserData
import com.casper.workouts.room.models.Workout
import com.casper.workouts.room.viewmodels.WorkoutViewModel
import com.casper.workouts.utils.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: WorkoutListAdapter
    private lateinit var workoutViewModel: WorkoutViewModel

    override fun onResume() {
        super.onResume()

        // Set last workout date text
        setLastWorkoutDate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutManager = LinearLayoutManager(this)
        workout_list.layoutManager = linearLayoutManager
        adapter = WorkoutListAdapter()
        workout_list.adapter = adapter
        workout_list.addItemDecoration(ListItemDecoration(resources.getDimension(R.dimen.list_item_spacing).toInt()))

        workoutViewModel = ViewModelProvider(this).get(WorkoutViewModel::class.java)
        workoutViewModel.allWorkouts.observe(this, Observer { workouts ->
            workouts?.let {
                adapter.setItems(workouts)

                // If we don't have any workouts, prompt to create one right away
                if (workouts.isEmpty()) {
                    startCreateWorkoutActivity()
                }
            }
        })

        // Allow for drag to reorder
        val touchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
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
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.8f
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
            }
        })
        touchHelper.attachToRecyclerView(workout_list)
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

        val items = adapter.getItems()
        for ((index, value) in items.withIndex()) {
            value.sortingIndex = index
        }

        workoutViewModel.update(items)
    }

    private fun setLastWorkoutDate() {
        val lastWorkoutUnixDate = UserData(this).lastWorkoutUnixDate
        if (lastWorkoutUnixDate == 0L) {
            page_subtitle.text = getString(R.string.activity_main_subtitle, getString(R.string.never))
        }
        else {
            // PrettyTime is for making a date format like "5 minutes ago" or "Just a moment ago" etc...
            val prettyTimeStr = PrettyTime().format(Date(lastWorkoutUnixDate))
            page_subtitle.text = getString(R.string.activity_main_subtitle, prettyTimeStr)
        }
    }

    fun onCreateWorkoutButtonClicked(view: View) {
        startCreateWorkoutActivity()
    }

    private fun startCreateWorkoutActivity() {
        val intent = Intent(this, CreateWorkoutActivity::class.java)
        startActivityForResult(intent, RESULT_CREATE_WORKOUT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CREATE_WORKOUT && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                // Get all data for workout
                val workoutName = intent.getStringExtra(CreateWorkoutActivity.EXTRA_REPLY_NAME) as String
                val workoutDesc = intent.getStringExtra(CreateWorkoutActivity.EXTRA_REPLY_DESC)
                val workoutImagePath = intent.getStringExtra(CreateWorkoutActivity.EXTRA_REPLY_IMAGE)

                // If we have a photo, copy this to our application folder
                var workoutImageName = ""
                workoutImagePath?.let { path ->
                    workoutImageName = FileUtils().saveWorkoutImage(this, path)
                }

                // Get next sorting index for list
                var sortingIndex = adapter.itemCount

                // Insert workout into db
                val workout = Workout(0, sortingIndex, workoutName, workoutDesc, workoutImageName, 0, 0, 0)
                workoutViewModel.insert(workout)
            }
        }
    }

    companion object {
        private const val RESULT_CREATE_WORKOUT = 0
    }
}