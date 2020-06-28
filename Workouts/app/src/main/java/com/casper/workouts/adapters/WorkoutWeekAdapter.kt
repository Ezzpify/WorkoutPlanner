package com.casper.workouts.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.casper.workouts.R
import com.casper.workouts.activities.EditWeekActivity
import com.casper.workouts.activities.WorkoutDayListActivity
import com.casper.workouts.custom.inflate
import com.casper.workouts.room.models.Week
import com.casper.workouts.room.viewmodels.WeekViewModel
import kotlinx.android.synthetic.main.adapter_workout_week_item.view.*
import java.util.*

class WorkoutWeekAdapter() : RecyclerView.Adapter<WorkoutWeekAdapter.WeekHolder>() {
    var itemPositionsChanged = false

    private var items = emptyList<Week>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekHolder {
        val inflatedView = parent.inflate(R.layout.adapter_workout_week_item, false)
        return WeekHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: WeekHolder, position: Int) {
        val weekItem = items[position]
        holder.bindWeek(weekItem)
    }

    fun setItems(weeks: List<Week>) {
        this.items = weeks
        notifyDataSetChanged()
    }

    fun getItems(): List<Week> = this.items

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        }
        else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
        itemPositionsChanged = true
        return true
    }

    class WeekHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var item: Week? = null

        init {

        }

        companion object {
            const val EXTRA_WEEK_UID = "EXTRA_UID"
            const val EXTRA_WEEK_NAME = "EXTRA_NAME"
            const val EXTRA_WEEK_WEEK = "EXTRA_WEEK"
        }

        fun bindWeek(week: Week) {
            item = week

            view.title.text = week.name
            setDescription(week.description)

            // Front view click listener
            view.main_item.setOnClickListener {
                item?.let { week ->
                    val context = itemView.context
                    val intent = Intent(context, WorkoutDayListActivity::class.java)
                    intent.putExtra(EXTRA_WEEK_UID, week.weekId)
                    intent.putExtra(EXTRA_WEEK_NAME, week.name)
                    context.startActivity(intent)
                }
            }

            // Back view click listeners
            view.edit.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditWeekActivity::class.java)
                intent.putExtra(EXTRA_WEEK_WEEK, item)
                context.startActivity(intent)
            }
            view.delete.setOnClickListener {

            }
        }

        private fun setDescription(description: String?) {
            description?.let {
                if (it.isNotEmpty()) {
                    view.description.text = it
                    return
                }
            }

            view.description.text = itemView.context.getString(R.string.no_description)
        }
    }
}