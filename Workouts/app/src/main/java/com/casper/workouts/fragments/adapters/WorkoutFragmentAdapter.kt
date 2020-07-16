package com.casper.workouts.fragments.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.casper.workouts.fragments.ExerciseFragment
import com.casper.workouts.fragments.WorkoutFragment
import com.casper.workouts.room.models.Exercise
import com.casper.workouts.utils.WorkoutUtil

class WorkoutFragmentAdapter(activity: AppCompatActivity, private val workoutDayHolders: List<WorkoutUtil.WorkoutDayHolder>): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = workoutDayHolders.size
    override fun createFragment(position: Int): Fragment = WorkoutFragment.newInstance(workoutDayHolders[position])
}