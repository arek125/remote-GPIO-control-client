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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnOffRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new OnOffRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class OnOffRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int mAppWidgetId,id_U;
    AppWidgetManager appWidgetManager;
    private ArrayList<String> idki=  new ArrayList<String>();
    private ArrayList<String> gpios=  new ArrayList<String>();
    private ArrayList<String> stany=  new ArrayList<String>();
    private ArrayList<String> nazwy=  new ArrayList<String>();
    private ArrayList<String> reverses=  new ArrayList<String>();

    public OnOffRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.id_U = intent.getIntExtra("ID_U",-1);
        appWidgetManager = AppWidgetManager.getInstance(context);
    }

    public void onCreate() {
    }

    public void onDestroy() {
        idki.clear();
        gpios.clear();
        stany.clear();
        nazwy.clear();
        reverses.clear();
    }

    public int getCount() {
        return idki.size();
    }

    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.grid_item_w);
        if (position <= getCount()) {
            String reverse= "";
            if(reverses.get(position).equals("1"))reverse="(r)";
            rv.setTextViewText(R.id.grid_item_name,nazwy.get(position));
            rv.setTextViewText(R.id.grid_item_gpio,"GPIO: "+gpios.get(position)+reverse);
            if ((stany.get(position).equals("1")&&reverses.get(position).equals("0"))||(stany.get(position).equals("0")&&reverses.get(position).equals("1")))
                rv.setImageViewResource(R.id.grid_item_image,R.drawable.green);
            else if ((stany.get(position).equals("0")&&reverses.get(position).equals("0"))||(stany.get(position).equals("1")&&reverses.get(position).equals("1")))
                rv.setImageViewResource(R.id.grid_item_image,R.drawable.red);
            else rv.setImageViewResource(R.id.grid_item_image,R.drawable.yelow);

            Intent fillInIntent = new Intent();
            fillInIntent.setAction("SWITCH");
            fillInIntent.putExtra("id_u",id_U);
            fillInIntent.putExtra("id",idki.get(position));
            fillInIntent.putExtra("stan",stany.get(position));
            fillInIntent.putExtra("reverse",reverses.get(position));
            fillInIntent.putExtra("gpio",gpios.get(position));
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
                RemoteViews rv = new RemoteViews(mContext.getPackageName(),R.layout.on_off_widgetm);
                rv.setViewVisibility(R.id.pbProgressAction, View.VISIBLE);
                appWidgetManager.updateAppWidget(mAppWidgetId,rv);
                DataBaseHelper db = new DataBaseHelper(mContext);
                //Cursor c = db.dajUrzadzenie(id_U);
                //c.moveToFirst();
                String response = "";
                boolean succes = false;
                boolean passwd = false,tcpOnly=false;
                List<String> list = new ArrayList<String>();
                //int dstPort = c.getInt(3);
                //String dstAddress = c.getString(2), dstPassword = c.getString(4), encKey = c.getString(5);
                //if(!c.isNull(17))tcpOnly = c.getInt(17)==1;
                //c.close();
                try {
                    //Connection conn = new Connection(dstAddress, dstPort, dstPassword, encKey,tcpOnly);
                    Connection conn = new Connection(db,id_U,5000,mContext);
                    //conn.timeout = 5000;
                    response = conn.sendString("GPIO_OlistT0", 32384);
                    list = new ArrayList<String>(Arrays.asList(response.split(";")));
                    if (list.get(0).equals("true")) passwd = true;
                    else if (list.get(0).equals("false")) passwd = false;
                    succes = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    response = "ERROR: " + e;
                    succes = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    response = "ERROR: " + e;
                    succes = false;
                }
                if (succes && passwd) {
                    rv.setTextColor(R.id.refresh, Color.GREEN);
                    idki.clear();
                    gpios.clear();
                    stany.clear();
                    nazwy.clear();
                    reverses.clear();
                    for (int j = 2; j < (list.size() - 1); j = j + 6) {
                        idki.add(list.get(j));
                        gpios.add(list.get(j + 1));
                        stany.add(list.get(j + 2));
                        nazwy.add(list.get(j + 3));
                        reverses.add(list.get(j + 4));
                        if (j > 1000) break;
                    }

                } else if (!succes || !passwd) {
                    rv.setTextColor(R.id.refresh, Color.RED);
                    if (!succes) {
                        db.dodajLog(id_U, response);
                    } else if (!passwd) {
                        db.dodajLog(id_U, response);
                    }
                }
                rv.setViewVisibility(R.id.pbProgressAction, View.INVISIBLE);
                appWidgetManager.updateAppWidget(mAppWidgetId,rv);
                db.close();
    }
}