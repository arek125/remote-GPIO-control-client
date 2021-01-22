package com.rgc;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DevicesCursorAdapter extends CursorAdapter {
    DataBaseHelper myDbHelper;
    public DevicesCursorAdapter(Context context, Cursor cursor, DataBaseHelper myDbHelper) {
        super(context, cursor, 0);
        this.myDbHelper = myDbHelper;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list1, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = view.findViewById(R.id.nazwa);
        tvName.setText(cursor.getString(1));
        TextView tvIp = (TextView) view.findViewById(R.id.ip);
        tvIp.setText(cursor.getString(2));
        TextView tvPort = (TextView) view.findViewById(R.id.port);
        tvPort.setText(cursor.getString(3));
        final ImageView image = view.findViewById(R.id.list_item_image);
        Connection cQuick = new Connection(myDbHelper,cursor.getInt(0),2000,context);
        GetAsyncData execad = new GetAsyncData(new GetAsyncData.AsyncResponse() {
            @Override
            public void processFinish(List<String> list) {
                image.setImageResource(R.drawable.green);

            }
            @Override
            public void processFail(String error) {
                image.setImageResource(R.drawable.red);
            }
        },context,cQuick,cursor.getInt(0),256,null,null);
        //execad.execute("version_check");
        execad.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"version_check");
    }
}
