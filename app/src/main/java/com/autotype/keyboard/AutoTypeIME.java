package com.autotype.keyboard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class AutoTypeIME extends InputMethodService {

    private static final String CHANNEL_ID = "autotype_channel";
    private static final String ACTION_START = "com.autotype.START";
    private static final String ACTION_STOP = "com.autotype.STOP";
    private static final int NOTIF_ID = 1;

    private boolean isRunning = false;
    private String savedText = "";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private NotificationManager notifManager;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_START.equals(intent.getAction())) startLoop();
            else if (ACTION_STOP.equals(intent.getAction())) stopLoop();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "AutoType", NotificationManager.IMPORTANCE_LOW);
        notifManager.createNotificationChannel(ch);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_START);
        filter.addAction(ACTION_STOP);
        registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        showNotification(false);
    }

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
        if (isRunning) handler.postDelayed(this::pasteText, 20);
    }

    private void loadText() {
        SharedPreferences prefs = getSharedPreferences("autotype_prefs", Context.MODE_PRIVATE);
        savedText = prefs.getString("text", "");
    }

    private void startLoop() {
        loadText();
        if (savedText.isEmpty()) return;
        isRunning = true;
        showNotification(true);
        pasteText();
    }

    private void stopLoop() {
        isRunning = false;
        showNotification(false);
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

    private void showNotification(boolean running) {
        PendingIntent startI = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_START), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent stopI  = PendingIntent.getBroadcast(this, 1, new Intent(ACTION_STOP),  PendingIntent.FLAG_IMMUTABLE);
        Notification n = new Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .setContentTitle("AutoType")
            .setContentText(running ? "در حال اجرا" : "آماده")
            .setOngoing(true)
            .addAction(running ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play,
                       running ? "خاموش" : "شروع",
                       running ? stopI : startI)
            .build();
        notifManager.notify(NOTIF_ID, n);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        notifManager.cancel(NOTIF_ID);
    }
}
