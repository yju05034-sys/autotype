package com.autotype.keyboard;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
    private NotificationManager notifManager;
    private static final String CH = "autotype";
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifManager.createNotificationChannel(new NotificationChannel(CH, "AutoType", NotificationManager.IMPORTANCE_LOW));
        showNotif(false);
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
        if (isRunning) handler.postDelayed(this::sendFiveTimes, 20);
    }
    private void loadText() {
        SharedPreferences p = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE);
        savedText = p.getString("text", "");
    }
    public void startLoop() {
        loadText();
        if (savedText.isEmpty()) return;
        isRunning = true;
        showNotif(true);
        handler.post(this::sendFiveTimes);
    }
    public void stopLoop() {
        isRunning = false;
        showNotif(false);
    }
    private void sendFiveTimes() {
        if (!isRunning) return;
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) { handler.postDelayed(this::sendFiveTimes, 30); return; }
        ic.beginBatchEdit();
        ic.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
        for (int i = 0; i < 5; i++) {
            ic.commitText(savedText + "\n", 1);
        }
        ic.endBatchEdit();
    }
    @Override
    public boolean onEvaluateFullscreenMode() { return false; }
    private void showNotif(boolean running) {
        PendingIntent pi = PendingIntent.getBroadcast(this, 0,
            new Intent(running ? "com.autotype.STOP" : "com.autotype.START").setPackage(getPackageName()),
            PendingIntent.FLAG_IMMUTABLE);
        Notification n = new Notification.Builder(this, CH)
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .setContentTitle("AutoType")
            .setContentText(running ? "در حال اجرا" : "آماده")
            .addAction(0, running ? "⏹ توقف" : "▶ شروع", pi)
            .setOngoing(true).build();
        notifManager.notify(1, n);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        notifManager.cancel(1);
    }
}
