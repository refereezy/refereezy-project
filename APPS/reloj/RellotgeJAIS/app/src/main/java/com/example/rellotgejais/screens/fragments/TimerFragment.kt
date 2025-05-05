package com.example.rellotgejais.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rellotgejais.MyApp
import com.example.rellotgejais.R
import com.example.rellotgejais.models.TimerViewModel
import com.example.rellotgejais.utils.ConfirmationDialog

class TimerFragment : Fragment() {

    private lateinit var timerViewModel: TimerViewModel
    private lateinit var textViewTimer: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        timerViewModel = (requireActivity().application as MyApp).timerViewModel

        textViewTimer = view.findViewById(R.id.timer)

        timerViewModel.elapsedTime.observe(viewLifecycleOwner) { seconds ->
            val minutes = seconds / 60
            val secs = seconds % 60

            textViewTimer.text = String.format("%02d:%02d", minutes, secs)
        }

        textViewTimer.setOnLongClickListener {
            if (!timerViewModel.isRunning) {
                if (timerViewModel.getElapsedMinutes() < 45) {
                    confirmTimeTruncation("00:00") {
                        timerViewModel.resetTimer()
                    }
                } else {
                    confirmTimeTruncation("45:00") {
                        timerViewModel.setCustomTime(45, 0)
                    }
                }
                true
            }
            true
        }

        return view
    }


    fun confirmTimeTruncation(newTime: String, onConfirm: () -> Unit) {

        ConfirmationDialog.showReportDialog (
            requireContext(),
            "Truncate time to $newTime?",
            onConfirm,
            onCancel = {}
        )

    }


}