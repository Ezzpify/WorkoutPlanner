package com.casper.workouts.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.casper.workouts.R
import kotlinx.android.synthetic.main.activity_create_day.*

class CreateDayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_day)
    }

    fun onSaveDayButtonClicked(view: View) {
        var error = false
        val errorText = getString(R.string.required)

        val dayName = day_name.text.toString().trim()
        val dayDescription = day_description.text.toString().trim()

        if (dayName.isEmpty()) {
            day_name.error = errorText
            error = true
        }

        if (error) {
            return
        }

        val replyIntent = Intent()
        replyIntent.putExtra(EXTRA_REPLY_NAME, dayName)
        replyIntent.putExtra(EXTRA_REPLY_DESC, dayDescription)
        setResult(Activity.RESULT_OK, replyIntent)
        finish()
    }

    companion object {
        const val EXTRA_REPLY_NAME = "REPLY_NAME"
        const val EXTRA_REPLY_DESC = "REPLY_DESC"
    }
}