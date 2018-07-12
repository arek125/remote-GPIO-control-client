package com.rgc;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.TextView;


public class GPIO_Input extends Fragment {
    static String dstName, dstAddress, dstPassword, encKey;
    static int Page, dstPort, arTime;
    ProgressBar pb;
    boolean menuCreated = false;
    Handler handler = new Handler();
    boolean handlerOn = false, running = false, VisibleToUser = true;
    Runnable runabble = null;
    GridView gridView;
    Button r;
    static int id_U;
    public static Context mContext;
    private ArrayList<String> idki = new ArrayList<String>();
    private ArrayList<String> gpios = new ArrayList<String>();
    private ArrayList<String> stany = new ArrayList<String>();
    private ArrayList<String> nazwy = new ArrayList<String>();
    private ArrayList<String> reverses = new ArrayList<String>();
    private ArrayList<String> bindidG = new ArrayList<String>();
    private ArrayList<String> bindtypeG = new ArrayList<String>();
    private ArrayList<String> idki_out = new ArrayList<String>();
    private ArrayList<String> nazwy_out = new ArrayList<String>();
    private ArrayList<String> gpios_out = new ArrayList<String>();
    private ArrayList<String> gpios_pwm = new ArrayList<String>();
    Date edittime = new Date(1);
    static Connection c;

    public GPIO_Input() {
    }

    public static GPIO_Input newInstance(int page, String name, Connection conn, Context Context, int artime, int id_u) {
        GPIO_Input fragment = new GPIO_Input();
        Page = page;
        dstName = name;
        c = conn;
        mContext = Context;
        arTime = artime;
        id_U = id_u;
        return fragment;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gpio_in, container, false);
        setHasOptionsMenu(true);
        gridView = (GridView) rootView.findViewById(R.id.gridView1);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, final View v,
                                    final int position, long id) {
                LayoutInflater factory = LayoutInflater.from(mContext);
                final View loginView = factory.inflate(R.layout.addtypein, null);
                TextView title = (TextView) loginView.findViewById(R.id.titleL);
                title.setText("Edit GPIO Input:");
                final Spinner bindtype = (Spinner) loginView.findViewById(R.id.bindtype);
                final Spinner bindid = (Spinner) loginView.findViewById(R.id.bindid);
                String[] items = new String[]{"None (Read only)", "Output on/off switch", "Output pushbutton"};
                ArrayAdapter<String> adapterbt = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, items);
                bindtype.setAdapter(adapterbt);
                bindtype.setSelection(Integer.parseInt(bindtypeG.get(position)));
                final TableRow tr1 = (TableRow) loginView.findViewById(R.id.tableRow5);
                bindtype.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        if (position == 0) tr1.setVisibility(View.INVISIBLE);
                        else tr1.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }
                });

                GPIO_InputTask GPIO_NameGet = new GPIO_InputTask(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if (output.equals("1")) {

                            String[] items = nazwy_out.toArray(new String[0]);
                            ArrayAdapter<String> adapterbi = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, items);
                            bindid.setAdapter(adapterbi);
                            int temp = idki_out.indexOf(bindidG.get(position));
                            if (temp > 0) bindid.setSelection(temp);
                        }
                    }
                });
                GPIO_NameGet.execute("GPIO_Oname");
                GPIO_InputTask Allpins_GPIO_pwm = new GPIO_InputTask(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                    }
                });
                Allpins_GPIO_pwm.execute("Allpins_GPIO_pwm");

                final String idt = idki.get(position);
                final String nazwaL = nazwy.get(position);
                final EditText name = (EditText) loginView.findViewById(R.id.name);
                name.setText(nazwaL);
                final String gpioL = gpios.get(position);
                final EditText gpio = (EditText) loginView.findViewById(R.id.gpio);
                gpio.setText(gpioL);
                final String reverseL = reverses.get(position);
                final CheckBox reverse = (CheckBox) loginView.findViewById(R.id.reverse);
                if (reverseL.equals("1")) reverse.setChecked(true);


                final AlertDialog d = new AlertDialog.Builder(mContext)
                        .setView(loginView)
                        .setPositiveButton("SAVE",
                                new Dialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface d, int which) {
                                        //Do nothing here. We override the onclick
                                    }
                                })
                        .setNeutralButton("DELETE",
                                new Dialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface d, int which) {
                                        GPIO_InputTask Delete_Type = new GPIO_InputTask(new AsyncResponse() {
                                            @Override
                                            public void processFinish(String output) {

                                            }
                                        });
                                        Delete_Type.execute("Delete_GPIO_out", idt, gpios.get(position), nazwy.get(position));
                                    }
                                })
                        .setNegativeButton("CANCEL", null)
                        .create();

                d.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                List<String> templist = new ArrayList(Arrays.asList(gpio.getText().toString().split(",")));
                                List<String> templist2 = new ArrayList(Arrays.asList(gpios.get(position).split(",")));
                                boolean cont = false;
                                boolean notbcm = false;
                                for (String temp : templist) {
                                    if (temp.matches("2|3|4|17|27|22|10|9|11|5|6|13|19|26|14|15|18|23|24|25|8|7|12|16|20|21")) {
                                        if (!templist2.contains(temp) && (gpios_pwm.contains(temp) || gpios_out.contains(temp) || gpios.contains(temp))) {
                                            cont = true;
                                            break;
                                        }
                                    } else {
                                        notbcm = true;
                                        break;
                                    }
                                }
                                String reverseS = "0";
                                if (reverse.isChecked()) reverseS = "1";
                                int bindtypeI = bindtype.getSelectedItemPosition();
                                String bindtypeS = String.valueOf(bindtypeI);
                                String bindidS = "0";
                                if (bindtypeI > 0 && idki_out.size() > 0)
                                    bindidS = idki_out.get(bindid.getSelectedItemPosition());
                                if (name.getText().toString().matches("") || gpio.getText().toString().matches(""))
                                    Toast.makeText(mContext, "Fill in Name and GPIO!", Toast.LENGTH_SHORT).show();
                                else if (cont)
                                    Toast.makeText(mContext, "Pin/s alredy in use!", Toast.LENGTH_SHORT).show();
                                else if (notbcm)
                                    Toast.makeText(mContext, "Not GPIO BCM!", Toast.LENGTH_SHORT).show();
                                else {
                                    GPIO_InputTask Edit_Type = new GPIO_InputTask(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {

                                        }
                                    });
                                    Edit_Type.execute("Edit_GPIO_in", idt, gpio.getText().toString(), name.getText().toString(), DatetoString(new Date()), reverseS, bindidS, bindtypeS, gpioL);

                                    d.dismiss();
                                }
                            }
                        });
                    }
                });
                d.show();


            }
        });
        return rootView;
    }


    public void check_state() {
        if (running == false) {
            GPIO_InputTask edittimecheck = new GPIO_InputTask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                }
            });
            edittimecheck.execute("GPIO_IEtime");

        }
    }

    public void list_update() {
        GPIO_InputTask GPIO_ListGet = new GPIO_InputTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {

            }
        });
        GPIO_ListGet.execute("GPIO_Ilist");
    }

    public Date StringtoDate(String dtStart) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = null;
        try {
            date = format.parse(dtStart);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public String DatetoString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String stringtime = null;
        try {
            stringtime = format.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return stringtime;
    }

    public class GPIO_InputTask extends AsyncTask<String, String, Boolean> {


        public AsyncResponse delegate = null;
        DataBaseHelper myDbHelper;

        public GPIO_InputTask(AsyncResponse delegate) {
            this.delegate = delegate;
            myDbHelper = new DataBaseHelper(mContext);
        }

        String response = "";
        boolean succes;
        boolean passwd;
        List<String> list;


        @Override
        protected void onPreExecute() {

            pb.setVisibility(View.VISIBLE);
            //handler.removeCallbacks(runabble);
            // handler.removeCallbacksAndMessages(null);
            running = true;
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            //handler.removeCallbacks(runabble);
            running = false;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String allp;
            try {
                //Connection c = new Connection();
                if (params[0].equals("GPIO_IEtime"))
                    response = c.sendString( allp = params[0] + ";", 256);
                else if (params[0].equals("GPIO_Ilist"))
                    response = c.sendString(params[0] + ";", 16384);
                else if (params[0].equals("Add_GPIO_in"))
                    response = c.sendString(params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + params[4] + ";" + params[5] + ";" + params[6] + ";" + android.os.Build.MODEL, 1024);
                else if (params[0].equals("Edit_GPIO_in"))
                    response = c.sendString(params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + params[4] + ";" + params[5] + ";" + params[6] + ";" + params[7] + ";" + params[8] + ";" + android.os.Build.MODEL, 1024);
                else if (params[0].equals("Delete_GPIO_out"))
                    response = c.sendString(params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + android.os.Build.MODEL, 256);
                else if (params[0].equals("GPIO_Oname"))
                    response = c.sendString(params[0] + ";", 8192);
                else if (params[0].equals("Allpins_GPIO_pwm"))
                    response = c.sendString( params[0] + ";", 1024);
                list = new ArrayList<String>(Arrays.asList(response.split(";")));
                if (list.get(0).equals("true")) passwd = true;
                else if (list.get(0).equals("false")) passwd = false;
                succes = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
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
            if (menuCreated == false) cancel(true);

            return succes;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            running = false;
            if (result == true && passwd == true) {
                r.setTextColor(Color.GREEN);
                if (list.get(1).equals("GPIO_IEtime")) {
                    if (!list.get(2).equals("None")) {
                        Date date = StringtoDate(list.get(2));
                        if (!date.equals(edittime)) {
                            list_update();
                            edittime = date;
                        }
                    } else gridView.setAdapter(null);
                } else if (list.get(1).equals("GPIO_Ilist")) {
                    idki.clear();
                    gpios.clear();
                    stany.clear();
                    nazwy.clear();
                    reverses.clear();
                    bindidG.clear();
                    bindtypeG.clear();
                    for (int j = 2; j < (list.size() - 1); j = j + 7) {
                        idki.add(list.get(j));
                        gpios.add(list.get(j + 1));
                        stany.add(list.get(j + 2));
                        nazwy.add(list.get(j + 3));
                        reverses.add(list.get(j + 4));
                        bindidG.add(list.get(j + 5));
                        bindtypeG.add(list.get(j + 6));
                        if (j > 1000) break;
                    }

                    gridView.setAdapter(new CustomGridAdapter(mContext, gpios, stany, nazwy, reverses, bindtypeG, 0));
                } else if (list.get(1).equals("Add_GPIO_in") || list.get(1).equals("Edit_GPIO_in") || list.get(1).equals("Delete_GPIO_out")) {
                    edittime = new Date(1);
                    check_state();
                } else if (list.get(1).equals("GPIO_Oname")) {
                    try {
                        idki_out.clear();
                        nazwy_out.clear();
                        gpios_out.clear();
                        for (int j = 2; j < (list.size() - 1); j = j + 4) {
                            idki_out.add(list.get(j));
                            nazwy_out.add(list.get(j + 1));
                            for (String temp : new ArrayList<String>(Arrays.asList(list.get(j + 2).split(","))))
                                gpios_out.add(temp);

                        }
                        delegate.processFinish("1");
                    } catch (IndexOutOfBoundsException e) {
                        idki_out.clear();
                        nazwy_out.clear();
                        delegate.processFinish("0");
                    }

                } else if (list.get(1).equals("Allpins_GPIO_pwm")) {
                    gpios_pwm.clear();
                    for (int j = 2; j < list.size() - 1; j++) {
                        gpios_pwm.add(list.get(j));
                    }
                }else if (list.get(1).equals("Delete_GPIO_out")) {
                    myDbHelper.usunPowiadomieniePoTID(list.get(2));
                }
            } else if (result == false || passwd == false) {
                if (result == false) {
                    myDbHelper.dodajLog(id_U, response);
                    r.setTextColor(Color.RED);
                } else if (passwd == false) {
                    myDbHelper.dodajLog(id_U, list.get(1));
                    r.setTextColor(Color.RED);
                }
            }

            pb.setVisibility(View.INVISIBLE);
            //  hendlerm();
            myDbHelper.close();
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

                LayoutInflater factory = LayoutInflater.from(mContext);
                final View loginView = factory.inflate(R.layout.addtypein, null);
                final Spinner bindtype = (Spinner) loginView.findViewById(R.id.bindtype);
                final Spinner bindid = (Spinner) loginView.findViewById(R.id.bindid);
                String[] items = new String[]{"None (Read only)", "Output on/off switch", "Output pushbutton"};
                ArrayAdapter<String> adapterbt = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, items);
                bindtype.setAdapter(adapterbt);
                final TableRow tr1 = (TableRow) loginView.findViewById(R.id.tableRow5);
                bindtype.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        if (position == 0) tr1.setVisibility(View.INVISIBLE);
                        else tr1.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }
                });

                GPIO_InputTask GPIO_NameGet = new GPIO_InputTask(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if (output.equals("1")) {

                            String[] items = nazwy_out.toArray(new String[0]);
                            ArrayAdapter<String> adapterbi = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, items);
                            bindid.setAdapter(adapterbi);

                        }
                    }
                });
                GPIO_NameGet.execute("GPIO_Oname");
                GPIO_InputTask Allpins_GPIO_pwm = new GPIO_InputTask(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {

                    }
                });
                Allpins_GPIO_pwm.execute("Allpins_GPIO_pwm");
                final AlertDialog d = new AlertDialog.Builder(mContext)
                        .setView(loginView)
                        .setPositiveButton("ADD",
                                new Dialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface d, int which) {
                                        //Do nothing here. We override the onclick
                                    }
                                })
                        .setNegativeButton("CANCEL", null)
                        .create();

                d.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                EditText name = (EditText) loginView.findViewById(R.id.name);
                                EditText gpio = (EditText) loginView.findViewById(R.id.gpio);
                                CheckBox reverse = (CheckBox) loginView.findViewById(R.id.reverse);
                                String reverseS = "0";
                                if (reverse.isChecked()) reverseS = "1";
                                int bindtypeI = bindtype.getSelectedItemPosition();
                                String bindtypeS = String.valueOf(bindtypeI);
                                String bindidS = "0";
                                if (bindtypeI > 0 && idki_out.size() > 0)
                                    bindidS = idki_out.get(bindid.getSelectedItemPosition());
                                boolean cont = false;
                                boolean notbcm = false;
                                for (String temp : new ArrayList<String>(Arrays.asList(gpio.getText().toString().split(",")))) {
                                    if (!gpios_pwm.contains(temp) && !gpios_out.contains(temp) && !gpios.contains(temp)) {
                                        if (!temp.matches("2|3|4|17|27|22|10|9|11|5|6|13|19|26|14|15|18|23|24|25|8|7|12|16|20|21")) {
                                            notbcm = true;
                                            break;
                                        }
                                    } else {
                                        cont = true;
                                        break;
                                    }
                                }
                                if (name.getText().toString().matches("") || gpio.getText().toString().matches(""))
                                    Toast.makeText(mContext, "Fill Name and GPIO!", Toast.LENGTH_SHORT).show();
                                else if (cont)
                                    Toast.makeText(mContext, "Pin/s alredy in use!", Toast.LENGTH_SHORT).show();
                                else if (notbcm)
                                    Toast.makeText(mContext, "Not GPIO BCM!", Toast.LENGTH_SHORT).show();
                                else {
                                    GPIO_InputTask Add_Type = new GPIO_InputTask(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {

                                        }
                                    });
                                    Add_Type.execute("Add_GPIO_in", gpio.getText().toString(), name.getText().toString(), DatetoString(new Date()), reverseS, bindidS, bindtypeS);

                                    d.dismiss();
                                }
                            }
                        });
                    }
                });
                d.show();

            }
        });
        pb.setVisibility(View.INVISIBLE);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        menuCreated = true;
        edittime = new Date(1);
        running = false;
        c.cancel();
        check_state();
        hendlerm();


        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
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
            hendlerm();
            handlerOn = true;
        }
        //menuCreated=true;
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


    public void hendlerm() {

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