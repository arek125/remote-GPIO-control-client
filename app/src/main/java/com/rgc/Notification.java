package com.rgc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Notification {
    int id;
    private Context context;
    private int precise,interval;

    Notification(int id, Context context){
        this.id = id;
        this.context = context;
        DataBaseHelper myDbHelper = new DataBaseHelper(context);
        Cursor c_pow = myDbHelper.dajPowiadomienie(id);
        if(c_pow.moveToFirst()){
            precise = c_pow.getInt(6);
            interval = c_pow.getInt(7)*1000;
        }
        c_pow.close();
        myDbHelper.close();
    }
    public void startAlarm(){

        Calendar cal = Calendar.getInstance();
        Intent notificationIntent = new Intent(context,AlarmReceiver.class);
        //notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("NOTIF_ID", id);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(precise == 1) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(interval);
            if(minutes <=15)am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, broadcast);
            else if(minutes>15 && minutes<=30)am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_HALF_HOUR, broadcast);
            else if(minutes>30 && minutes<=720)am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, broadcast);
            else if(minutes>720)am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, broadcast);
        }
        else if(precise == 2) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval, broadcast);
        }
    }
    public void cancelAlarm(){
        Intent notificationIntent = new Intent(context,AlarmReceiver.class);
        notificationIntent.putExtra("NOTIF_ID", id);
        //notificationIntent.addCategory("android.intent.category.DEFAULT");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(broadcast);
    }
}
