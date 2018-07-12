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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;


public class GPIO_Status extends Fragment {
    static String dstName, model = android.os.Build.MODEL;
    static int Page, arTime;
    ProgressBar pb;
    boolean menuCreated = false;
    Handler handler = new Handler();
    boolean handlerOn = false, running = false, VisibleToUser = true, editMode = false;
    Runnable runabble = null;
    GridView gridView;
    Button r;
    static int id_U;
    public static Context mContext;
    private ArrayList<String> idki = new ArrayList<String>();
    private ArrayList<String> gpios = new ArrayList<String>();
    private ArrayList<String> gpios_in = new ArrayList<String>();
    private ArrayList<String> gpios_pwm = new ArrayList<String>();
    private ArrayList<String> stany = new ArrayList<String>();
    private ArrayList<String> nazwy = new ArrayList<String>();
    private ArrayList<String> reverses = new ArrayList<String>();
    private ArrayList<String> types = new ArrayList<String>();
    Date edittime = new Date(1), settime = new Date(1);
    static Connection c;

    public GPIO_Status() {
    }

    public static GPIO_Status newInstance(int page, String name, Connection conn, Context Context, int artime, int id_u) {
        GPIO_Status fragment = new GPIO_Status();
        Page = page;
        dstName = name;
        mContext = Context;
        arTime = artime;
        id_U = id_u;
        c=conn;
        return fragment;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gpio_status, container, false);
        setHasOptionsMenu(true);
        gridView = (GridView) rootView.findViewById(R.id.gridView1);
        CheckBox editModeCh = (CheckBox) rootView.findViewById(R.id.editMode);
        editModeCh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                  @Override
                                                  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                      editMode = isChecked;
                                                  }
                                              }
        );
        gridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                int action = event.getActionMasked();
                float currentXPosition = event.getX();
                float currentYPosition = event.getY();
                final int position = gridView.pointToPosition((int) currentXPosition, (int) currentYPosition);
                //gridView.setSelection(position);
                // get the center for the clipping circle
                final AlphaAnimation alpha = new AlphaAnimation(1F, 0.5F);
                if (position > AdapterView.INVALID_POSITION) {
                    final String idt = idki.get(position);
                    final String gpio = gpios.get(position);
                    final String type = types.get(position);
                    String stan = stany.get(position);
                    final String reverse = reverses.get(position);
                    String stan_ustaw = null;
                    LinearLayout ll1 = (LinearLayout) gridView.getChildAt(position);
                    final ImageView iv = (ImageView) ll1.findViewById(R.id.grid_item_image);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        alpha.setFillAfter(true);
                        ll1.startAnimation(alpha);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        alpha.setFillAfter(false);
                        ll1.startAnimation(alpha);
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN && !editMode) {
                        if (stan.equals("0") && type.equals("0")) stan_ustaw = "1";
                        else if (stan.equals("1") && type.equals("0")) stan_ustaw = "0";
                        else if (type.equals("1") && reverse.equals("0")) stan_ustaw = "1";
                        else if (type.equals("1") && reverse.equals("1")) stan_ustaw = "0";
                    /*else if (stan.equals("2")&&type.equals("1")){
						GPIO_StatusTask Set_GPIO_State = new GPIO_StatusTask(new AsyncResponse(){
							@Override
							public void processFinish(String output){
								stany.set(position, output);
								//ImageView iv = (ImageView) v.findViewById(R.id.grid_item_image);
								if ((output.equals("1")&&reverse.equals("0"))||(output.equals("0")&&reverse.equals("1")))
									iv.setImageResource(R.drawable.green);
								else if ((output.equals("0")&&reverse.equals("0"))||(output.equals("1")&&reverse.equals("1")))
									iv.setImageResource(R.drawable.red);
								else iv.setImageResource(R.drawable.yelow);

							}});
						stan_ustaw="1";if(reverse.equals("1"))stan_ustaw = "0";
						Set_GPIO_State.execute("GPIO_set",idt,gpio,stan_ustaw,DatetoString(settime=new Date()),reverse,android.os.Build.MODEL);
						stany.set(position, stan_ustaw);
					}*/
                        else if (stan.equals("2") && !type.equals("1")) {//stan_ustaw="0";

                            PopupMenu popup = new PopupMenu(getActivity(), v);
                            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                            popup.setOnMenuItemClickListener(
                                    new PopupMenu.OnMenuItemClickListener() {
                                        public boolean onMenuItemClick(MenuItem item) {
                                            switch (item.getItemId()) {
                                                case R.id.on:
                                                    GPIO_StatusTask Set_GPIO_State = new GPIO_StatusTask(new AsyncResponse() {
                                                        @Override
                                                        public void processFinish(String output) {
                                                            stany.set(position, output);
                                                            //ImageView iv = (ImageView) v.findViewById(R.id.grid_item_image);
                                                            if ((output.equals("1") && reverse.equals("0")) || (output.equals("0") && reverse.equals("1")))
                                                                iv.setImageResource(R.drawable.green);
                                                            else if ((output.equals("0") && reverse.equals("0")) || (output.equals("1") && reverse.equals("1")))
                                                                iv.setImageResource(R.drawable.red);
                                                            else
                                                                iv.setImageResource(R.drawable.yelow);

                                                        }
                                                    });
                                                    String stan_ustaw = "1";
                                                    if (reverse.equals("1")) stan_ustaw = "0";
                                                    Set_GPIO_State.execute("GPIO_set", idt, gpio, stan_ustaw, DatetoString(settime = new Date()), reverse, android.os.Build.MODEL);
                                                    break;
                                                case R.id.off:
                                                    GPIO_StatusTask Set_GPIO_State_1 = new GPIO_StatusTask(new AsyncResponse() {
                                                        @Override
                                                        public void processFinish(String output) {
                                                            stany.set(position, output);
                                                            //ImageView iv = (ImageView) v.findViewById(R.id.grid_item_image);
                                                            if ((output.equals("1") && reverse.equals("0")) || (output.equals("0") && reverse.equals("1")))
                                                                iv.setImageResource(R.drawable.green);
                                                            else if ((output.equals("0") && reverse.equals("0")) || (output.equals("1") && reverse.equals("1")))
                                                                iv.setImageResource(R.drawable.red);
                                                            else
                                                                iv.setImageResource(R.drawable.yelow);

                                                        }
                                                    });
                                                    stan_ustaw = "0";
                                                    if (reverse.equals("1")) stan_ustaw = "1";
                                                    Set_GPIO_State_1.execute("GPIO_set", idt, gpio, stan_ustaw, DatetoString(settime = new Date()), reverse, android.os.Build.MODEL);
                                                    break;

                                            }
                                            return true;
                                        }
                                    });

                            popup.show();//showing popup menu

                        }
                        if (stan.equals("0") || stan.equals("1") || type.equals("1")) {
                            GPIO_StatusTask Set_GPIO_State = new GPIO_StatusTask(new AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    stany.set(position, output);
                                    //ImageView iv = (ImageView) v.findViewById(R.id.grid_item_image);
                                    //LinearLayout ll1 = (LinearLayout) gridView.getChildAt(position);
                                    //ImageView iv = (ImageView) ll1.findViewById(R.id.grid_item_image);

                                    if ((output.equals("1") && reverse.equals("0")) || (output.equals("0") && reverse.equals("1")))
                                        iv.setImageResource(R.drawable.green);
                                    else if ((output.equals("0") && reverse.equals("0")) || (output.equals("1") && reverse.equals("1")))
                                        iv.setImageResource(R.drawable.red);
                                    else iv.setImageResource(R.drawable.yelow);

                                }
                            });
                            Set_GPIO_State.execute("GPIO_set", idt, gpio, stan_ustaw, DatetoString(settime = new Date()), reverse, android.os.Build.MODEL);
                            //Set_GPIO_State.execute("GPIO_set",idt,gpio,stan_ustaw,"2017-01-06 19:22:50.938",reverse,model);
                            if (type.equals("1")) stany.set(position, stan_ustaw);
                        }
                        //Toast.makeText(mContext, "+"+position, Toast.LENGTH_SHORT).show();
                    } else if (event.getAction() == MotionEvent.ACTION_UP && !editMode && type.equals("1")) {
                        if (reverse.equals("0")) stan_ustaw = "0";
                        else if (reverse.equals("1")) stan_ustaw = "1";
                        GPIO_StatusTask Set_GPIO_State = new GPIO_StatusTask(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                stany.set(position, output);
                                //ImageView iv = (ImageView) v.findViewById(R.id.grid_item_image);
                                if ((output.equals("1") && reverse.equals("0")) || (output.equals("0") && reverse.equals("1")))
                                    iv.setImageResource(R.drawable.green);
                                else if ((output.equals("0") && reverse.equals("0")) || (output.equals("1") && reverse.equals("1")))
                                    iv.setImageResource(R.drawable.red);
                                else iv.setImageResource(R.drawable.yelow);

                            }
                        });
                        Set_GPIO_State.execute("GPIO_set", idt, gpio, stan_ustaw, DatetoString(settime = new Date()), reverse, android.os.Build.MODEL);
                        if (type.equals("1")) stany.set(position, stan_ustaw);

                        //Toast.makeText(mContext, "-"+position, Toast.LENGTH_SHORT).show();
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN && editMode) {
                        LayoutInflater factory = LayoutInflater.from(mContext);
                        GPIO_StatusTask Allpins_GPIO_pwm = new GPIO_StatusTask(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                            }
                        });
                        Allpins_GPIO_pwm.execute("Allpins_GPIO_pwm");
                        GPIO_StatusTask Allpins_GPIO_in = new GPIO_StatusTask(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                            }
                        });
                        Allpins_GPIO_in.execute("Allpins_GPIO_in");
                        final View loginView = factory.inflate(R.layout.addtype, null);
                        TextView title = (TextView) loginView.findViewById(R.id.titleL);
                        title.setText("Edit GPIO Output:");
                        final String nazwaL = nazwy.get(position);
                        final EditText name = (EditText) loginView.findViewById(R.id.name);
                        name.setText(nazwaL);
                        final String gpioL = gpios.get(position);
                        final EditText gpioet = (EditText) loginView.findViewById(R.id.gpio);
                        gpioet.setText(gpioL);
                        final String reverseL = reverses.get(position);
                        final CheckBox reversech = (CheckBox) loginView.findViewById(R.id.reverse);
                        final Spinner actl = (Spinner) loginView.findViewById(R.id.actl);
                        String[] items = new String[]{"On/off switch (o)", "Pushbutton (p)"};
                        ArrayAdapter<String> adapterbt = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, items);
                        actl.setAdapter(adapterbt);
                        actl.setSelection(Integer.parseInt(types.get(position)));
                        if (reverseL.equals("1")) reversech.setChecked(true);
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
                                                GPIO_StatusTask Delete_Type = new GPIO_StatusTask(new AsyncResponse() {
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
                                        List<String> templist = new ArrayList(Arrays.asList(gpioet.getText().toString().split(",")));
                                        List<String> templist2 = new ArrayList(Arrays.asList(gpios.get(position).split(",")));
                                        boolean cont = false;
                                        boolean notbcm = false;
                                        for (String temp : templist) {
                                            if (temp.matches("2|3|4|17|27|22|10|9|11|5|6|13|19|26|14|15|18|23|24|25|8|7|12|16|20|21")) {
                                                if (!templist2.contains(temp) && (gpios_pwm.contains(temp) || gpios_in.contains(temp))) {
                                                    cont = true;
                                                    break;
                                                }
                                            } else {
                                                notbcm = true;
                                                break;
                                            }
                                        }
                                        String reverseS = "0";
                                        if (reversech.isChecked()) reverseS = "1";
                                        if (name.getText().toString().matches("") || gpioet.getText().toString().matches(""))
                                            Toast.makeText(mContext, "Fill in Name and GPIO!", Toast.LENGTH_SHORT).show();
                                        else if (cont)
                                            Toast.makeText(mContext, "Pin/s alredy in use!", Toast.LENGTH_SHORT).show();
                                        else if (notbcm)
                                            Toast.makeText(mContext, "Not GPIO BCM!", Toast.LENGTH_SHORT).show();
                                        else {
                                            GPIO_StatusTask Edit_Type = new GPIO_StatusTask(new AsyncResponse() {
                                                @Override
                                                public void processFinish(String output) {

                                                }
                                            });
                                            Edit_Type.execute("Edit_GPIO_out", idt, gpioet.getText().toString(), name.getText().toString(), DatetoString(new Date()), reverseS, gpioL, String.valueOf(actl.getSelectedItemPosition()));

                                            d.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                        d.show();
                    }
                }
                return true;
            }

            ;
        });

        return rootView;
    }


    public void check_state() {
        if (running == false) {
            GPIO_StatusTask edittimecheck = new GPIO_StatusTask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                }
            });
            edittimecheck.execute("GPIO_OEtime");

        }
    }

    public void list_update() {
        GPIO_StatusTask GPIO_ListGet = new GPIO_StatusTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {

            }
        });
        GPIO_ListGet.execute("GPIO_Olist");
    }

    public static Date StringtoDate(String dtStart) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
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

    public static String DatetoString(Date date) {
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

    public class GPIO_StatusTask extends AsyncTask<String, String, Boolean> {


        public AsyncResponse delegate = null;
        DataBaseHelper myDbHelper;

        public GPIO_StatusTask(AsyncResponse delegate) {
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

            try {
                //Connection c = new Connection();
                if (params[0].equals("GPIO_OEtime"))
                    response = c.sendString(params[0] + ";", 128);
                else if (params[0].equals("GPIO_set"))
                    response = c.sendString( params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + params[4] + ";" + params[5] + ";" + params[6], 128);
                else if (params[0].equals("Allpins_GPIO_pwm"))
                    response = c.sendString(params[0] + ";", 1024);
                else if (params[0].equals("Allpins_GPIO_in"))
                    response = c.sendString( params[0] + ";", 1024);
                else if (params[0].equals("GPIO_Olist"))
                    response = c.sendString( params[0] + ";", 16384);
                else if (params[0].equals("Add_GPIO_out"))
                    response = c.sendString(params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + params[4] + ";" + params[5] + ";" + android.os.Build.MODEL, 1024);
                else if (params[0].equals("Edit_GPIO_out"))
                    response = c.sendString(params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + params[4] + ";" + params[5] + ";" + params[6] + ";" + params[7] + ";" + android.os.Build.MODEL, 1024);
                else if (params[0].equals("Delete_GPIO_out"))
                    response = c.sendString(params[0] + ";" + params[1] + ";" + params[2] + ";" + params[3] + ";" + android.os.Build.MODEL, 256);
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
                if (list.get(1).equals("GPIO_OEtime")) {
                    if (!list.get(2).equals("None")) {
                        Date date = StringtoDate(list.get(2));

                        if (!date.equals(edittime)) {
                            list_update();
                            edittime = date;
                        }

                    } else gridView.setAdapter(null);
                } else if (list.get(1).equals("GPIO_Olist")) {
                    idki.clear();
                    gpios.clear();
                    stany.clear();
                    nazwy.clear();
                    reverses.clear();
                    types.clear();
                    for (int j = 2; j < (list.size() - 1); j = j + 6) {
                        idki.add(list.get(j));
                        gpios.add(list.get(j + 1));
                        stany.add(list.get(j + 2));
                        nazwy.add(list.get(j + 3));
                        reverses.add(list.get(j + 4));
                        types.add(list.get(j + 5).equals("None") ? "0" : list.get(j + 5));
                        if (j > 1000) break;
                    }
                    gridView.setAdapter(new CustomGridAdapter(mContext, gpios, stany, nazwy, reverses, types));
                } else if (list.get(1).equals("GPIO_set")) {
                    Date date = StringtoDate(list.get(3));
                    if (!date.equals(settime)) list_update();
                    edittime = date;
                    delegate.processFinish(list.get(2));
                } else if (list.get(1).equals("Add_GPIO_out") || list.get(1).equals("Edit_GPIO_out") || list.get(1).equals("Delete_GPIO_out")) {
                    edittime = new Date(1);
                    check_state();
                } else if (list.get(1).equals("Allpins_GPIO_pwm")) {
                    gpios_pwm.clear();
                    for (int j = 2; j < (list.size() - 1); j++) {
                        gpios_pwm.add(list.get(j));
                    }
                } else if (list.get(1).equals("Allpins_GPIO_in")) {
                    gpios_in.clear();
                    for (int j = 2; j < (list.size() - 1); j++) {
                        gpios_in.add(this.list.get(j));
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
                edittime = new Date(1);
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
                final View loginView = factory.inflate(R.layout.addtype, null);
                GPIO_StatusTask Allpins_GPIO_pwm = new GPIO_StatusTask(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                    }
                });
                Allpins_GPIO_pwm.execute("Allpins_GPIO_pwm");
                GPIO_StatusTask Allpins_GPIO_in = new GPIO_StatusTask(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                    }
                });
                Allpins_GPIO_in.execute("Allpins_GPIO_in");
                final Spinner actl = (Spinner) loginView.findViewById(R.id.actl);
                String[] items = new String[]{"On/off switch (o)", "Pushbutton (p)"};
                ArrayAdapter<String> adapterbt = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, items);
                actl.setAdapter(adapterbt);
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
                                boolean cont = false;
                                boolean notbcm = false;
                                for (String temp : new ArrayList<String>(Arrays.asList(gpio.getText().toString().split(",")))) {
                                    if (!gpios_pwm.contains(temp) && !gpios_in.contains(temp)) {
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
                                    GPIO_StatusTask Add_Type = new GPIO_StatusTask(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                        }
                                    });
                                    Add_Type.execute("Add_GPIO_out", gpio.getText().toString(), name.getText().toString(), DatetoString(new Date()), reverseS, String.valueOf(actl.getSelectedItemPosition()));

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