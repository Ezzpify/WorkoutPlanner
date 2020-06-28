package com.casper.workouts.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.casper.workouts.R
import com.casper.workouts.adapters.WorkoutWeekAdapter
import com.casper.workouts.custom.ListItemDecoration
import com.casper.workouts.dialogs.ErrorDialog
import com.casper.workouts.room.models.Week
import com.casper.workouts.room.viewmodels.WeekViewModel
import kotlinx.android.synthetic.main.activity_workout_week_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WorkoutWeekListActivity : AppCompatActivity() {
    private var workoutId: Long = -1L;
    private var workoutName: String? = null

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: WorkoutWeekAdapter
    private lateinit var weekViewModel: WeekViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_week_list)

        // Get all intent data for workout
        workoutName = intent.getStringExtra(WorkoutHomeActivity.EXTRA_WORKOUT_NAME)
        workoutId = intent.getLongExtra(WorkoutHomeActivity.EXTRA_WORKOUT_UID, -1L)
        if (workoutId == -1L) {
            ErrorDialog(this, getString(R.string.error), getString(R.string.general_error_oops)).show()
        }

        // Set view data
        workout_title.text = workoutName

        // Set up recyclerview for displaying weeks
        linearLayoutManager = LinearLayoutManager(this)
        week_list.layoutManager = linearLayoutManager
        adapter = WorkoutWeekAdapter()
        week_list.adapter = adapter
        week_list.addItemDecoration(ListItemDecoration(resources.getDimension(R.dimen.list_item_spacing).toInt()))

        // ViewModel for getting week data
        weekViewModel = ViewModelProvider(this).get(WeekViewModel::class.java)
        weekViewModel.getWeeks(workoutId).observe(this, Observer { weeks ->
            adapter.setItems(weeks)

            // If we don't have any weeks, prompt to create one right away
            if (weeks.isEmpty()) {
                startCreateWeekActivity()
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
        touchHelper.attachToRecyclerView(week_list)
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

        weekViewModel.update(items)
    }

    private fun startCreateWeekActivity() {
        val intent = Intent(this, CreateWeekActivity::class.java)
        startActivityForResult(intent, RESULT_CREATE_WEEK)
    }

    fun onCreateWorkoutButtonClicked(view: View) {
        startCreateWeekActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CREATE_WEEK) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let { intent ->
                    // Get all data for week
                    val weekName = intent.getStringExtra(CreateWeekActivity.EXTRA_REPLY_NAME) as String
                    val weekDesc = intent.getStringExtra(CreateWeekActivity.EXTRA_REPLY_DESC)

                    // Get next sorting index for list
                    var sortingIndex = adapter.itemCount

                    // Create new week object
                    val week = Week(0, workoutId, sortingIndex, weekName, weekDesc)
                    weekViewModel.insert(week)
                }
            }
            else {
                if (adapter.itemCount == 0) {
                    finish()
                }
            }
        }
    }

    companion object {
        private const val RESULT_CREATE_WEEK = 0
    }
}