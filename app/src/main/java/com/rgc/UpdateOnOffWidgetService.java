package com.rgc;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class UpdateOnOffWidgetService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ComponentName theWidget = new ComponentName(this, OnOffWidget.class);
        final AppWidgetManager manager = AppWidgetManager.getInstance(this);
        final int[] ids = manager.getAppWidgetIds(theWidget);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                manager.notifyAppWidgetViewDataChanged(ids, R.id.switches);
//            }
//        });
        return super.onStartCommand(intent, flags, startId);
    }
}
