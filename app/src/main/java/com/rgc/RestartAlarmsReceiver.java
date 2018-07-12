package com.rgc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;


public class RestartAlarmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())||"android.intent.action.MY_PACKAGE_REPLACED".equals(intent.getAction())) {
            DataBaseHelper myDbHelper = new DataBaseHelper(context);
            final Cursor kp = myDbHelper.dajPowiadomienia(true,-1);
            while(kp.moveToNext()){
                Notification n = new Notification(kp.getInt(0),context);
                n.startAlarm();
            }
            kp.close();
            myDbHelper.close();
        } else {
            Log.e("mdi", "Received unexpected intent " + intent.toString());
        }
    }
}
