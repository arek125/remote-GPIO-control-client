package com.rgc;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetAsyncData extends AsyncTask<String, String, Boolean> {

    public interface AsyncResponse {
        void processFinish(List<String> list);
        void processFail(String error);
    }

    public AsyncResponse delegate = null;
    DataBaseHelper myDbHelper;
    Connection c;
    int id_U;
    //Context mContext;
    int reciveSize = 1024;
    ProgressBar pb;
    Button r;

    public GetAsyncData(AsyncResponse delegate, Context mContext, Connection c, int id_U, int reciveSize,ProgressBar pb,Button r) {
        this.delegate = delegate;
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


    @Override
    protected void onPreExecute() {
        if(pb != null)pb.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
    }

    @Override
    protected Boolean doInBackground(String... params) {
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

        return succes;
    }

    @Override
    protected void onPostExecute(Boolean result) {

//        Log.e("mdi",response);
        if (result && passwd) {
            if(r != null)r.setTextColor(Color.GREEN);
            delegate.processFinish(list);
        } else if (!result || !passwd) {
            if(r != null)r.setTextColor(Color.RED);
            if (!result) {
                myDbHelper.dodajLog(id_U, response);
                delegate.processFail(response);
            } else if (!passwd) {
                myDbHelper.dodajLog(id_U, list.get(1));
                delegate.processFail(list.get(1));
            }
        }
        if(pb != null)pb.setVisibility(View.INVISIBLE);
        myDbHelper.close();
        super.onPostExecute(result);
    }

}
