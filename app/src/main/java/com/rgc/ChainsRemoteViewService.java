package com.rgc;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class ChainsRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ChainsRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ChainsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int mAppWidgetId,id_U;
    AppWidgetManager appWidgetManager;
    private ArrayList<Chain> chains=  new ArrayList<>();
    boolean finishFlag = false;

    public ChainsRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.id_U = intent.getIntExtra("ID_U",-1);
        appWidgetManager = AppWidgetManager.getInstance(context);
    }

    public void onCreate() {
    }

    public void onDestroy() {
        chains.clear();
    }

    public int getCount() {
        return chains.size();
    }

    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.grid_item_w);
        if (position <= getCount()) {
            Chain ch = chains.get(position);
            rv.setTextViewText(R.id.grid_item_name,ch.nazwa);
            rv.setTextViewText(R.id.grid_item_gpio,ch.nazwaStatusu);
            if(ch.status == 0)
                rv.setImageViewResource(R.id.grid_item_image,R.drawable.green);
            else rv.setImageViewResource(R.id.grid_item_image,R.drawable.yelow);
            Intent fillInIntent = new Intent();
            fillInIntent.setAction("CHAIN_EXE");
            fillInIntent.putExtra("id_u",id_U);
            fillInIntent.putExtra("id",String.valueOf(ch.id));

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
                //final Cursor c = db.dajUrzadzenie(id_U);
                //c.moveToFirst();
               // boolean tcpOnly=false;
                //if(!c.isNull(17))tcpOnly = c.getInt(17)==1;
                //Connection cQuick = new Connection(c.getString(2), c.getInt(3), c.getString(4), c.getString(5), tcpOnly,5000);
                Connection cQuick = new Connection(db,id_U,5000,mContext);
                GetAsyncData execad = new GetAsyncData(new GetAsyncData.AsyncResponse() {
                    @Override
                    public void processFinish(List<String> list) {
                        chains.clear();
                        for (int i = 2; i < list.size()-1; i+=7)
                            chains.add(new Chain(
                                    Integer.parseInt(list.get(i)),
                                    Integer.parseInt(list.get(i+1)),
                                    list.get(i+1).equals("0")?"Ready":"At Lp. "+list.get(i+1),
                                    list.get(i+2),
                                    list.get(i+4),
                                    list.get(i+5).equals("1")
                            ));
                        rv.setTextColor(R.id.refresh, Color.GREEN);
                        finishFlag = true;
                    }
                    @Override
                    public void processFail(String error) {
                        rv.setTextColor(R.id.refresh, Color.RED);
                        finishFlag = true;
                    }
                },mContext,cQuick,id_U,16384,null,null);
                execad.execute("GPIO_ChainList");
                while (!finishFlag) {
                    try { Thread.sleep(100); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
                finishFlag = false;
                rv.setViewVisibility(R.id.pbProgressAction, View.INVISIBLE);
                appWidgetManager.updateAppWidget(mAppWidgetId,rv);
                //c.close();
                db.close();

    }
}