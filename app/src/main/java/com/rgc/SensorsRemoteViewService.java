package com.rgc;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorsRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new SensorsRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class SensorsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int mAppWidgetId,id_U;
    AppWidgetManager appWidgetManager;
    private ArrayList<String[]> sensors=  new ArrayList<>();
    boolean finishFlag = false;

    public SensorsRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.id_U = intent.getIntExtra("ID_U",-1);
        appWidgetManager = AppWidgetManager.getInstance(context);
    }

    public void onCreate() {
    }

    public void onDestroy() {
        sensors.clear();

    }

    public int getCount() {
        return sensors.size();
    }

    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.grid_item_sensor);
        if (position <= getCount()) {
            rv.setTextViewText(R.id.grid_item_name,sensors.get(position)[1]);
            rv.setTextViewText(R.id.grid_item_date,sensors.get(position)[4]);
            rv.setTextViewText(R.id.grid_item_value,sensors.get(position)[2]+sensors.get(position)[3]);
            Intent fillInIntent = new Intent();
            fillInIntent.setAction("REFRESH_SENSOR");
            fillInIntent.putExtra("id_u",id_U);
            fillInIntent.putExtra("id",sensors.get(position)[0]);
            rv.setOnClickFillInIntent(R.id.llgi, fillInIntent);
        }

        return rv;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
                final RemoteViews rv = new RemoteViews(mContext.getPackageName(),R.layout.on_off_widgetm);
                rv.setViewVisibility(R.id.pbProgressAction, View.VISIBLE);
                appWidgetManager.updateAppWidget(mAppWidgetId,rv);
                final DataBaseHelper db = new DataBaseHelper(mContext);
                final Cursor c = db.dajUrzadzenie(id_U);
                c.moveToFirst();
                boolean tcpOnly=false;
                if(!c.isNull(17))tcpOnly = c.getInt(17)==1;
                Connection cQuick = new Connection(c.getString(2), c.getInt(3), c.getString(4), c.getString(5), tcpOnly,5000);
                GetAsyncData execad = new GetAsyncData(new GetAsyncData.AsyncResponse() {
                    @Override
                    public void processFinish(List<String> list) {
                        sensors.clear();
                        for (int i = 2; i < list.size()-1; i+=11) {//id;name;value;unit;date
                            String sensor[] = {list.get(i),list.get(i+1),list.get(i+3),list.get(i+6),AdvSAListAdapter.UTCtoLocalDate(list.get(i+7),"yyyy-MM-dd HH:mm:ss",false,"MM/dd HH:mm")};
                            sensors.add(sensor);
                        }
                        rv.setTextColor(R.id.refresh, Color.GREEN);
                        finishFlag = true;
                    }
                    @Override
                    public void processFail(String error) {
                        rv.setTextColor(R.id.refresh, Color.RED);
                        finishFlag = true;
                    }
                },mContext,cQuick,c.getInt(0),32384,null,null);
                execad.execute("SENSOR_list");
                while (!finishFlag) {
                    try { Thread.sleep(100); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
                finishFlag = false;
                rv.setViewVisibility(R.id.pbProgressAction, View.INVISIBLE);
                appWidgetManager.updateAppWidget(mAppWidgetId,rv);
                c.close();
                db.close();

    }
}