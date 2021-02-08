package com.bernaferrari.emojislidersample.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bernaferrari.emojislidersample.R
import kotlinx.android.synthetic.main.frag_main_game.*


class Showcase : Fragment() {

    private val TAG = "Showcase"
    private val ON_BACK_PRESSED = "onBackPressed"


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.frag_main_game, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handleBackPress()
        LifeCycleWebView(web_view, lifecycle)

        web_view.registerHandler("getInitialData") { data, function ->
            Log.d(TAG, "getInitialData called: $data")
            function.onReceiveValue("\"abhishek\"")
        }

        web_view.registerHandler("onBackPressRegister") { data, function ->
        }

        web_view.setDefaultHandler { data, function ->
            Log.d(TAG, "setDefaultHandler: $data")
        }

        web_view.registerHandler("openReactNativeStore") { data, function ->
            Log.d(TAG, "setDefaultHandler: $data")
        }

        web_view.registerHandler("showToast") { data, function ->
            Toast.makeText(activity, data, Toast.LENGTH_SHORT).show()
        }

        web_view.registerHandler("onExit") { data, function ->
            activity?.finish()
        }

        button3.setOnClickListener {
            web_view.loadUrl(editTextTextPersonName.text.toString())
        }


    }

    private fun handleBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                fun activityBackPress() {
                    isEnabled = false
                    activity?.onBackPressed()
                }

                web_view.callHandler(ON_BACK_PRESSED, ON_BACK_PRESSED) {
                    if (it?.toBoolean() == false)
                        activityBackPress()
                }
            }
        })
    }


}
