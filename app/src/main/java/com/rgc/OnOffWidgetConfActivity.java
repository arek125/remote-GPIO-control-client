package com.rgc;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class OnOffWidgetConfActivity extends Activity {
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Button btAdd;
    private Spinner conns;
    private AppWidgetManager widgetManager;
    private RemoteViews views;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setResult(RESULT_CANCELED);
        // activity stuffs
        setContentView(R.layout.add_widget);
        btAdd = findViewById(R.id.addWidget);
        conns = findViewById(R.id.connections);
        String[] columns = new String[] { "nazwa" };
        int[] to = new int[] { android.R.id.text1 };
        final DataBaseHelper db = new DataBaseHelper(getApplicationContext());
        final Cursor c = db.dajUrzadzenia();
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, c, columns, to,0);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conns.setAdapter(mAdapter);
        widgetManager = AppWidgetManager.getInstance(this);
        views = new RemoteViews(this.getPackageName(), R.layout.on_off_widgetm);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID || c.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Define server connection first !", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conns != null && conns.getSelectedItem() !=null){
                    c.moveToPosition(conns.getSelectedItemPosition());
                    views.setTextViewText(R.id.title, c.getString(1));
                    Intent intentMA = new Intent(getApplicationContext(), MainActivity.class);
                    intentMA.putExtra("ID_U",c.getInt(0));
                    views.setOnClickPendingIntent(R.id.title,PendingIntent.getActivity(getApplicationContext(), c.getInt(0), intentMA, PendingIntent.FLAG_UPDATE_CURRENT));
                    views.setOnClickPendingIntent(R.id.refresh,getPendingSelfIntent(OnOffWidgetConfActivity.this, "REFRESH",mAppWidgetId));
                    views.setPendingIntentTemplate(R.id.switches, getPendingSelfIntent(OnOffWidgetConfActivity.this, "SWITCH",mAppWidgetId));
                    Intent inten1t = new Intent(getApplicationContext(), OnOffRemoteViewService.class);
                    inten1t.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    inten1t.setData(Uri.parse(inten1t.toUri(Intent.URI_INTENT_SCHEME)));
                    inten1t.putExtra("ID_U", c.getInt(0));
                    views.setRemoteAdapter(R.id.switches, inten1t);

                    //views.setEmptyView(R.id.switches,R.id.title);

                    widgetManager.updateAppWidget(mAppWidgetId, views);
                    widgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.switches);
                    Intent resultValue = new Intent();
                    // Set the results as expected from a 'configure activity'.
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    resultValue.putExtra("D_ID",c.getInt(0));
                    SharedPreferences.Editor editor = getSharedPreferences("WOF", MODE_PRIVATE).edit();
                    editor.putInt("D_ID"+mAppWidgetId, c.getInt(0));
                    editor.apply();
                    setResult(RESULT_OK, resultValue);
                    finish();
                    db.close();
                }
                else
                    Toast.makeText(getApplicationContext(), "Define server connection first !", Toast.LENGTH_LONG).show();
            }
        });
    }

    static protected PendingIntent getPendingSelfIntent(Context context, String action, int mAppWidgetId) {
        Intent intent = new Intent(context, OnOffWidget.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
