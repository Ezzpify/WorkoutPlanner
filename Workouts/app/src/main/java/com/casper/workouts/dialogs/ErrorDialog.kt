package com.casper.workouts.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.casper.workouts.R
import kotlinx.android.synthetic.main.dialog_error.*

class ErrorDialog(context: Context, private val title: String, private val desc: String) : Dialog(context) {
    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_error)

        dialog_title.text = title
        dialog_description.text = desc

        button_ok.setOnClickListener {
            dismiss()
        }
    }
}