package com.example.refereezyapp.utils

import android.app.Activity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.refereezyapp.R

class PopUp {
    enum class Type { OK, INFO, ERROR }

    companion object {
        fun show(activity: Activity, message: String, type: Type) {
            val view = LayoutInflater.from(activity).inflate(R.layout.layout_popup, null)
            val icon = view.findViewById<ImageView>(R.id.popup_icon)
            val text = view.findViewById<TextView>(R.id.popup_text)

            val displayMessage = if (message.length > 20)
                message.substring(0, 20) + "..." else message

            when (type) {
                Type.OK -> {
                    icon.setImageResource(R.drawable.ic_check)
                }
                Type.INFO -> {
                    icon.setImageResource(R.drawable.ic_info)
                }
                Type.ERROR -> {
                    icon.setImageResource(R.drawable.ic_error)
                }
            }
            text.text = displayMessage

            val toast = Toast(activity)
            toast.duration = Toast.LENGTH_LONG
            toast.view = view
            toast.show()
        }
    }
}
