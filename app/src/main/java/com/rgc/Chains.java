package com.rgc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Chains extends Fragment {
    static String dstName;
    static int Page, arTime;
    int Records = -1;
    static Date edittime = new Date(1);
    static ProgressBar pb;
    static Button r;
    //static DataBaseHelper myDbHelper;
    static int id_U;
    boolean menuCreated = false, VisibleToUser = true;
    static boolean handlerOn = false, running = false;
    static ListView listview;
    Handler handler = new Handler();
    Runnable runabble = null;
    public static Context mContext;
    static SparseArray<String> gpio_o = new SparseArray<>();
    static SparseArray<String> gpio_pwm = new SparseArray<>();
    static SparseArray<String> actions = new SparseArray<>();
    private static List<Chain> chainList = new ArrayList<>();
    static Connection c;
    public Chains() {
    }

    public static Chains newInstance(int page, String name, Connection conn, Context Context, int artime, int id_u) {
        Chains fragment = new Chains();
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
        //myDbHelper = new DataBaseHelper(mContext);
        listview = rootView.findViewById(R.id.listView2);
        setHasOptionsMenu(true);
        return rootView;
    }


    static public void check_state() {
        if (running == false) {
            ChainsTask edittimecheck = new ChainsTask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                }
            });
            edittimecheck.execute("GPIO_ChainEtime");

        }
    }

    static public void list_update() {
        ChainsTask GPIO_ListGet = new ChainsTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        GPIO_ListGet.execute("GPIO_ChainList");
        ChainsTask GPIO_Onames = new ChainsTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        GPIO_Onames.execute("GPIO_Oname");
        ChainsTask GPIO_PWMnames = new ChainsTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        GPIO_PWMnames.execute("GPIO_PWMnames");
        ChainsTask Actionnames = new ChainsTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        Actionnames.execute("GPIO_ActionsNames");

    }


    public static class ChainsTask extends AsyncTask<String, Void, Boolean> {

        public AsyncResponse delegate = null;
        DataBaseHelper myDbHelper;
        public ChainsTask(AsyncResponse delegate) {
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
                if (params[0].equals("GPIO_ChainEtime"))
                    response = c.sendString( params[0], 256);
                else if (params[0].matches("GPIO_OInames|GPIO_Oname|GPIO_PWMnames|SENSOR_names|GPIO_ActionsNames"))
                    response = c.sendString(params[0] + ";", 8192);
                else if (params[0].equals("GPIO_ChainList"))
                    response = c.sendString(params[0] + ";", 16384);
                else if (params[0].matches("GPIO_ChainCancel|GPIO_ChainExecute|GPIO_ChainAdd|GPIO_ChainDelete"))
                    response = c.sendString( params[0]+";"+params[1]+";" + android.os.Build.MODEL, 256);
                else if (params[0].matches("GPIO_ChainUpdate|GPIO_ChainBondsOrder|GPIO_ChainBondDelete"))
                    response = c.sendString( params[0]+";"+params[1]+";"+params[2], 256);
                else if (params[0].matches("GPIO_ChainBondUpdate"))
                    response = c.sendString( params[0]+";"+params[1]+";"+params[2]+";"+params[3]+";"+params[4]+";"+params[5]+";"+params[6]+";"+params[7]+";"+params[8], 256);
                else if (params[0].matches("GPIO_ChainBondAdd"))
                    response = c.sendString( params[0]+";"+params[1]+";"+params[2]+";"+params[3]+";"+params[4]+";"+params[5]+";"+params[6]+";"+params[7], 256);

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
                if (list.get(1).equals("GPIO_ChainEtime")) {
                    if (!(list.get(2)).equals("None")) {
                        Date date =AdvSAListAdapter.StringtoDate(list.get(2));
                        if (!date.equals(edittime)) {
                            list_update();
                            edittime = date;
                        }
                    } else {
                        list_update();
                    }
                }
                else if (list.get(1).equals("GPIO_ChainList")){
                    listview.setAdapter(null);
                    chainList.clear();
                    if(list.size() > 2){
                        for (int i = 2; i < list.size()-1; i+=5) {
                            chainList.add(new Chain(
                                    Integer.parseInt(list.get(i)),
                                    Integer.parseInt(list.get(i+1)),
                                    list.get(i+1).equals("0")?"Ready":"In progress at Lp. "+list.get(i+1),
                                    list.get(i+2),
                                    list.get(i+4)
                            ));
                        }
                        listview.setAdapter(new ChainsListAdapter(chainList,mContext));
                    }
                }else if (list.get(1).matches("GPIO_ChainCancel|GPIO_ChainExecute|GPIO_ChainUpdate|GPIO_ChainAdd|GPIO_ChainDelete|GPIO_ChainBondUpdate|GPIO_ChainBondAdd|GPIO_ChainBondsOrder|GPIO_ChainBondDelete"))
                    list_update();
                else if (list.get(1).equals("GPIO_Oname")) {
                    gpio_o.clear();
                    for (int i = 2; i < list.size()-1; i+=4) {
                        gpio_o.put(Integer.parseInt(list.get(i)),list.get(i+1));
                    }
                }else if (list.get(1).equals("GPIO_PWMnames")) {
                    gpio_pwm.clear();
                    for (int i = 2; i < list.size()-1; i+=2) {
                        gpio_pwm.put(Integer.parseInt(list.get(i)),list.get(i+1));
                    }
                }else if (list.get(1).equals("GPIO_ActionsNames")) {
                    actions.clear();
                    for (int i = 2; i < list.size()-1; i+=2) {
                        actions.put(Integer.parseInt(list.get(i)),list.get(i+1));
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
                showLogs(mContext);
                return false;
            }
        });
        Button b = (Button) v.findViewById(R.id.btnCustomAction);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chain chain = new Chain();
                chain.chainDialog(false,mContext);
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
    public static void showLogs(final Context context){
        final DataBaseHelper myDbHelper = new DataBaseHelper(context);
        LayoutInflater factory = LayoutInflater.from(context);
        final View loginView = factory.inflate(R.layout.errorlog_view, null);
        final ListView listView = (ListView) loginView.findViewById(R.id.errorlist);
        myDbHelper.czyscLogi(id_U);
        final Cursor k = myDbHelper.dajLogi(id_U);
        if (k.moveToNext()) {
            k.moveToPrevious();
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    String[] columns = new String[]{"timestamp", "data"};
                    int[] to = new int[]{R.id.time, R.id.data};
                    ListAdapter customAdapter = new SimpleCursorAdapter(context, R.layout.list3, k, columns, to,0);
                    listView.setAdapter(customAdapter);
                }
            });
        }
        new MaterialDialog.Builder(context)
                .customView(loginView, false)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        k.close();
                        myDbHelper.close();
                    }
                })
                .show();
//        final AlertDialog d = new AlertDialog.Builder(context)
//                .setView(loginView)
//                .setNegativeButton("OK", null)
//                .create();
//        d.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }


}