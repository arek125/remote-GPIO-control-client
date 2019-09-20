package com.rgc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.SystemClock;
import android.view.View;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MultiTypeWidget extends AppWidgetProvider {
    private PendingIntent service;
    RemoteViews remoteViews;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent i = new Intent(context, UpdateMultiTypeWidgetService.class);

        if (service == null) {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000, service);
        final int count = appWidgetIds.length;
        SharedPreferences prefs = context.getSharedPreferences("WOF", MODE_PRIVATE);
        final DataBaseHelper db = new DataBaseHelper(context);
        for (int j = 0; j < count; j++) {
            int widgetId = appWidgetIds[j];
//            AppWidgetProviderInfo pr =  appWidgetManager.getAppWidgetInfo(widgetId);
//            String widgetName = pr.loadLabel(context.getPackageManager());
            int id_U =  prefs.getInt("D_ID"+widgetId,-1);
            String type = prefs.getString("TYPE"+widgetId,"On/Off Outputs");
            remoteViews = new RemoteViews(context.getPackageName(),R.layout.on_off_widgetm);
            if (id_U != -1) {
                Cursor c = db.dajUrzadzenie(id_U);
                c.moveToFirst();
                remoteViews.setTextViewText(R.id.title, c.getString(1) + " - "+type);
                Intent intentMA = new Intent(context, MainActivity.class);
                intentMA.putExtra("ID_U",c.getInt(0));
                remoteViews.setOnClickPendingIntent(R.id.title ,PendingIntent.getActivity(context, id_U, intentMA, PendingIntent.FLAG_UPDATE_CURRENT));
                remoteViews.setOnClickPendingIntent(R.id.refresh, MultiTypeWidgetConfActivity.getPendingSelfIntent(context, "REFRESH",widgetId));
                //remoteViews.setPendingIntentTemplate(R.id.switches, OnOffWidgetConfActivity.getPendingSelfIntent(context, "SWITCH",widgetId));
                Intent inten1t;
                switch(type){
                    case "On/Off Outputs":
                        remoteViews.setPendingIntentTemplate(R.id.switches, MultiTypeWidgetConfActivity.getPendingSelfIntent(context, "SWITCH",widgetId));
                        inten1t = new Intent(context, OnOffRemoteViewService.class);
                        break;
                    case "Sensors":
                        remoteViews.setPendingIntentTemplate(R.id.switches, MultiTypeWidgetConfActivity.getPendingSelfIntent(context, "REFRESH_SENSOR",widgetId));
                        inten1t = new Intent(context, SensorsRemoteViewService.class);
                        break;
                    case "Chains":
                        remoteViews.setPendingIntentTemplate(R.id.switches, MultiTypeWidgetConfActivity.getPendingSelfIntent(context, "CHAIN_EXE",widgetId));
                        inten1t = new Intent(context, ChainsRemoteViewService.class);
                        break;
                    default:
                            inten1t = new Intent(context, OnOffRemoteViewService.class);
                }
                inten1t.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                inten1t.setData(Uri.parse(inten1t.toUri(Intent.URI_INTENT_SCHEME)));
                inten1t.putExtra("ID_U", c.getInt(0));
                remoteViews.setRemoteAdapter(R.id.switches, inten1t);
                appWidgetManager.updateAppWidget(widgetId, remoteViews);
                appWidgetManager.notifyAppWidgetViewDataChanged(widgetId,R.id.switches);
                c.close();
            }
        }
        db.close();
    }

    public void onReceive(final Context context,final Intent intent) {
        super.onReceive(context, intent);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final int mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if ("SWITCH".equals(intent.getAction())){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int stanAktualny = Integer.parseInt(intent.getStringExtra("stan"));
                    int id_U = intent.getIntExtra("id_u",-1);
                    int reverse = Integer.parseInt(intent.getStringExtra("reverse"));
                    String idt = intent.getStringExtra("id");
                    String gpio = intent.getStringExtra("gpio");
                    String stanUstaw = "";
                    if (stanAktualny==1||(stanAktualny==2 && reverse==1))
                        stanUstaw="0";
                    else
                        stanUstaw="1";
                    //Log.d("Mdi",stanUstaw);
                    RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.on_off_widgetm);
                    rv.setViewVisibility(R.id.pbProgressAction, View.VISIBLE);
                    appWidgetManager.updateAppWidget(mAppWidgetId,rv);
                    DataBaseHelper db = new DataBaseHelper(context);
                    Cursor c = db.dajUrzadzenie(id_U);
                    c.moveToFirst();
                    String response = "";
                    boolean passwd = false;
                    boolean succes = false,tcpOnly = false;
                    List<String> list = new ArrayList<String>();
                    int dstPort = c.getInt(3);
                    String dstAddress = c.getString(2), dstPassword = c.getString(4), encKey = c.getString(5);
                    if(!c.isNull(17))tcpOnly = c.getInt(17)==1;
                    c.close();
                    try {
                        Connection conn = new Connection(dstAddress, dstPort, dstPassword, encKey,tcpOnly);
                        response = conn.sendString("GPIO_set"+";"+idt+";"+gpio+";"+stanUstaw+";"+GPIO_Status.DatetoString(new Date())+";"+String.valueOf(reverse)+";"+android.os.Build.MODEL, 256);
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
                        appWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.switches);
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
            }).start();

        } else if ("REFRESH_SENSOR".equals(intent.getAction()) || "CHAIN_EXE".equals(intent.getAction())){
            int id_U = intent.getIntExtra("id_u",-1);
            String idt = intent.getStringExtra("id");
            final RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.on_off_widgetm);
            rv.setViewVisibility(R.id.pbProgressAction, View.VISIBLE);
            appWidgetManager.updateAppWidget(mAppWidgetId,rv);
            DataBaseHelper db = new DataBaseHelper(context);
            Cursor c = db.dajUrzadzenie(id_U);
            c.moveToFirst();
            boolean tcpOnly=false;
            if(!c.isNull(17))tcpOnly = c.getInt(17)==1;
            Connection cQuick = new Connection(c.getString(2), c.getInt(3), c.getString(4), c.getString(5), tcpOnly,5000);
            GetAsyncData execad = new GetAsyncData(new GetAsyncData.AsyncResponse() {
                @Override
                public void processFinish(List<String> list) {
                    rv.setTextColor(R.id.refresh, Color.GREEN);
                    appWidgetManager.updateAppWidget(mAppWidgetId,rv);
                    appWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.switches);
                }
                @Override
                public void processFail(String error) {
                    rv.setTextColor(R.id.refresh, Color.RED);
                    appWidgetManager.updateAppWidget(mAppWidgetId,rv);
                }
            },context,cQuick,c.getInt(0),256,null,null);
            if("REFRESH_SENSOR".equals(intent.getAction()))
                execad.execute("SENSOR_refresh",idt);
            else
                execad.execute("GPIO_ChainExecute",idt);
        }else if ("REFRESH".equals(intent.getAction())){
            //int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, OnOffWidget.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.switches);
//            Log.d("Mdi",String.valueOf(mAppWidgetId));

        }
    }
}