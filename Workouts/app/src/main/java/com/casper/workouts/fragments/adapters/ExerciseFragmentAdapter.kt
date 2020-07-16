package com.casper.workouts.fragments.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.casper.workouts.fragments.ExerciseFragment
import com.casper.workouts.room.models.Exercise

class ExerciseFragmentAdapter(activity: AppCompatActivity, val exercises: List<Exercise>): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = exercises.size
    override fun createFragment(position: Int): Fragment = ExerciseFragment.newInstance(exercises[position])
}