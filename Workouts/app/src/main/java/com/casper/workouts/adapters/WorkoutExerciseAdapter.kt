package com.casper.workouts.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.casper.workouts.R
import com.casper.workouts.activities.EditExerciseActivity
import com.casper.workouts.callbacks.DeleteItemCallback
import com.casper.workouts.callbacks.OptionDialogCallback
import com.casper.workouts.callbacks.SearchExerciseSelectedCallback
import com.casper.workouts.custom.inflate
import com.casper.workouts.dialogs.OptionDialog
import com.casper.workouts.room.models.Exercise
import kotlinx.android.synthetic.main.adapter_workout_exercise_item.view.*
import kotlinx.android.synthetic.main.adapter_workout_exercise_item.view.description
import kotlinx.android.synthetic.main.adapter_workout_exercise_item.view.title
import java.util.*
import kotlin.collections.ArrayList

class WorkoutExerciseAdapter(val deleteItemCallback: DeleteItemCallback) : RecyclerView.Adapter<WorkoutExerciseAdapter.ExerciseHolder>() {

    var itemPositionsChanged = false

    private var items = emptyList<Exercise>()

    override fun getItemCount() = items.size

    fun doesContainExerciseId(id: Long): Boolean = items.any { it.exerciseId == id }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
        val inflatedView = parent.inflate(R.layout.adapter_workout_exercise_item, false)
        return ExerciseHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
        val exerciseItem = items[position]
        holder.bindExercise(exerciseItem)
    }

    fun setItems(exercises: List<Exercise>) {
        this.items = exercises
        notifyDataSetChanged()
    }

    fun getItems(): List<Exercise> = this.items

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

    inner class ExerciseHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var item: Exercise? = null

        init {

        }

        @SuppressLint("SetTextI18n")
        fun bindExercise(exercise: Exercise) {
            item = exercise

            // Set exercise UI data
            view.title.text = exercise.name
            view.exercise_tag.text = exercise.muscleWorked
            setDescription(exercise.description)
            setWeight(exercise.weight, exercise.weightUnit)
            setRepsSets(exercise.reps, exercise.sets)

            // Back view click listeners
            view.edit.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditExerciseActivity::class.java)
                intent.putExtra(EXTRA_EXERCISE, item)
                context.startActivity(intent)

                view.swipe_layout.close(false)
            }
            view.delete.setOnClickListener {
                val context = view.context
                OptionDialog(context,
                    context.getString(R.string.delete),
                    context.getString(R.string.activity_workout_exercise_delete_workout_dialog_desc, exercise.name),
                    context.getString(R.string.cancel),
                    context.getString(R.string.yes),
                    object: OptionDialogCallback {
                        override fun optionOneClicked() {
                            // No - do nothing
                        }

                        override fun optionTwoClicked() {
                            deleteItemCallback.onDeleted(exercise)
                        }
                    }).show()

                view.swipe_layout.close(true)
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

        @SuppressLint("SetTextI18n")
        private fun setWeight(weight: Double?, unit: String?) {
            weight?.let {
                view.exercise_weight.text = "$weight $unit"
                view.exercise_weight.visibility = View.VISIBLE
                return
            }

            view.exercise_weight.visibility = View.GONE
        }

        @SuppressLint("SetTextI18n")
        private fun setRepsSets(reps: Int?, sets: Int?) {
            val repsText = if (reps != null) "R: $reps" else ""
            val setsText = if (sets != null) "S: $sets" else ""
            view.exercise_reps_sets.visibility = View.VISIBLE

            if (repsText.isNotEmpty() && setsText.isNotEmpty()) {
                view.exercise_reps_sets.text = "$repsText / $setsText"
            }
            else if (repsText.isNotEmpty()) {
                view.exercise_reps_sets.text = repsText
            }
            else if (setsText.isNotEmpty()) {
                view.exercise_reps_sets.text = setsText
            }
            else {
                view.exercise_reps_sets.visibility = View.GONE
            }
        }
    }

    companion object {
        const val EXTRA_EXERCISE = "EXTRA_EXERCISE"
    }
}