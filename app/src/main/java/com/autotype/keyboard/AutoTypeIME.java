package com.autotype.keyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

public class AutoTypeIME extends InputMethodService {

    private String savedText = "";
    private boolean isRunning = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private WindowManager windowManager;
    private View floatView;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public View onCreateInputView() {
        View v = new View(this);
        v.setLayoutParams(new android.view.ViewGroup.LayoutParams(0, 0));
        return v;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        loadText();
        showFloatButton();
        if (isRunning) handler.postDelayed(this::pasteText, 20);
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        removeFloatButton();
    }

    private void showFloatButton() {
        if (floatView != null) return;
        floatView = LayoutInflater.from(this).inflate(R.layout.float_buttons, null);

        Button btnStart = floatView.findViewById(R.id.btn_start);
        Button btnStop  = floatView.findViewById(R.id.btn_stop);

        btnStop.setVisibility(View.GONE);

        btnStart.setOnClickListener(v -> {
            isRunning = true;
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);
            pasteText();
        });

        btnStop.setOnClickListener(v -> {
            isRunning = false;
            btnStop.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.x = 16;
        params.y = 200;

        windowManager.addView(floatView, params);
    }

    private void removeFloatButton() {
        if (floatView != null) {
            windowManager.removeView(floatView);
            floatView = null;
        }
    }

    private void loadText() {
        SharedPreferences prefs = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE);
        savedText = prefs.getString("text", "");
    }

    private void pasteText() {
        if (!isRunning) return;
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) { handler.postDelayed(this::pasteText, 30); return; }
        ic.beginBatchEdit();
        ic.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
        ic.commitText(savedText, 1);
        ic.endBatchEdit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && isRunning) {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) ic.performEditorAction(EditorInfo.IME_ACTION_SEND);
            handler.postDelayed(this::pasteText, 20);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onEvaluateFullscreenMode() { return false; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFloatButton();
    }
}
