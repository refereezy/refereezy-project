package com.example.refereezyapp.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.refereezyapp.R
import com.example.refereezyapp.data.models.Team

class ScoreFragment (
    private val localScore: Int,
    private val visitorScore: Int,
    private val localTeam: Team,
    private val visitorTeam: Team
) : Fragment() {

    private lateinit var scoreView: TextView
    private lateinit var localTeamLogo: ImageView
    private lateinit var visitorTeamLogo: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mas cosas
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // cargar datos

        scoreView = view.findViewById(R.id.socreboardtxt)
        localTeamLogo = view.findViewById(R.id.localTeamLogo)
        visitorTeamLogo = view.findViewById(R.id.visitorTeamLogo)

        scoreView.text = "$localScore - $visitorScore"

        // load with glide the images
        loadTeamLogo(localTeam, localTeamLogo)
        loadTeamLogo(visitorTeam, visitorTeamLogo)

    }

    fun loadTeamLogo(team: Team, imageView: ImageView) {
        Glide.with(this)
            .load(team.logo_url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
            .onLoadFailed(ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null))
    }
}