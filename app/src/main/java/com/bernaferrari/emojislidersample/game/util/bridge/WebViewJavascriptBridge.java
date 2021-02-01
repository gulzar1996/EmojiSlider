package com.bernaferrari.emojislidersample.game.util.bridge;


import android.webkit.ValueCallback;

interface WebViewJavascriptBridge {
    void send(String data);
    void send(String data, ValueCallback<String> responseCallback);
}
