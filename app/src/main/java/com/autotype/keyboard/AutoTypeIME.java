package com.autotype.keyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.TextView;

public class AutoTypeIME extends InputMethodService {

    private boolean isRunning = false;
    private String savedText = "";
    private Button btnStart, btnStop;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.keyboard_view, null);
        btnStart = view.findViewById(R.id.btn_start);
        btnStop  = view.findViewById(R.id.btn_stop);
        loadText();
        btnStart.setOnClickListener(v -> startLoop());
        btnStop.setOnClickListener(v -> stopLoop());
        return view;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        loadText();
        if (isRunning) {
            handler.postDelayed(this::pasteText, 30);
        }
    }

    private void loadText() {
        SharedPreferences prefs = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE);
        savedText = prefs.getString("text", "");
    }

    private void startLoop() {
        if (savedText.isEmpty()) return;
        isRunning = true;
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        pasteText();
    }

    private void stopLoop() {
        isRunning = false;
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
    }

    private void pasteText() {
        if (!isRunning) return;
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            handler.postDelayed(this::pasteText, 50);
            return;
        }
        ic.beginBatchEdit();
        ic.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
        ic.commitText(savedText, 1);
        ic.endBatchEdit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && isRunning) {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.performEditorAction(EditorInfo.IME_ACTION_SEND);
            }
            handler.postDelayed(this::pasteText, 30);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }
}
