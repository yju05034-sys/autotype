package com.autotype.keyboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText etText     = findViewById(R.id.et_text);
        Button btnSave      = findViewById(R.id.btn_save);
        Button btnEnable    = findViewById(R.id.btn_enable);
        Button btnSelect    = findViewById(R.id.btn_select);
        TextView tvStatus   = findViewById(R.id.tv_status);

        SharedPreferences prefs = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE);

        // نمایش متن ذخیره شده
        String existing = prefs.getString("text", "");
        if (!existing.isEmpty()) {
            etText.setText(existing);
        }

        // ذخیره متن
        btnSave.setOnClickListener(v -> {
            String text = etText.getText().toString();
            if (text.isEmpty()) {
                tvStatus.setText("❌ متن خالی است!");
                return;
            }
            prefs.edit().putString("text", text).apply();
            tvStatus.setText("✅ متن ذخیره شد!");
        });

        // فعال‌سازی در تنظیمات سیستم
        btnEnable.setOnClickListener(v ->
            startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        );

        // انتخاب کیبورد
        btnSelect.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showInputMethodPicker();
        });
    }
}
