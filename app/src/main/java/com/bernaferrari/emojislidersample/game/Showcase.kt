package com.bernaferrari.emojislidersample.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bernaferrari.emojislidersample.R
import com.bernaferrari.emojislidersample.game.util.bridge.BridgeHandler
import kotlinx.android.synthetic.main.frag_main_game.*


class Showcase : Fragment() {

    private val TAG = "Showcase"

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.frag_main_game, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button3.setOnClickListener {
            web_view.loadUrl("http://10.0.2.2:7456/")
        }

        web_view.setDefaultHandler { data, function ->
            Log.d(TAG, "setDefaultHandler: $data")
        }

        web_view.registerHandler("openReactNativeStore") { data, function ->
            Log.d(TAG, "setDefaultHandler: $data")
        }
    }


}
