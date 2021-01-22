package com.rgc;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetSyncData {

    DataBaseHelper myDbHelper;
    Connection c;
    int id_U;
    //Context mContext;
    int reciveSize = 1024;
    ProgressBar pb;
    Button r;

    public GetSyncData(Context mContext, Connection c, int id_U, int reciveSize, ProgressBar pb, Button r) {
        this.c = c;
        this.id_U = id_U;
        this.reciveSize = reciveSize;
        myDbHelper = new DataBaseHelper(mContext);
        this.pb = pb;
        this.r = r;
    }

    String response = "";
    boolean succes;
    boolean passwd;
    List<String> list;



    public void execute(String... params) {
        if(pb != null)pb.setVisibility(View.VISIBLE);

        try {
            response = c.sendString( TextUtils.join(";", params) + ";"+ android.os.Build.MODEL, reciveSize);
            list = new ArrayList<String>(Arrays.asList(response.split(";")));
            if (list.get(0).equals("true")) passwd = true;
            else if (list.get(0).equals("false")) passwd = false;
            succes = true;
        } catch (IOException e) {
            e.printStackTrace();
            response = "ERROR: " + e;
            succes = false;

        } catch (IndexOutOfBoundsException e) {
            response = "ERROR: " + e;
        } catch (Exception e) {
            e.printStackTrace();
            response = "ERROR: " + e;
            succes = false;
        }


//        Log.e("mdi",response);
        if (succes && passwd) {
            if(r != null)r.setTextColor(Color.GREEN);
        } else if (!succes || !passwd) {
            if(r != null)r.setTextColor(Color.RED);
            if (!succes) {
                myDbHelper.dodajLog(id_U, response);
            } else if (!passwd) {
                myDbHelper.dodajLog(id_U, list.get(1));
            }
        }
        if(pb != null)pb.setVisibility(View.INVISIBLE);
        myDbHelper.close();
    }

}
