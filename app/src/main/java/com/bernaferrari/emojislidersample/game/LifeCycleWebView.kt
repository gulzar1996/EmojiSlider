package com.bernaferrari.emojislidersample.game

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bernaferrari.emojislidersample.game.util.bridge.BridgeWebView

class LifeCycleWebView(private val webView: BridgeWebView, lifecycle: Lifecycle
) : LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    private val LIFE_CYCLE_HOOKS = "lifeCycleHooks"

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        webView.callHandler(LIFE_CYCLE_HOOKS, "ON_START", null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
        webView.callHandler(LIFE_CYCLE_HOOKS, "ON_RESUME", null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        webView.callHandler(LIFE_CYCLE_HOOKS, "ON_STOP", null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        webView.callHandler(LIFE_CYCLE_HOOKS, "ON_PAUSE", null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        webView.callHandler(LIFE_CYCLE_HOOKS, "ON_DESTROY", null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        webView.callHandler(LIFE_CYCLE_HOOKS, "ON_CREATE", null)
    }

}