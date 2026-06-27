package com.autotype.keyboard;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class ControlReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AutoTypeIME.instance == null) return;
        if ("com.autotype.START".equals(intent.getAction())) AutoTypeIME.instance.startLoop();
        else if ("com.autotype.STOP".equals(intent.getAction())) AutoTypeIME.instance.stopLoop();
    }
}
