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
public class AutoTypeIME extends InputMethodService {
    private String savedText = "";
    private final Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public View onCreateInputView() {
        View v = new View(this);
        v.setLayoutParams(new android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
        return v;
    }
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        loadText();
        handler.postDelayed(this::pasteText, 20);
    }
    private void loadText() {
        SharedPreferences prefs = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE);
        savedText = prefs.getString("text", "");
    }
    private void pasteText() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) { handler.postDelayed(this::pasteText, 30); return; }
        ic.beginBatchEdit();
        ic.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
        ic.commitText(savedText, 1);
        ic.endBatchEdit();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) ic.performEditorAction(EditorInfo.IME_ACTION_SEND);
            handler.postDelayed(this::pasteText, 20);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onEvaluateFullscreenMode() { return false; }
}
