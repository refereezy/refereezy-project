package com.example.rellotgejais.screens.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rellotgejais.R
import com.example.rellotgejais.screens.report.ActionListActivity

class ScoreboardFragment : Fragment() {
    private lateinit var marcador: TextView
    private lateinit var escudoIzquierdo: ImageView
    private lateinit var escudoDerecho: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scoreboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        marcador = view.findViewById(R.id.socreboardtxt)
        escudoIzquierdo = view.findViewById(R.id.escudoIzquierdo)
        escudoDerecho = view.findViewById(R.id.escudoDerecho)

        val longClickListener = View.OnLongClickListener {
            startActivity(Intent(activity, ActionListActivity::class.java))
            true
        }

        marcador.setOnLongClickListener(longClickListener)
        escudoIzquierdo.setOnLongClickListener(longClickListener)
        escudoDerecho.setOnLongClickListener(longClickListener)
    }

    fun actualitzaGol() {
        //marcador.text = getString(R.string.s)
    }

    companion object {
        fun newInstance(): ScoreboardFragment = ScoreboardFragment()
    }
}