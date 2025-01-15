package com.driver.sagardriverapp.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import com.driver.sagardriverapp.R


object CommonDialog {

    fun createDialog(
        context: Context,
        title: String,
        subtitle: String,
        positiveButtonText: String,
        negativeButtonText: String,
        positiveButtonCallback: (() -> Unit)? = null,
        negativeButtonCallback: (() -> Unit)? = null

    ): Dialog  {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.common_dialog, null)

        // Create the dialog
        val dialog = Dialog(context,R.style.DialogBackgroundStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)

        // Set the title and subtitle
        val tvDialogTitle = view.findViewById<TextView>(R.id.tvDialogTitle)
        val tvDialogSubTitle = view.findViewById<TextView>(R.id.tvDialogSubTitle)
        tvDialogTitle.text = title
        tvDialogSubTitle.text = subtitle

        // Set the button texts
        val positiveButton = view.findViewById<TextView>(R.id.tvPostiveText)
        val negativeButton = view.findViewById<TextView>(R.id.tvNegetiveText)
        positiveButton.text = positiveButtonText
        negativeButton.text = negativeButtonText

        /* // Create the dialog
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()*/
        positiveButton.setOnClickListener {
            dialog.dismiss()
            positiveButtonCallback?.invoke()
        }
        negativeButton.setOnClickListener {
            dialog.dismiss()
            negativeButtonCallback?.invoke()
        }

        return dialog
    }
    @SuppressLint("MissingInflatedId")
    fun createAuthDialog(
        context: Context,

        positiveButtonCallback: (() -> Unit)? = null,


        ): Dialog  {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.auth_dialog, null)

        // Create the dialog
        val dialog = Dialog(context,R.style.DialogBackgroundStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)

        // Set the title and subtitle


        // Set the button texts
        val positiveButton = view.findViewById<TextView>(R.id.tvButton)


        /* // Create the dialog
         val dialog = AlertDialog.Builder(context)
             .setView(view)
             .create()*/
        positiveButton.setOnClickListener {
            dialog.dismiss()
            positiveButtonCallback?.invoke()
        }



        return dialog
    }
}