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
        EditText etText   = findViewById(R.id.et_text);
        Button btnSave    = findViewById(R.id.btn_save);
        Button btnStart   = findViewById(R.id.btn_start);
        Button btnStop    = findViewById(R.id.btn_stop);
        Button btnEnable  = findViewById(R.id.btn_enable);
        Button btnSelect  = findViewById(R.id.btn_select);
        TextView tvStatus = findViewById(R.id.tv_status);
        SharedPreferences prefs = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE);
        etText.setText(prefs.getString("text", ""));
        btnSave.setOnClickListener(v -> {
            prefs.edit().putString("text", etText.getText().toString()).apply();
            tvStatus.setText("✅ ذخیره شد");
        });
        btnStart.setOnClickListener(v -> {
            prefs.edit().putBoolean("running", true).apply();
            if (AutoTypeIME.instance != null) AutoTypeIME.instance.startLoop();
            tvStatus.setText("💀 در حال اجرا");
            finish();
        });
        btnStop.setOnClickListener(v -> {
            prefs.edit().putBoolean("running", false).apply();
            if (AutoTypeIME.instance != null) AutoTypeIME.instance.stopLoop();
            tvStatus.setText("⛔ متوقف شد");
        });
        btnEnable.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        btnSelect.setOnClickListener(v -> ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker());
    }
}
