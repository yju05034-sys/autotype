package com.autotype.keyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.TextView;

public class AutoTypeIME extends InputMethodService {

    private boolean isRunning = false;
    private String savedText = "";

    private TextView tvPreview, tvStatus;
    private Button btnStart, btnStop;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.keyboard_view, null);

        tvPreview = view.findViewById(R.id.tv_text_preview);
        tvStatus  = view.findViewById(R.id.tv_status);
        btnStart  = view.findViewById(R.id.btn_start);
        btnStop   = view.findViewById(R.id.btn_stop);

        loadText();
        updatePreview();

        btnStart.setOnClickListener(v -> startLoop());
        btnStop.setOnClickListener(v -> stopLoop());

        return view;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        loadText();
        updatePreview();

        // اگر حلقه فعال بود، بلافاصله paste کن
        if (isRunning) {
            handler.postDelayed(this::pasteText, 60);
        }
    }

    private void loadText() {
        SharedPreferences prefs = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE);
        savedText = prefs.getString("text", "");
    }

    private void updatePreview() {
        if (tvPreview == null) return;
        if (savedText.isEmpty()) {
            tvPreview.setText("⚠ ابتدا در برنامه AutoType متن ذخیره کنید");
        } else {
            tvPreview.setText(savedText);
        }
    }

    private void startLoop() {
        if (savedText.isEmpty()) {
            setStatus("❌ متنی ذخیره نشده!");
            return;
        }
        isRunning = true;
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        setStatus("🟢 فعال | Enter بزن → دوباره paste میشه");
        pasteText();
    }

    private void stopLoop() {
        isRunning = false;
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        setStatus("⏹ متوقف شد");
    }

    private void pasteText() {
        if (!isRunning) return;

        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            handler.postDelayed(this::pasteText, 100);
            return;
        }

        // پاک کردن محتوای فعلی فیلد و paste یکجای متن
        ic.beginBatchEdit();
        ic.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
        ic.commitText(savedText, 1);
        ic.endBatchEdit();

        setStatus("✅ متن paste شد | حالا Enter بزن");
    }

    // وقتی کاربر Enter می‌زند (یا دکمه ارسال)
    @Override
    public void sendDefaultEditorAction(boolean fromEnterKey) {
        super.sendDefaultEditorAction(fromEnterKey);

        if (isRunning) {
            setStatus("⏳ در حال آماده‌سازی...");
            // کوتاه‌ترین تاخیر ممکن قبل از paste مجدد
            handler.postDelayed(this::pasteText, 80);
        }
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false; // هرگز fullscreen نشو
    }

    private void setStatus(String msg) {
        handler.post(() -> {
            if (tvStatus != null) tvStatus.setText(msg);
        });
    }
}
