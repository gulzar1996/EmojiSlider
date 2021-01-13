package com.bernaferrari.emojislidersample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.frag_main.*
import java.util.*


class Showcase : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.frag_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //sliderv4.sliderParticleSystem = slider_particle_system
        sliderv4.setProgress(0f)

        bt_increase.setOnClickListener {

            val r = Math.max(Math.min(((random(0.0, 0.9)).toFloat()), 1f), 0f)
            sliderv4.setProgress(r, true)
            textView3.append("Random : $r\n")
        }


    }

    fun random(min: Double, max: Double): Double {
        val r = Random()
        return (r.nextInt(((max - min) * 10 + 1).toInt()) + min * 10) / 10.0
    }

}
