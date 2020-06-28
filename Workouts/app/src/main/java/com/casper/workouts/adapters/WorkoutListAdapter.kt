package com.casper.workouts.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.casper.workouts.R
import com.casper.workouts.activities.EditWorkoutActivity
import com.casper.workouts.activities.WorkoutHomeActivity
import com.casper.workouts.room.models.Workout
import com.casper.workouts.custom.inflate
import com.casper.workouts.utils.FileUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_workout_list_item.view.*
import java.util.*

class WorkoutListAdapter() : RecyclerView.Adapter<WorkoutListAdapter.WorkoutHolder>() {
    var itemPositionsChanged = false

    private var items = emptyList<Workout>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutHolder {
        val inflatedView = parent.inflate(R.layout.adapter_workout_list_item, false)
        return WorkoutHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: WorkoutHolder, position: Int) {
        val workoutItem = items[position]
        holder.bindWorkout(workoutItem)
    }

    fun setItems(workouts: List<Workout>) {
        this.items = workouts
        notifyDataSetChanged()
    }

    fun getItems(): List<Workout> = this.items

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

    class WorkoutHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var item: Workout? = null

        init {

        }

        companion object {
            const val EXTRA_WORKOUT_UID = "EXTRA_UID"
            const val EXTRA_WORKOUT_NAME = "EXTRA_NAME"
            const val EXTRA_WORKOUT_WORKOUT = "EXTRA_WORKOUT"
        }

        fun bindWorkout(workout: Workout) {
            item = workout

            // Get workout image
            workout.imageName?.let { imageName ->
                if (imageName.isNotEmpty()) {
                    FileUtils().getWorkoutImage(view.context, imageName)?.let { file ->
                        Picasso.get().load(file).into(view.image)
                    }
                }
            }

            view.title.text = workout.name
            setDescription(workout.description)

            // Front view click listener
            view.main_item.setOnClickListener {
                item?.let { workout ->
                    val context = itemView.context
                    val intent = Intent(context, WorkoutHomeActivity::class.java)
                    intent.putExtra(EXTRA_WORKOUT_UID, workout.workoutId)
                    intent.putExtra(EXTRA_WORKOUT_NAME, workout.name)
                    context.startActivity(intent)
                }
            }

            // Back view click listeners
            view.edit.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditWorkoutActivity::class.java)
                intent.putExtra(EXTRA_WORKOUT_WORKOUT, item)
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