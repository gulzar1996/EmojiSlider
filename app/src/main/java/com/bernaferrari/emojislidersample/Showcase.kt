package com.bernaferrari.emojislidersample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.frag_main.*


class Showcase : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.frag_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sliderv4.sliderParticleSystem = slider_particle_system
        sliderv4.progress = 0f

        bt_increase.setOnClickListener {
            val currentProgress = sliderv4.progress
            sliderv4.setProgressWithAnimation((currentProgress + random()).toFloat())
        }
        bt_decrease.setOnClickListener {
            val currentProgress = sliderv4.progress
            sliderv4.setProgressWithAnimation((currentProgress - random()).toFloat())
        }

    }

    fun random(): Double = (Math.random() * (0.5f)) as Double

}
