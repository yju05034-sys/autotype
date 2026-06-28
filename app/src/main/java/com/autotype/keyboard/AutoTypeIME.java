package com.autotype.keyboard;
import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
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
        SharedPreferences p = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE);
        isRunning = p.getBoolean("running", false);
    }
    @Override
    public View onCreateInputView() {
        View v = new View(this);
        v.setLayoutParams(new android.view.ViewGroup.LayoutParams(0, 0));
        return v;
    }
    @Override
    public boolean onEvaluateFullscreenMode() { return false; }
    @Override
    public int onEvaluateInputViewShown() { return 0; }
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        loadText();
        if (isRunning) handler.postDelayed(this::sendFiveTimes, 10);
    }
    private void loadText() {
        SharedPreferences p = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE);
        savedText = p.getString("text", "");
    }
    public void startLoop() {
        loadText();
        isRunning = true;
        getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE).edit().putBoolean("running", true).apply();
        handler.post(this::sendFiveTimes);
    }
    public void stopLoop() {
        isRunning = false;
        getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE).edit().putBoolean("running", false).apply();
    }
    private void sendFiveTimes() {
        if (!isRunning) return;
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) { handler.postDelayed(this::sendFiveTimes, 20); return; }
        ic.beginBatchEdit();
        ic.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
        for (int i = 0; i < 5; i++) ic.commitText(savedText + "\n", 1);
        ic.endBatchEdit();
    }
    @Override
    public void onDestroy() { super.onDestroy(); instance = null; }
}
