package com.rgc;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdvScheduledActions extends Fragment {
    static String dstName;
    static int Page, arTime;
    int Records = -1;
    //ArrayList<String> sensor_units = new ArrayList();
    static Date edittime = new Date(1);
    static ProgressBar pb;
    static Button r;
    static int id_U;
    boolean menuCreated = false, VisibleToUser = true;
    static boolean handlerOn = false, running = false;
    static ListView listview;
    Handler handler = new Handler();
    Runnable runabble = null;
    public static Context mContext;
    static SparseArray<String> gpio_io = new SparseArray<>();
    static SparseArray<String> gpio_o = new SparseArray<>();
    static SparseArray<String> gpio_pwm = new SparseArray<>();
    static SparseArray<String> chains = new SparseArray<>();
    static Map <String,String> sensors = new HashMap<String,String>();
    static Map <String,String> sensorsUnit = new HashMap<String,String>();
    private static List<AdvScheduledAction> asaList = new ArrayList<>();
    static Connection c;
    public AdvScheduledActions() {
    }

    public static AdvScheduledActions newInstance(int page, String name, Connection conn, Context Context, int artime, int id_u) {
        AdvScheduledActions fragment = new AdvScheduledActions();
        Page = page;
        dstName = name;
        c = conn;
        arTime = artime;
        id_U = id_u;
        mContext = Context;
        return fragment;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.adv_scheduled_actions, container, false);
        listview = rootView.findViewById(R.id.listView2);
        setHasOptionsMenu(true);
        return rootView;
    }


    static public void check_state() {
        if (running == false) {
            AdvScheduledActionsTask edittimecheck = new AdvScheduledActionsTask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                }
            });
            edittimecheck.execute("GPIO_ASAEtime");

        }
    }

    static public void list_update() {
        AdvScheduledActionsTask GPIO_ListGet = new AdvScheduledActionsTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        GPIO_ListGet.execute("GPIO_ASAlist");
        AdvScheduledActionsTask GPIO_Onames = new AdvScheduledActionsTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        GPIO_Onames.execute("GPIO_Oname");
        AdvScheduledActionsTask GPIO_PWMnames = new AdvScheduledActionsTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        GPIO_PWMnames.execute("GPIO_PWMnames");
        AdvScheduledActionsTask GPIO_IOnames = new AdvScheduledActionsTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        GPIO_IOnames.execute("GPIO_OInames");
        AdvScheduledActionsTask taskSensorNames = new AdvScheduledActionsTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        taskSensorNames.execute("SENSOR_names");
        AdvScheduledActionsTask taskChainNames = new AdvScheduledActionsTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        taskChainNames.execute("Chain_names");


    }



    public static class AdvScheduledActionsTask extends AsyncTask<String, Void, Boolean> {

        public AsyncResponse delegate = null;
        DataBaseHelper myDbHelper;

        public AdvScheduledActionsTask(AsyncResponse delegate) {
            this.delegate = delegate;
            myDbHelper = new DataBaseHelper(mContext);
        }

        int records;
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
                if (params[0].equals("GPIO_ASAEtime"))
                    response = c.sendString( params[0], 256);
                else if (params[0].matches("GPIO_OInames|GPIO_Oname|GPIO_PWMnames|SENSOR_names|Chain_names"))
                    response = c.sendString(params[0] + ";", 8192);
                else if (params[0].equals("GPIO_ASAlist"))
                    response = c.sendString(params[0] + ";", 16384);
                else if (params[0].equals("GPIO_ASA_Add"))
                    response = c.sendString(params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + params[4] + ";" + params[5] + ";" + params[6] + ";" + params[7] + ";" + params[8] + ";" +AdvSAListAdapter.DatetoString(new Date()), 1024);
                else if (params[0].equals("GPIO_ASA_Update"))
                    response = c.sendString(params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + params[4] + ";" + params[5] + ";" + params[6] + ";" + params[7] + ";" + params[8] + ";" + params[9] + ";" + AdvSAListAdapter.DatetoString(new Date()), 1024);
                else if (params[0].equals("GPIO_ASA_Delete"))
                    response = c.sendString(params[0]+ ";" + params[1], 256);
                else if (params[0].matches("GPIO_ASA_DeleteTrigger|GPIO_ASA_SetConj"))
                    response = c.sendString(params[0]+ ";" + params[1] + ";" + params[2], 256);
                else if (params[0].equals("GPIO_ASA_AddTrigger"))
                    response = c.sendString(params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + params[4] + ";" + params[5] + ";" + AdvSAListAdapter.DatetoString(new Date()), 1024);
                else if (params[0].equals("GPIO_ASA_UpdateTrigger"))
                    response = c.sendString(params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + params[4] + ";" + params[5] + ";" + params[6] + ";" + AdvSAListAdapter.DatetoString(new Date()), 1024);

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
            if (result == true && passwd == true) {
                r.setTextColor(Color.GREEN);
                if (list.get(1).equals("GPIO_ASAEtime")) {
                    if (!(list.get(2)).equals("None")) {
                        Date date = AdvSAListAdapter.StringtoDate(list.get(2));
                        if (!date.equals(edittime)) {
                            list_update();
                            edittime = date;
                        }
                    } else {
                        list_update();
                    }
                }
                else if (list.get(1).equals("GPIO_ASAlist")){
                    listview.setAdapter(null);
                    asaList.clear();
                    if(list.size() > 2){
                        for (int i = 2; i < list.size()-1; i+=18) {
                            if(list.get(i+1).equals("output")) {
//                                int reverseNumber = Integer.parseInt(list.get(i + 12));
                                int stateNumber = Integer.parseInt(list.get(i + 5));
                                String stan = "";
//                                if (stateNumber == 0 && reverseNumber == 0 || stateNumber == 1 && reverseNumber == 1)
//                                    stan = "OFF";
//                                else if (stateNumber == 1 && reverseNumber == 0 || stateNumber == 0 && reverseNumber == 1)
//                                    stan = "ON";
//                                else if (stateNumber == 2)
//                                    stan = "OPPOSITE";
                                if (stateNumber == 0)
                                    stan = "OFF";
                                else if (stateNumber == 1)
                                    stan = "ON";
                                else if (stateNumber == 2)
                                    stan = "OPPOSITE";
                                asaList.add(new AdvScheduledAction(
                                        Integer.parseInt(list.get(i)),
                                        list.get(i + 1),
                                        list.get(i + 2),
                                        list.get(i + 3),
                                        list.get(i + 10),
                                        list.get(i + 17),
                                        Integer.parseInt(list.get(i + 4)),
                                        stan,
                                        list.get(i + 11),
                                        list.get(i + 15).equals("1")
                                ));
                            }
                            else if(list.get(i+1).equals("pwm")){
                                String stan = "";
                                if (list.get(i + 9).equals("1"))stan = "ON";
                                else  if (list.get(i + 9).equals("0"))stan = "OFF";
                                else stan = "OPPOSITE";
                                asaList.add(new AdvScheduledAction(
                                        Integer.parseInt(list.get(i)),
                                        list.get(i + 1),
                                        list.get(i + 2),
                                        list.get(i + 3),
                                        list.get(i + 10),
                                        list.get(i + 17),
                                        Integer.parseInt(list.get(i + 6)),
                                        stan,
                                        list.get(i + 7),
                                        list.get(i + 8),
                                        list.get(i + 13),//11?
                                        list.get(i + 15).equals("1")
                                ));
                            }
                            else if(list.get(i+1).equals("chain")){
                                asaList.add(new AdvScheduledAction(
                                        Integer.parseInt(list.get(i)),
                                        list.get(i + 1),
                                        list.get(i + 2),
                                        list.get(i + 3),
                                        list.get(i + 10),
                                        list.get(i + 17),
                                        Integer.parseInt(list.get(i + 14)),
                                        list.get(i + 16),
                                        list.get(i + 15).equals("1")
                                ));
                            }
                        }
                        listview.setAdapter(new AdvSAListAdapter(asaList,mContext));
                    }
                }
                else if (list.get(1).matches("GPIO_ASA_Add|GPIO_ASA_Update|GPIO_ASA_Delete|GPIO_ASA_DeleteTrigger|GPIO_ASA_UpdateTrigger|GPIO_ASA_AddTrigger|GPIO_ASA_SetConj")) {
                    edittime = new Date(1);
                    check_state();
                }else if (list.get(1).equals("GPIO_Oname")) {
                    gpio_o.clear();
                    for (int i = 2; i < list.size()-1; i+=4) {
                        gpio_o.put(Integer.parseInt(list.get(i)),list.get(i+1));
                    }
                }else if (list.get(1).equals("GPIO_PWMnames")) {
                    gpio_pwm.clear();
                    for (int i = 2; i < list.size()-1; i+=2) {
                        gpio_pwm.put(Integer.parseInt(list.get(i)),list.get(i+1));
                    }
                }else if (list.get(1).equals("GPIO_OInames")) {
                    gpio_io.clear();
                    for (int i = 2; i < list.size()-1; i+=2) {
                        gpio_io.put(Integer.parseInt(list.get(i)),list.get(i+1));
                    }
                }else if (list.get(1).equals("SENSOR_names")){
                    sensors.clear();sensorsUnit.clear();
                    for (int i = 2; i < list.size()-1; i+=3) {
                        sensors.put(list.get(i),list.get(i+1));
                        sensorsUnit.put(list.get(i),list.get(i+2));
                    }
                }else if (list.get(1).equals("Chain_names")) {
                    chains.clear();
                    for (int i = 2; i < list.size()-1; i+=2) {
                        chains.put(Integer.parseInt(list.get(i)),list.get(i+1));
                    }
                }
            } else if (result == false || passwd == false) {
                if (result == false) {
                    myDbHelper.dodajLog(id_U, response);
                    r.setTextColor(Color.RED);
                } else if (passwd == false) {
                    myDbHelper.dodajLog(id_U, list.toString());
                    r.setTextColor(Color.RED);
                }
            }
            myDbHelper.close();
            pb.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        //myDbHelper.edytujUrzadzenie(id_U,(Page-1));
        MenuItem actionViewItem = menu.findItem(R.id.miActionButton);
        View v = MenuItemCompat.getActionView(actionViewItem);
        pb = (ProgressBar) v.findViewById(R.id.pbProgressAction);
        r = (Button) v.findViewById(R.id.btnCustomAction3);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.cancel();
                edittime = new Date(1);
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
        Button b = (Button) v.findViewById(R.id.btnCustomAction);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdvScheduledAction newAction = new AdvScheduledAction();
                newAction.actionDialog(false,mContext,gpio_o,gpio_pwm,chains);
            }
        });
        pb.setVisibility(View.INVISIBLE);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        menuCreated = true;
        edittime = new Date(1);
        running = false;
        c.cancel();
        check_state();
        hendlerm2();
        super.onCreateOptionsMenu(menu, inflater);
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


    public void hendlerm2() {

        if (handlerOn == false && menuCreated == true && arTime > 0) {
            runabble = new Runnable() {
                public void run() {
                    if (menuCreated == true) {
                        check_state();
                        handler.postDelayed(this, arTime);
                    }
                }
            };

            handler.postDelayed(runabble, arTime);
            handlerOn = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }


}