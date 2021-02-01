package com.bernaferrari.emojislidersample.bridge;

import android.webkit.ValueCallback;

public interface BridgeHandler {
    void handler(String data, ValueCallback<String> function);
}
