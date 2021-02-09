package com.rgc;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;


public class Sensors extends Fragment {
    static String dstName;
    static int Page, arTime;
    ProgressBar pb;
    boolean menuCreated = false, VisibleToUser = true, running;
    boolean handlerOn = false;
    Button r;
    static int id_U;
    ListView listview,listviewH;
    private SimpleAdapter adapter;
    Handler handler = new Handler();
    Runnable runabble = null;
    public static Context mContext;
    LayoutInflater inflater;
    static Connection c;
    List<HashMap<String, String>> fillMaps;
    public Sensors() {
    }

    public static Sensors newInstance(int page, String name, Connection conn, Context Context, int artime,int id_u) {
        Sensors fragment = new Sensors();
        Page = page;
        dstName = name;
        arTime = artime;
        c = conn;
        mContext = Context;
        id_U = id_u;
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduled_actions, container, false);

        listview = (ListView) rootView.findViewById(R.id.listView2);
        setHasOptionsMenu(true);
        this.inflater = inflater;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,final int position, long id) {
                //final View loginView = inflater.inflate(R.layout.sensor_history, null);
                final TextView dId =  view.findViewById(R.id.id);
                final TextView dName =  view.findViewById(R.id.name);
                final TextView dType =  view.findViewById(R.id.type);
                final SensorsTask myClientTask2_1 = new SensorsTask();
                final View loginView = inflater.inflate(R.layout.edit_sensor, null);
                TextView dhr =  view.findViewById(R.id.h_refresh);
                TextView dhk =  view.findViewById(R.id.h_keep);
                final EditText edName = loginView.findViewById(R.id.name);
                final EditText edhr = loginView.findViewById(R.id.hr);
                final EditText edhk = loginView.findViewById(R.id.hk);
                edName.setText(dName.getText().toString());
                edhr.setText(dhr.getText().toString());
                edhk.setText(dhk.getText().toString());
                //myClientTask2_1.execute("SENSOR_history",dId.getText().toString(),dType.getText().toString(),dName.getText().toString());

                final PopupMenu popup = new PopupMenu(mContext, view);
                popup.getMenuInflater().inflate(R.menu.sensor_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(
                        new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                //item.getActionView().startAnimation(alpha);
                                switch (item.getItemId()) {
                                    case R.id.refresh:
                                        myClientTask2_1.execute("SENSOR_refresh",dId.getText().toString());
                                        break;
                                    case R.id.showHistory:
                                        myClientTask2_1.execute("SENSOR_history",dId.getText().toString(),dType.getText().toString(),dName.getText().toString());
                                        break;
                                    case R.id.edit:
                                        modalDialog(true,position);
//                                        new AlertDialog.Builder(getActivity())
//                                                .setView(loginView)
//                                                .setPositiveButton("SAVE",
//                                                        new Dialog.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface d, int which) {
//                                                                myClientTask2_1.execute("SENSOR_update",dId.getText().toString(),edName.getText().toString(),edhr.getText().toString(),edhk.getText().toString());
//                                                            }
//                                                        })
//                                                .setNeutralButton("REMOVE",
//                                                        new Dialog.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface d, int which) {
//                                                                myClientTask2_1.execute("SENSOR_remove",dId.getText().toString());
//                                                            }
//                                                        })
//                                                .setNegativeButton("CANCEL", null)
//                                                .create()
//                                                .show();
                                        break;

                                }
                                return true;
                            }
                        });
                popup.show();

            }
        });
//        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                final View loginView = inflater.inflate(R.layout.edit_sensor, null);
//                final TextView dId =  view.findViewById(R.id.id);
//                TextView dName =  view.findViewById(R.id.name);
//                TextView dhr =  view.findViewById(R.id.h_refresh);
//                TextView dhk =  view.findViewById(R.id.h_keep);
//                final EditText edName = loginView.findViewById(R.id.name);
//                final EditText edhr = loginView.findViewById(R.id.hr);
//                final EditText edhk = loginView.findViewById(R.id.hk);
//                edName.setText(dName.getText().toString());
//                edhr.setText(dhr.getText().toString());
//                edhk.setText(dhk.getText().toString());
//                final AlertDialog d = new AlertDialog.Builder(getActivity())
//                        .setView(loginView)
//                        .setPositiveButton("SAVE",
//                                new Dialog.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface d, int which) {
//                                        SensorsTask myClientTask2_1 = new SensorsTask();
//                                        myClientTask2_1.execute("SENSOR_update",dId.getText().toString(),edName.getText().toString(),edhr.getText().toString(),edhk.getText().toString());
//                                    }
//                                })
//                        .setNeutralButton("REMOVE",
//                                new Dialog.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface d, int which) {
//                                        SensorsTask myClientTask2_1 = new SensorsTask();
//                                        myClientTask2_1.execute("SENSOR_remove",dId.getText().toString());
//                                    }
//                                })
//                        .setNegativeButton("CANCEL", null)
//
//                        .create();
//
//                d.show();
//                return true;
//            }
//        });
        return rootView;
    }

    public void check_state() {
        if (running == false) {
            SensorsTask myClientTask2_1 = new SensorsTask();
            myClientTask2_1.execute("SENSOR_list");

        }
    }

    public class SensorsTask extends AsyncTask<String, Void, Boolean> {

        String response = "";
        boolean succes;
        boolean passwd;
        List<String> list;
        String sensorName = "",sensorType="";


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
                if (params[0].equals("SENSOR_list"))
                    response = c.sendString( params[0], 16384);
                else if (params[0].matches("SENSOR_update|SENSOR_addCustom|SENSOR_updateCustom"))
                    response = c.sendString(TextUtils.join(";", params) + ";", 1024);
                else if (params[0].matches("SENSOR_remove|SENSOR_refresh"))
                    response = c.sendString(params[0] + ";" + params[1], 256);
                else if (params[0].equals("SENSOR_history")) {
                    response = c.sendStringTCP(params[0] + ";" + params[1] + ";" + params[2],true);
                    sensorName = params[3];
                    sensorType = params[2];
                }

                list = new ArrayList<String>(Arrays.asList(response.split(";")));
                if (list.get(0).equals("true")) passwd = true;
                else if (list.get(0).equals("false")) passwd = false;
                succes = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
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
                if (list.get(1).equals("SENSOR_list")) {
                    if(list.size() > 2) {
                        String[] from = new String[]{"id", "name", "type", "value", "h_refresh", "h_keep","refresh_date"};
                        int[] to = new int[]{R.id.id, R.id.name, R.id.type, R.id.value, R.id.h_refresh, R.id.h_keep, R.id.refresh_date};

                        fillMaps = new ArrayList<HashMap<String, String>>();
                        for (int i = 2; i < list.size()-1; i+=11) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("id", list.get(i));
                            map.put("name", list.get(i+1));
                            map.put("type", list.get(i+2));
                            map.put("value",list.get(i+3)+list.get(i+6));
                            map.put("h_refresh", list.get(i+4));
                            map.put("h_keep", list.get(i+5));
                            map.put("refresh_date",AdvSAListAdapter.UTCtoLocalDate(list.get(i+7),"yyyy-MM-dd HH:mm:ss",false));
                            map.put("cmdId", list.get(i+8));
                            map.put("gpio", list.get(i+9));
                            map.put("dataName", list.get(i+10));
                            map.put("unit", list.get(i+6));
                            fillMaps.add(map);
                        }
                        adapter = new SimpleAdapter(mContext, fillMaps, R.layout.sensor_elem, from, to);
                        listview.setAdapter(adapter);
                    }
                } else if (list.get(1).matches("SENSOR_update|SENSOR_addCustom|SENSOR_updateCustom|SENSOR_remove|SENSOR_refresh")) {
                    check_state();
                } else if (list.get(1).equals("SENSOR_history")) {
                    final View loginView = inflater.inflate(R.layout.sensor_history, null);

                    //final TableLayout tb = loginView.findViewById(R.id.tableHistory);
                    TextView tvh = loginView.findViewById(R.id.value);
                    TextView tvt = loginView.findViewById(R.id.titleL);
                    tvt.setText(sensorName + " sensor history");
                    tvh.setText("Value in "+list.get(2));
//                    for (int i = 3; i < list.size()-1; i+=2) {
//                        TableRow tr = new TableRow(mContext);
//                        tr.setGravity(Gravity.CENTER);
//                        TextView timestamp = new TextView(mContext);
//                        timestamp.setText(AdvSAListAdapter.UTCtoLocalDate(list.get(i),"yyyy-MM-dd HH:mm:ss",false)+" ");
//                        tr.addView(timestamp);
//                        TextView value = new TextView(mContext);
//                        value.setText(list.get(i+1));
//                        tr.addView(value);
//                        tb.addView(tr);
//                    }
                    listviewH = loginView.findViewById(R.id.listHistory);
                    if(list.size() > 2) {
                        String[] from = new String[]{"date", "value"};
                        int[] to = new int[]{R.id.date, R.id.value};
                        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                        for (int i = 3; i < list.size()-1; i+=2) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("date", AdvSAListAdapter.UTCtoLocalDate(list.get(i),"yyyy-MM-dd HH:mm:ss",false)+" ");
                            map.put("value", list.get(i+1));
                            fillMaps.add(map);
                        }
                        SimpleAdapter sa = new SimpleAdapter(mContext, fillMaps, R.layout.sensor_history_elem, from, to);
                        listviewH.setAdapter(sa);
                    }
//                    listviewH.setOnScrollListener(new AbsListView.OnScrollListener(){
//                        @Override
//                        public void onScrollStateChanged(AbsListView view, int scrollState) {
//                            // Ignore this method
//                        }
//                        @Override
//                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, final int totalItemCount) {
//                            final int lastIndexInScreen = visibleItemCount + firstVisibleItem;
//                            if (lastIndexInScreen>= totalItemCount && lastIndexInScreen > 0 && !running) {
//
//                            }
//
//                        }
//
//                    });

                    new AlertDialog.Builder(getActivity())
                            .setView(loginView)
                            .show();

                    //d.show();
                }
            } else if (!result || !passwd) {
                if (!result) {
                    myDbHelper.dodajLog(id_U, response);
                    r.setTextColor(Color.RED);
                } else if (!passwd) {
                    myDbHelper.dodajLog(id_U, response);
                    r.setTextColor(Color.RED);
                }
            }
            pb.setVisibility(View.INVISIBLE);
            myDbHelper.close();
            super.onPostExecute(result);
        }

    }
    HashMap<String, String> selectedSensor;String selectedSensorType = "custom";
    public void modalDialog(final boolean editMode,final int position){
        final View loginView = inflater.inflate(R.layout.edit_sensor, null);
        final EditText edName = loginView.findViewById(R.id.name);
        final EditText edhr = loginView.findViewById(R.id.hr);
        final EditText edhk = loginView.findViewById(R.id.hk);
        final EditText unit = loginView.findViewById(R.id.unit);
        final EditText gpio = loginView.findViewById(R.id.gpio);
        final EditText dataName = loginView.findViewById(R.id.dataName);
        final Spinner dataSource = loginView.findViewById(R.id.sourceCmd);
        final SparseArray CmdsList= new SparseArray<>();
        if(editMode){
            selectedSensor = fillMaps.get(position);
            selectedSensorType = selectedSensor.get("type");
            edName.setText(selectedSensor.get("name"));
            edhr.setText(selectedSensor.get("h_refresh"));
            edhk.setText(selectedSensor.get("h_keep"));
            unit.setText(selectedSensor.get("unit"));
            gpio.setText(selectedSensor.get("gpio"));
            dataName.setText(selectedSensor.get("dataName"));
        }
        else{
            edhr.setText("3600");
            edhk.setText("7");
        }
        if(!selectedSensorType.equals("custom")){
            TableRow unitRow = loginView.findViewById(R.id.tableRow4);
            TableRow gpioRow = loginView.findViewById(R.id.tableRow5);
            TableRow dataNameRow = loginView.findViewById(R.id.tableRow6);
            TableRow dataSourceRow = loginView.findViewById(R.id.sourceCmdRow);
            unitRow.setVisibility(View.GONE);
            gpioRow.setVisibility(View.GONE);
            dataNameRow.setVisibility(View.GONE);
            dataSourceRow.setVisibility(View.GONE);
        }
        GetAsyncData execad = new GetAsyncData(new GetAsyncData.AsyncResponse() {
            @Override
            public void processFinish(List<String> list) {
                CmdsList.clear();
                for (int i = 2; i < list.size()-1; i+=4) {
                    CmdsList.put(Integer.parseInt(list.get(i)),list.get(i+1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, AdvScheduledAction.ConvertToList(CmdsList));
                adapter.setDropDownViewResource(R.layout.spinner_item);
                dataSource.setAdapter(adapter);
                if(editMode){
                    if(selectedSensorType.equals("custom")){
                        int cmdId =Integer.parseInt(selectedSensor.get("cmdId"));
                        dataSource.setSelection(CmdsList.indexOfKey(cmdId));
                    }
                }
            }
            @Override
            public void processFail(String error) {

            }
        },mContext,c,id_U,1024,pb,r);
        execad.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"GetCustomCmds");

        final SensorsTask myClientTask2_1 = new SensorsTask();
        MaterialDialog d = new MaterialDialog.Builder(mContext)
                //.title(R.string.title)
                .customView(loginView, true)
                .autoDismiss(false)
                .positiveText("SAVE")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(edName.getText().toString().isEmpty()) Toast.makeText(mContext, "Name required !", Toast.LENGTH_SHORT).show();
                        else if(edhr.getText().toString().isEmpty()) Toast.makeText(mContext, "History refresh value is required !", Toast.LENGTH_SHORT).show();
                        else if(edhk.getText().toString().isEmpty()) Toast.makeText(mContext, "History range back value is required !", Toast.LENGTH_SHORT).show();
                        else if(dataSource.getSelectedItem() == null && selectedSensorType.equals("custom"))Toast.makeText(mContext, "Source for custom sensor is required  !", Toast.LENGTH_SHORT).show();
                        else{
                            if(editMode && selectedSensorType.equals("custom"))myClientTask2_1.execute("SENSOR_updateCustom",selectedSensor.get("id"),edName.getText().toString(),edhr.getText().toString(),edhk.getText().toString(),unit.getText().toString(),gpio.getText().toString(),dataName.getText().toString(),String.valueOf(CmdsList.keyAt(dataSource.getSelectedItemPosition())));
                            else if (editMode)myClientTask2_1.execute("SENSOR_update",selectedSensor.get("id"),edName.getText().toString(),edhr.getText().toString(),edhk.getText().toString());
                            else myClientTask2_1.execute("SENSOR_addCustom",edName.getText().toString(),edhr.getText().toString(),edhk.getText().toString(),unit.getText().toString(),gpio.getText().toString(),dataName.getText().toString(),String.valueOf(CmdsList.keyAt(dataSource.getSelectedItemPosition())));
                            dialog.dismiss();
                        }
                    }
                })
                .neutralText("DELETE")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        myClientTask2_1.execute("SENSOR_remove",selectedSensor.get("id"));
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

        if(!editMode)
            d.getActionButton(DialogAction.NEUTRAL).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //myDbHelper.edytujUrzadzenie(id_U,(Page-1));
        MenuItem actionViewItem = menu.findItem(R.id.miActionButton);
        View v = MenuItemCompat.getActionView(actionViewItem);
        pb = (ProgressBar) v.findViewById(R.id.pbProgressAction);
        Button b = (Button) v.findViewById(R.id.btnCustomAction);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSensorType = "custom";
                modalDialog(false,-1);
            }
        });
        //Button b2 = (Button) v.findViewById(R.id.btnCustomAction2);
        //b.setVisibility(View.GONE);
        //b2.setVisibility(View.VISIBLE);
        r = (Button) v.findViewById(R.id.btnCustomAction3);
        r.setOnClickListener( new View.OnClickListener() {
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
        hendlerm2();
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
            hendlerm2();
            handlerOn = true;
        }
        super.onResume();
    }

    public void hendlerm2() {

        if (handlerOn == false && menuCreated == true && arTime > 0) {
            runabble = new Runnable() {
                public void run() {
                    if (menuCreated == true) {
                        check_state();
                        handler.postDelayed(this, arTime);
                    } //now is every 11 sec (10 sec timeout)
                }
            };

            handler.postDelayed(runabble, arTime);
            handlerOn = true;
        }
    }
}
