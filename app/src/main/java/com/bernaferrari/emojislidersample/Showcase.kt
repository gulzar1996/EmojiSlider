package com.bernaferrari.emojislidersample

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.frag_main.*


class Showcase : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.frag_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sliderv4.sliderParticleSystem = slider_particle_system

        bt_increase.setOnClickListener { sliderv4.progress = sliderv4.progress + (0.1).toFloat() }
        bt_decrease.setOnClickListener { sliderv4.progress = sliderv4.progress - (0.1).toFloat() }

    }
}
