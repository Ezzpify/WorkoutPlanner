package com.casper.workouts.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.casper.workouts.R
import com.casper.workouts.activities.EditDayActivity
import com.casper.workouts.activities.WorkoutExerciseListActivity
import com.casper.workouts.custom.inflate
import com.casper.workouts.room.models.Day
import kotlinx.android.synthetic.main.adapter_workout_day_item.view.*
import java.util.*

class WorkoutDayAdapter : RecyclerView.Adapter<WorkoutDayAdapter.DayHolder>() {
    var itemPositionsChanged = false

    private var items = emptyList<Day>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder {
        val inflatedView = parent.inflate(R.layout.adapter_workout_day_item, false)
        return DayHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: DayHolder, position: Int) {
        val dayItem = items[position]
        holder.bindDay(dayItem)
    }

    fun setItems(days: List<Day>) {
        this.items = days
        notifyDataSetChanged()
    }

    fun getItems(): List<Day> = this.items

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

    class DayHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var item: Day? = null

        init {

        }

        companion object {
            const val EXTRA_DAY_UID = "EXTRA_UID"
            const val EXTRA_DAY_NAME = "EXTRA_NAME"
            const val EXTRA_DAY_DAY = "EXTRA_DAY"
        }

        fun bindDay(day: Day) {
            item = day

            view.title.text = day.name
            setDescription(day.description)

            // Front view click listener
            view.main_item.setOnClickListener {
                item?.let { day ->
                    val context = itemView.context
                    val intent = Intent(context, WorkoutExerciseListActivity::class.java)
                    intent.putExtra(EXTRA_DAY_UID, day.dayId)
                    intent.putExtra(EXTRA_DAY_NAME, day.name)
                    context.startActivity(intent)
                }
            }

            // Back view click listeners
            view.edit.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditDayActivity::class.java)
                intent.putExtra(EXTRA_DAY_DAY, day)
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