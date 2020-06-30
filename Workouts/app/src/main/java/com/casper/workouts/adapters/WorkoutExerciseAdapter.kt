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
import com.casper.workouts.utils.FileUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_workout_exercise_item.view.*
import kotlinx.android.synthetic.main.adapter_workout_exercise_item.view.description
import kotlinx.android.synthetic.main.adapter_workout_exercise_item.view.image
import kotlinx.android.synthetic.main.adapter_workout_exercise_item.view.title
import java.util.*
import kotlin.collections.ArrayList

class WorkoutExerciseAdapter() : RecyclerView.Adapter<WorkoutExerciseAdapter.ExerciseHolder>(),
    Filterable {
    private var selectedItemCallback: SearchExerciseSelectedCallback? = null
    private var deleteItemCallback: DeleteItemCallback? = null

    var itemPositionsChanged = false

    private var items = emptyList<Exercise>()
    private var filteredItems = emptyList<Exercise>()

    private val filter = ExerciseFilter()

    constructor(selectedCallback: SearchExerciseSelectedCallback) : this() {
        selectedItemCallback = selectedCallback
    }

    constructor(callback: DeleteItemCallback) : this() {
        deleteItemCallback = callback
    }

    override fun getFilter(): Filter {
        return filter
    }

    override fun getItemCount() = filteredItems.size

    fun doesContainExerciseId(id: Long): Boolean = filteredItems.any { it.exerciseId == id }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
        val inflatedView = parent.inflate(R.layout.adapter_workout_exercise_item, false)
        return ExerciseHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
        val exerciseItem = filteredItems[position]
        holder.bindExercise(exerciseItem)
    }

    fun setItems(exercises: List<Exercise>) {
        this.items = exercises
        this.filteredItems = exercises
        notifyDataSetChanged()
    }

    fun getItems(): List<Exercise> = this.filteredItems

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(filteredItems, i, i + 1)
            }
        }
        else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(filteredItems, i, i - 1)
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

            // Get exercise image
            exercise.imageName?.let { imageName ->
                if (imageName.isNotEmpty()) {
                    FileUtils().getWorkoutImage(view.context, imageName)?.let { file ->
                        Picasso.get().load(file).into(view.image)
                    }
                }
            }

            // Set exercise UI data
            view.title.text = exercise.name
            view.exercise_tag.text = exercise.tag
            setDescription(exercise.description)
            setWeight(exercise.weight, exercise.weightUnit)
            setRepsSets(exercise.reps, exercise.sets)

            // Front view click listener
            view.main_item.setOnClickListener {
                item?.let {
                    selectedItemCallback?.onExerciseSelected(it)
                }
            }

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
                            deleteItemCallback?.onDeleted(exercise)
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
                return
            }

            view.exercise_weight.visibility = View.GONE
        }

        @SuppressLint("SetTextI18n")
        private fun setRepsSets(reps: Int?, sets: Int?) {
            val repsText = if (reps != null) "R: $reps" else ""
            val setsText = if (sets != null) "S: $sets" else ""

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

    inner class ExerciseFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val resultList = ArrayList<Exercise>()

            constraint?.let { c ->
                val queryText = c.toString().toLowerCase(Locale.getDefault())

                if (queryText.isNotEmpty()) {
                    // Filter through items to find matching results
                    // We only filter by exercise name, though
                    for (exercise in items) {
                        if (exercise.name.toLowerCase(Locale.getDefault()).contains(queryText, true)
                            || exercise.tag.toLowerCase(Locale.getDefault()).contains(queryText, true)) {
                            resultList.add(exercise)
                        }
                    }

                    results.values = resultList
                }
                else {
                    // Empty query, return original items
                    results.values = items
                }
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.values.let { values ->
                // I'm a fucking wizard so I already know what type the 'Any' will be
                @Suppress("UNCHECKED_CAST")
                filteredItems = values as ArrayList<Exercise>
                notifyDataSetChanged()
            }
        }
    }

    companion object {
        const val EXTRA_EXERCISE = "EXTRA_EXERCISE"
    }
}