package com.rgc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Notifications extends Fragment{
    static String dstName;
    static int Page, arTime;
    ProgressBar pb;
    boolean menuCreated = false, VisibleToUser = true, running;
    boolean handlerOn = false;
    Button r;
    static int id_U;
    ListView listview;
    Handler handler = new Handler();
    Runnable runabble = null;
    public static Context mContext;
    LayoutInflater inflater;
    Map <Integer,String> gpio_io = new HashMap<Integer,String>();
    Map <String,String> sensors = new HashMap<String,String>();
    Map <String,String> sensorsUnit = new HashMap<String,String>();
    CountDownLatch latch;
    static Connection c;

    public Notifications() {
    }

    public static Notifications newInstance(int page,String name, Connection conn, Context Context, int artime, int id_u) {
        Notifications fragment = new Notifications();
        Page = page;
        dstName = name;
        c = conn;
        arTime = artime;
        mContext = Context;
        id_U = id_u;
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduled_actions, container, false);
        //Log.i("Mdi","CREATING");
        listview = (ListView) rootView.findViewById(R.id.listView2);
        setHasOptionsMenu(true);
        this.inflater = inflater;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,final int position, long id) {
                notifDialog(true,(Cursor) parent.getItemAtPosition(position));
            }
        });
//        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//                return true;
//            }
//        });
        return rootView;
    }

    public void check_state() {
        if (running == false) {
            latch = new CountDownLatch(2);
            NotificationsTask taskIOnames = new NotificationsTask();
            taskIOnames.execute("GPIO_OInames");
            NotificationsTask taskSensorNames = new NotificationsTask();
            taskSensorNames.execute("SENSOR_names");
            final DataBaseHelper myDbHelper = new DataBaseHelper(mContext);
            new Thread(new Runnable() {
                public void run() {
                    final Cursor k = myDbHelper.dajPowiadomienia(false,id_U);
                    //Toast.makeText(mContext, "Started!", Toast.LENGTH_SHORT).show();
                    try {
                        latch.await(10, TimeUnit.SECONDS);
                    }catch (InterruptedException e){}
                    //Toast.makeText(mContext, "Loaded!", Toast.LENGTH_SHORT).show();
                    if(k.getColumnCount() > 0){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String[] columns = new String[]{"target_id", "type", "condition", "value", "repeat_sec"};
                                int[] to = new int[]{R.id.target, R.id.type, R.id.condition, R.id.value, R.id.imageView};
                                SimpleCursorAdapter customAdapter = new SimpleCursorAdapter(mContext, R.layout.notification_item, k, columns, to,0);

                                customAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                                    @Override
                                    public boolean setViewValue(View view, Cursor cursor, int column) {
                                        if (view.getId() == R.id.target){
                                            TextView tv = (TextView) view;
                                            if (cursor.getString(3).equals("i/o"))
                                                tv.setText(gpio_io.get(cursor.getInt(2)));
                                            else if (cursor.getString(3).equals("sensor"))
                                                tv.setText(sensors.get(cursor.getString(2)));
                                            else
                                                return false;
                                            return true;
                                        }
                                        else if(view.getId() == R.id.value){
                                            TextView tv = (TextView) view;
                                            if (cursor.getString(3).equals("sensor"))
                                                tv.setText(cursor.getString(4)+sensorsUnit.get(cursor.getString(2)));
                                            else
                                                return false;
                                            return true;
                                        }
                                        else if(view.getId() == R.id.imageView){
                                            ImageView iv =(ImageView) view;
                                            if(cursor.getInt(6)>0)
                                                iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.green));
                                            else iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.red));
                                            return true;
                                        }

                                        return false;
                                    }
                                });
                                listview.setAdapter(customAdapter);
                                myDbHelper.close();
                            }
                        });

                    }
                }
            }).start();

        }
    }

    public class NotificationsTask extends AsyncTask<String, Void, Boolean> {

        String response = "";
        boolean succes;
        boolean passwd;
        List<String> list;



        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
            running = true;
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                if (params[0].equals("GPIO_OInames"))
                    response = c.sendString( params[0] + ";", 8192);
                else if (params[0].equals("SENSOR_names"))
                    response = c.sendString(params[0] + ";", 8192);
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


            return succes;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            running = false;
            DataBaseHelper myDbHelper = new DataBaseHelper(mContext);
            if (result && passwd) {
                r.setTextColor(Color.GREEN);
                if (list.get(1).equals("GPIO_OInames")) {
                    gpio_io.clear();
                    for (int i = 2; i < list.size()-1; i+=2) {
                        gpio_io.put(Integer.parseInt(list.get(i)),list.get(i+1));
                    }
                    //Toast.makeText(mContext, "GPIO_IOnames!"+gpio_io.get(1), Toast.LENGTH_SHORT).show();
                }else if (list.get(1).equals("SENSOR_names")){
                    sensors.clear();sensorsUnit.clear();
                    for (int i = 2; i < list.size()-1; i+=3) {
                        sensors.put(list.get(i),list.get(i+1));
                        sensorsUnit.put(list.get(i),list.get(i+2));
                    }
                    //Toast.makeText(mContext, "SENSOR_names!", Toast.LENGTH_SHORT).show();
                }
            } else if (!result || !passwd) {
                if (!result) {
                    myDbHelper.dodajLog(id_U, response);
                    r.setTextColor(Color.RED);
                } else if (!passwd) {
                    myDbHelper.dodajLog(id_U, list.get(1));
                    r.setTextColor(Color.RED);
                }
            }
            latch.countDown();
            pb.setVisibility(View.INVISIBLE);
            myDbHelper.close();
            super.onPostExecute(result);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem actionViewItem = menu.findItem(R.id.miActionButton);
        View v = MenuItemCompat.getActionView(actionViewItem);
        pb = (ProgressBar) v.findViewById(R.id.pbProgressAction);
        Button b = (Button) v.findViewById(R.id.btnCustomAction);
        b.setVisibility(View.VISIBLE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifDialog(false,null);
            }
        });
        r = (Button) v.findViewById(R.id.btnCustomAction3);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.cancel();
                check_state();
            }
        });
        r.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Chains.showLogs(mContext);
                return false;
            }
        });
        pb.setVisibility(View.INVISIBLE);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        menuCreated = true;
        running = false;
        c.cancel();
        check_state();
        //hendlerm2();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        VisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
        } else {
            handler.removeCallbacks(runabble);
            handlerOn = false;
        }

    }

    @Override
    public void onStop() {
        handler.removeCallbacks(runabble);
        handlerOn = false;
        //menuCreated=false;
        super.onStop();
    }

    @Override
    public void onResume() {
        if (menuCreated && VisibleToUser) {
            check_state();
        }
        super.onResume();
    }

    public void notifDialog(final boolean editMode,final Cursor k){
        View view = inflater.inflate(R.layout.notif_edit, null );

        final TableRow trTargetIO = view.findViewById(R.id.targetioRow);
        final TableRow trTargetS = view.findViewById(R.id.targetSRow);
        final TableRow trCond = view.findViewById(R.id.condRow);
        final TableRow trValueIO = view.findViewById(R.id.valueioRow);
        final TableRow trValueS = view.findViewById(R.id.valueSRow);

        final CheckBox activeCh = view.findViewById(R.id.active);
        final Spinner typeS = view.findViewById(R.id.type);
        final List<String> typeList = new ArrayList<String>();
        typeList.add("i/o");
        typeList.add("sensor");
        final ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, typeList);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeS.setAdapter(typeAdapter);
        typeS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    trTargetIO.setVisibility(View.VISIBLE);
                    trTargetS.setVisibility(View.GONE);
                    trCond.setVisibility(View.GONE);
                    trValueIO.setVisibility(View.VISIBLE);
                    trValueS.setVisibility(View.GONE);
                }
                else if(position == 1){
                    trTargetIO.setVisibility(View.GONE);
                    trTargetS.setVisibility(View.VISIBLE);
                    trCond.setVisibility(View.VISIBLE);
                    trValueIO.setVisibility(View.GONE);
                    trValueS.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        final Spinner targetIO = view.findViewById(R.id.targetio);
        final List<String> targetIOlist = new ArrayList<String>();
        targetIOlist.addAll(gpio_io.values());
        final List<Integer> targetIOlistIDs = new ArrayList<Integer>();
        targetIOlistIDs.addAll(gpio_io.keySet());
        final ArrayAdapter<String> targetIOAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, targetIOlist);
        targetIOAdapter.setDropDownViewResource(R.layout.spinner_item);
        targetIO.setAdapter(targetIOAdapter);
        final Spinner targetS = view.findViewById(R.id.targetS);
        final List<String> targetSlist = new ArrayList<String>();
        targetSlist.addAll(sensors.values());
        final List<String> targetSlistIDs = new ArrayList<String>();
        targetSlistIDs.addAll(sensors.keySet());
        final ArrayAdapter<String> targetSAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, targetSlist);
        targetSAdapter.setDropDownViewResource(R.layout.spinner_item);
        targetS.setAdapter(targetSAdapter);
//        final Spinner condition = view.findViewById(R.id.condition);
//        final List<String> conditionList = new ArrayList<String>();
//        conditionList.add("euqal");
//        conditionList.add("less");
//        conditionList.add("bigger");
//        final ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, conditionList);
//        conditionAdapter.setDropDownViewResource(R.layout.spinner_item);
//        condition.setAdapter(conditionAdapter);
        final EditText operator = view.findViewById(R.id.operator);

        final Spinner valueIO = view.findViewById(R.id.valueio);
        final List<String> valueIOList = new ArrayList<String>();
        valueIOList.add("ANY");
        valueIOList.add("ON");
        valueIOList.add("OFF");
        final ArrayAdapter<String> valueIOAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, valueIOList);
        valueIOAdapter.setDropDownViewResource(R.layout.spinner_item);
        valueIO.setAdapter(valueIOAdapter);
        final EditText valueS = view.findViewById(R.id.valueS);
        final CheckBox precise = view.findViewById(R.id.precise);
        final EditText interval = view.findViewById(R.id.interval);
        final TextView soundURL = view.findViewById(R.id.sound);

        if(editMode){
            if(k.getInt(6)==0)activeCh.setChecked(false);
            else if(k.getInt(6)==2)precise.setChecked(true);
            typeS.setSelection(typeList.indexOf(k.getString(3)));
            if(k.getString(3).equals("i/o")){
                targetIO.setSelection(targetIOlistIDs.indexOf(k.getInt(2)));
                valueIO.setSelection(valueIOList.indexOf(k.getString(4)));
            }else if(k.getString(3).equals("sensor")){
                targetS.setSelection(targetSlistIDs.indexOf(k.getString(2)));
                //condition.setSelection(conditionList.indexOf(k.getString(5)));
                operator.setText(k.getString(5));
                valueS.setText(k.getString(4));
            }
            interval.setText(k.getString(7));
            if(!k.isNull(8))
                if(!k.getString(8).isEmpty())
                    soundURL.setText(k.getString(8));
        }
        MaterialDialog d = new MaterialDialog.Builder(mContext)
                //.title(R.string.title)
                .customView(view, true)
                .autoDismiss(false)
                .positiveText("SAVE")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DataBaseHelper myDbHelper = new DataBaseHelper(mContext);
                        boolean valid = true;
                        if(typeS.getSelectedItemPosition() == 0 && (targetIO.getCount() == 0 || interval.getText().toString().equals(""))){
                            Toast.makeText(mContext, "Choose target and interval!", Toast.LENGTH_SHORT).show();
                            valid = false;
                        }
                        else if(typeS.getSelectedItemPosition() == 1 && (valueS.getText().toString().isEmpty() || interval.getText().toString().isEmpty() || !operator.getText().toString().matches("^==|!=|<|>|<=|>=$"))) {
                            Toast.makeText(mContext, "Fill value, interval and correct operator!", Toast.LENGTH_SHORT).show();
                            valid = false;
                        }
                        else if (Integer.parseInt(interval.getText().toString()) < 60){
                            Toast.makeText(mContext, "Minimum interval is 60s!", Toast.LENGTH_SHORT).show();
                            valid = false;
                        }
                        int preciseV = 0;
                        if(activeCh.isChecked())preciseV=1;
                        if(precise.isChecked()&&activeCh.isChecked())preciseV=2;
                        if(valid) {
                            if (editMode) {
                                if (typeS.getSelectedItemPosition() == 0)
                                    myDbHelper.edytujPowiadomienie(k.getInt(0), id_U, targetIOlistIDs.get(targetIO.getSelectedItemPosition()).toString(), typeS.getSelectedItem().toString(), valueIO.getSelectedItem().toString(),operator.getText().toString(), preciseV, Long.parseLong(interval.getText().toString()), soundURL.getText().toString());
                                else if (typeS.getSelectedItemPosition() == 1)
                                    myDbHelper.edytujPowiadomienie(k.getInt(0), id_U, targetSlistIDs.get(targetS.getSelectedItemPosition()), typeS.getSelectedItem().toString(), valueS.getText().toString(), operator.getText().toString(), preciseV, Long.parseLong(interval.getText().toString()), soundURL.getText().toString());
                                Notification n = new Notification(k.getInt(0), mContext);
                                if (preciseV > 0)
                                    n.startAlarm();
                                else
                                    n.cancelAlarm();
                            } else {
                                int id_pow = -1;
                                if (typeS.getSelectedItemPosition() == 0)
                                    id_pow = myDbHelper.dodajPowiadomienie(id_U, targetIOlistIDs.get(targetIO.getSelectedItemPosition()).toString(), typeS.getSelectedItem().toString(), valueIO.getSelectedItem().toString(), operator.getText().toString(), preciseV, Long.parseLong(interval.getText().toString()), soundURL.getText().toString());
                                else if (typeS.getSelectedItemPosition() == 1)
                                    id_pow = myDbHelper.dodajPowiadomienie(id_U, targetSlistIDs.get(targetS.getSelectedItemPosition()), typeS.getSelectedItem().toString(), valueS.getText().toString(), operator.getText().toString(), preciseV, Long.parseLong(interval.getText().toString()), soundURL.getText().toString());
                                if (preciseV > 0 && id_pow != -1) {
                                    Notification n = new Notification(id_pow, mContext);
                                    n.startAlarm();
                                }
                            }
                            myDbHelper.close();
                            dialog.dismiss();
                            check_state();
                        }
                    }
                })
                .neutralText("DELETE")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DataBaseHelper myDbHelper = new DataBaseHelper(mContext);
                        //Notification n = new Notification(k.getInt(0), mContext);
                        if (k.getInt(6)> 0) {
                            Notification nD = new Notification(k.getInt(0), mContext);
                            nD.cancelAlarm();
                        }
                        myDbHelper.usunPowiadomienie(k.getInt(0));
                        myDbHelper.close();
                        dialog.dismiss();
                        check_state();
                    }
                })
                .negativeText("CANCEL")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

//        final AlertDialog d = new AlertDialog.Builder(getActivity().getWindow().getContext())
//                .setView(view)
//                .setCancelable(false)
//                .setPositiveButton("SAVE",
//                    new Dialog.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface d, int which) {
//                            boolean valid = true;
//                            if(typeS.getSelectedItemPosition() == 0 && (targetIO.getCount() == 0 || interval.getText().toString().equals(""))){
//                                Toast.makeText(mContext, "Choose target and interval!", Toast.LENGTH_SHORT).show();
//                                valid = false;
//                            }
//                            else if(typeS.getSelectedItemPosition() == 1 && (valueS.getText().toString().isEmpty() || interval.getText().toString().isEmpty() || operator.getText().toString().isEmpty()||!operator.getText().toString().matches("^==|!=|<|>|<=|>=$"))) {
//                                Toast.makeText(mContext, "Fill value, interval and correct operator!", Toast.LENGTH_SHORT).show();
//                                valid = false;
//                            }
//                            int preciseV = 0;
//                            if(activeCh.isChecked())preciseV=1;
//                            if(precise.isChecked()&&activeCh.isChecked())preciseV=2;
//                            if(valid) {
//                                if (editMode) {
//                                    if (typeS.getSelectedItemPosition() == 0)
//                                        myDbHelper.edytujPowiadomienie(k.getInt(0), id_U, targetIOlistIDs.get(targetIO.getSelectedItemPosition()).toString(), typeS.getSelectedItem().toString(), valueIO.getSelectedItem().toString(),operator.getText().toString(), preciseV, Long.parseLong(interval.getText().toString()), soundURL.getText().toString());
//                                    else if (typeS.getSelectedItemPosition() == 1)
//                                        myDbHelper.edytujPowiadomienie(k.getInt(0), id_U, targetSlistIDs.get(targetS.getSelectedItemPosition()), typeS.getSelectedItem().toString(), valueS.getText().toString(), operator.getText().toString(), preciseV, Long.parseLong(interval.getText().toString()), soundURL.getText().toString());
//                                    Notification n = new Notification(k.getInt(0), mContext);
//                                    if (preciseV > 0)
//                                        n.startAlarm();
//                                    else
//                                        n.cancelAlarm();
//                                } else {
//                                    int id_pow = -1;
//                                    if (typeS.getSelectedItemPosition() == 0)
//                                        id_pow = myDbHelper.dodajPowiadomienie(id_U, targetIOlistIDs.get(targetIO.getSelectedItemPosition()).toString(), typeS.getSelectedItem().toString(), valueIO.getSelectedItem().toString(), operator.getText().toString(), preciseV, Long.parseLong(interval.getText().toString()), soundURL.getText().toString());
//                                    else if (typeS.getSelectedItemPosition() == 1)
//                                        id_pow = myDbHelper.dodajPowiadomienie(id_U, targetSlistIDs.get(targetS.getSelectedItemPosition()), typeS.getSelectedItem().toString(), valueS.getText().toString(), operator.getText().toString(), preciseV, Long.parseLong(interval.getText().toString()), soundURL.getText().toString());
//                                    if (preciseV > 0 && id_pow != -1) {
//                                        Notification n = new Notification(id_pow, mContext);
//                                        n.startAlarm();
//                                    }
//                                }
//
//                                d.dismiss();
//                                check_state();
//                            }
//                        }
//                    })
//                .setNeutralButton("DELETE",
//                    new Dialog.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface d, int which) {
//                            myDbHelper.usunPowiadomienie(k.getInt(0));
//                            Notification n = new Notification(k.getInt(0), mContext);
//                            if (k.getInt(6)> 0) {
//                                Notification nD = new Notification(k.getInt(0), mContext);
//                                nD.cancelAlarm();
//                            }
//                            d.dismiss();
//                            check_state();
//                        }
//                    })
//                .setNegativeButton("CANCEL", new Dialog.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface d, int which) {
//                       d.cancel();
//                    }
//                }).create();
        soundURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //d.dismiss();
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                }
                else {

                    RingtonePickerDialog.Builder ringtonePickerBuilder = new RingtonePickerDialog
                            .Builder(mContext, getActivity().getSupportFragmentManager())
                            .setTitle("Select ringtone")
                            .displayDefaultRingtone(true)
                            .displaySilentRingtone(true)
                            .setPositiveButtonText("SET RINGTONE")
                            .setCancelButtonText("CANCEL")
                            .setPlaySampleWhileSelection(true)
                            .setListener(new RingtonePickerListener() {
                                @Override
                                public void OnRingtoneSelected(@NonNull String ringtoneName, Uri ringtoneUri) {
                                    soundURL.setText(ringtoneUri.getPath());
                                    //d.show();
                                }
                            });
                    if(editMode && !soundURL.getText().toString().equals("Not selected")){
                        Uri selectedSound = Uri.parse(soundURL.getText().toString());
                        ringtonePickerBuilder.setCurrentRingtoneUri(selectedSound);
                    }
                    //ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_MUSIC);
                    ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
                    ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE);
                    ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM);

                    ringtonePickerBuilder.show();
                }
            }
        });
        //d.show();

        if(!editMode)
            d.getActionButton(DialogAction.NEUTRAL).setVisibility(View.INVISIBLE);


    }

//    public void hendlerm2() {
//
//        if (handlerOn == false && menuCreated == true && arTime > 0) {
//            runabble = new Runnable() {
//                public void run() {
//                    if (menuCreated == true) {
//                        check_state();
//                        handler.postDelayed(this, arTime);
//                    }
//                }
//            };
//
//            handler.postDelayed(runabble, arTime);
//            handlerOn = true;
//        }
//    }

}
