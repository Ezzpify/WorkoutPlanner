package com.casper.workouts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.casper.workouts.R
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.utils.WorkoutUtil
import kotlinx.android.synthetic.main.fragment_workout.*

class WorkoutFragment: Fragment() {
    companion object {
        private const val ARG_WORKOUT_DAY = "ARG_WORKOUT_DAY"

        fun newInstance(workoutDayHolder: WorkoutUtil.WorkoutDayHolder) = WorkoutFragment().apply {
            arguments = bundleOf(
                ARG_WORKOUT_DAY to workoutDayHolder)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? = inflater.inflate(R.layout.fragment_workout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val dayHolder = it.getSerializable(ARG_WORKOUT_DAY) as WorkoutUtil.WorkoutDayHolder

            workout_week_name.text = dayHolder.week.name
            workout_day_name.text = dayHolder.workoutDay.day.name
            workout_exercise_count.text = getString(R.string.fragment_workout_exercise_count, dayHolder.workoutDay.exercises.size)
        }
    }
}