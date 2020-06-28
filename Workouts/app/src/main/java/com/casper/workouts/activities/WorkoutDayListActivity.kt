package com.casper.workouts.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.casper.workouts.R
import com.casper.workouts.adapters.WorkoutDayAdapter
import com.casper.workouts.adapters.WorkoutWeekAdapter
import com.casper.workouts.custom.ListItemDecoration
import com.casper.workouts.dialogs.ErrorDialog
import com.casper.workouts.room.models.Day
import com.casper.workouts.room.viewmodels.DayViewModel
import kotlinx.android.synthetic.main.activity_workout_day_list.*

class WorkoutDayListActivity : AppCompatActivity() {
    private var weekId: Long = -1L;
    private var weekName: String? = null

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: WorkoutDayAdapter
    private lateinit var dayViewModel: DayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_day_list)

        // Get all intent data for week
        weekName = intent.getStringExtra(WorkoutWeekAdapter.WeekHolder.EXTRA_WEEK_NAME)
        weekId = intent.getLongExtra(WorkoutWeekAdapter.WeekHolder.EXTRA_WEEK_UID, -1L)
        if (weekId == -1L) {
            ErrorDialog(this, getString(R.string.error), getString(R.string.general_error_oops)).show()
        }

        // Set view data
        week_title.text = weekName

        // Set up recyclerview for displaying days
        linearLayoutManager = LinearLayoutManager(this)
        day_list.layoutManager = linearLayoutManager
        adapter = WorkoutDayAdapter()
        day_list.adapter = adapter
        day_list.addItemDecoration(ListItemDecoration(resources.getDimension(R.dimen.list_item_spacing).toInt()))

        // ViewModel for getting week data
        dayViewModel = ViewModelProvider(this).get(DayViewModel::class.java)
        dayViewModel.getDays(weekId).observe(this, Observer { days ->
            adapter.setItems(days)

            // If we don't have any weeks, prompt to create one right away
            if (days.isEmpty()) {
                startCreateDayActivity()
            }
        })

        // Allow for drag to reorder
        val touchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(UP or DOWN, 0) {
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
                if (actionState == ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.8f
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
            }
        })
        touchHelper.attachToRecyclerView(day_list)
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

        dayViewModel.update(items)
    }

    fun onCreateDayButtonClicked(view: View) {
        startCreateDayActivity()
    }

    private fun startCreateDayActivity() {
        val intent = Intent(this, CreateDayActivity::class.java)
        startActivityForResult(intent, RESULT_CREATE_DAY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CREATE_DAY && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                // Get all data for week
                val dayName = intent.getStringExtra(CreateDayActivity.EXTRA_REPLY_NAME) as String
                val dayDescription = intent.getStringExtra(CreateDayActivity.EXTRA_REPLY_DESC) as String

                // Get next sorting index for list
                var index = 0
                if (adapter.itemCount > 0) {
                    index = adapter.getItems().last().sortingIndex + 1
                }

                // Create new day object
                val day = Day(0, weekId, index, dayName, dayDescription)
                dayViewModel.insert(day)
            }
        }
    }

    companion object {
        private const val RESULT_CREATE_DAY = 0
    }
}