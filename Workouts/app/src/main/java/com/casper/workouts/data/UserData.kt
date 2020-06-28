package com.casper.workouts.data

import android.content.Context
import android.content.SharedPreferences
import com.casper.workouts.R

class UserData(private val context: Context) {
    private fun getSharedPrefs(): SharedPreferences {
        return context.getSharedPreferences(context.getString(R.string.shared_preference_key), Context.MODE_PRIVATE)
    }

    var lastWorkoutUnixDate: Long
        get() {
            return getSharedPrefs().getLong(context.getString(R.string.sp_last_workout_date_string), 0)
        }
        set(value) {
            val sharedPreferences = getSharedPrefs()
            with (sharedPreferences.edit()) {
                putLong(context.getString(R.string.sp_last_workout_date_string), value)
                commit()
            }
        }

    var lastUsedWeightUnit: String
        get() {
            getSharedPrefs().getString(context.getString(R.string.sp_last_used_weight_unit), "")?.let { value ->
                return value
            }

            return ""
        }
        set(value) {
            val sharedPreferences = getSharedPrefs()
            with (sharedPreferences.edit()) {
                putString(context.getString(R.string.sp_last_used_weight_unit), value)
                commit()
            }
        }
}