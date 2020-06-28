package com.casper.workouts.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.casper.workouts.R
import com.casper.workouts.adapters.WorkoutExerciseAdapter
import com.casper.workouts.callbacks.SearchExerciseSelectedCallback
import com.casper.workouts.custom.ListItemDecoration
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.room.viewmodels.ExerciseViewModel
import kotlinx.android.synthetic.main.activity_exercise_search.*
import kotlinx.android.synthetic.main.activity_workout_exercises_list.exercises_list

class ExercisesSearchActivity : AppCompatActivity(), SearchExerciseSelectedCallback {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: WorkoutExerciseAdapter
    private lateinit var exerciseViewModel: ExerciseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_search)

        // Set up recyclerview for displaying days
        linearLayoutManager = LinearLayoutManager(this)
        exercises_list.layoutManager = linearLayoutManager
        adapter = WorkoutExerciseAdapter(this)
        exercises_list.adapter = adapter
        exercises_list.addItemDecoration(ListItemDecoration(resources.getDimension(R.dimen.list_item_spacing).toInt()))

        // Hide no content view
        no_content.visibility = View.GONE

        // Get all exercises, ascending by name
        exerciseViewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)
        exerciseViewModel.allExercises.observe(this, Observer { allExercises ->
            allExercises?.let {
                adapter.setItems(allExercises)

                if (allExercises.isEmpty()) {
                    // Show no content view
                    no_content.visibility = View.VISIBLE
                }
            }
        })

        // Init search view
        search_view.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    fun onGoBackButtonClicked(view: View) {
        finish()
    }

    override fun onExerciseSelected(exercise: Exercise) {
        val replyIntent = Intent()
        replyIntent.putExtra(EXTRA_REPLY_EXERCISE, exercise)
        setResult(Activity.RESULT_OK, replyIntent)
        finish()
    }

    companion object {
        const val EXTRA_REPLY_EXERCISE = "REPLY_EXERCISE"
    }
}