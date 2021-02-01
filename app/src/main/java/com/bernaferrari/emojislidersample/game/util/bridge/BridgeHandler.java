package com.bernaferrari.emojislidersample.game.util.bridge;

import android.webkit.ValueCallback;

public interface BridgeHandler {
    void handler(String data, ValueCallback<String> function);
}
