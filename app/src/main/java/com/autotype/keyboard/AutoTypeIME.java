package com.autotype.keyboard;
import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
public class AutoTypeIME extends InputMethodService {
    public static AutoTypeIME instance;
    private String savedText = "";
    private boolean isRunning = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        isRunning = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE).getBoolean("running", false);
    }
    @Override
    public View onCreateInputView() {
        View v = new View(this);
        v.setLayoutParams(new android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0));
        return v;
    }
    @Override
    public boolean onEvaluateFullscreenMode() { return false; }
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        savedText = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE).getString("text", "");
        if (isRunning) handler.postDelayed(this::send, 10);
    }
    public void startLoop() {
        savedText = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE).getString("text", "");
        isRunning = true;
        getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE).edit().putBoolean("running", true).apply();
        handler.post(this::send);
    }
    public void stopLoop() {
        isRunning = false;
        getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE).edit().putBoolean("running", false).apply();
    }
    private void send() {
        if (!isRunning) return;
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) { handler.postDelayed(this::send, 20); return; }
        ic.beginBatchEdit();
        ic.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
        for (int i = 0; i < 5; i++) ic.commitText(savedText + "\n", 1);
        ic.endBatchEdit();
    }
    @Override
    public void onDestroy() { super.onDestroy(); instance = null; }
}
