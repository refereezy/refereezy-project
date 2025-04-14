package com.example.refereezyapp.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.refereezyapp.MyApp
import com.example.refereezyapp.R
import com.example.refereezyapp.data.handlers.TimerViewModel
import com.example.refereezyapp.utils.ConfirmationDialog

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

        textViewTimer.setOnClickListener {
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
            }
        }

        return view
    }


    fun confirmTimeTruncation(newTime: String, onConfirm: () -> Unit) {

        ConfirmationDialog.showReportDialog (
            requireContext(),
            "Truncate time?",
            "Are you sure you want to truncate the time to $newTime?",
            onConfirm,
            onCancel = {}
        )

    }


}
