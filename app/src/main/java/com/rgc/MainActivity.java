package com.rgc;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class MainActivity extends AppCompatActivity {
    //MenuItem miActionProgressItem;
    ProgressBar pb;
    int verCode = 4;
    //boolean activityCreated = false;
    Connection cC;
    DataBaseHelper myDbHelper;
    ListView listView;
    SimpleCursorAdapter customAdapter;

    public void addConnection(final boolean editMode, final Cursor k) {

        LayoutInflater factory = LayoutInflater.from(this);
        final View loginView = factory.inflate(R.layout.addconnection, null);
        final EditText name = (EditText) loginView.findViewById(R.id.name);
        final EditText ip = (EditText) loginView.findViewById(R.id.ip);
        final EditText port = (EditText) loginView.findViewById(R.id.port);
        final EditText pass = (EditText) loginView.findViewById(R.id.password);
        pass.setEnabled(false);
        final EditText artime = (EditText) loginView.findViewById(R.id.artime);
        final CheckBox chk = (CheckBox) loginView.findViewById(R.id.setpass);
        final CheckBox chk2 = (CheckBox) loginView.findViewById(R.id.disablepass);
        final CheckBox tab_output = (CheckBox) loginView.findViewById(R.id.GPIO_output);
        final CheckBox tab_input = (CheckBox) loginView.findViewById(R.id.GPIO_input);
        final CheckBox tab_pwm = (CheckBox) loginView.findViewById(R.id.GPIO_pwm);
        //final CheckBox tab_SA = (CheckBox) loginView.findViewById(R.id.GPIO_ASA);
        final CheckBox tab_history = (CheckBox) loginView.findViewById(R.id.GPIO_history);
        final CheckBox sensors = (CheckBox) loginView.findViewById(R.id.sensors);
        final CheckBox notifications = (CheckBox) loginView.findViewById(R.id.notifications);
        final CheckBox tab_ASA = (CheckBox) loginView.findViewById(R.id.GPIO_ASA);
        final CheckBox tab_Chains = (CheckBox) loginView.findViewById(R.id.GPIO_chains);
        final CheckBox tcpOnly = (CheckBox) loginView.findViewById(R.id.tcpOnly);
        final CheckBox rf = (CheckBox) loginView.findViewById(R.id.rf);
        final CheckBox cmd = (CheckBox) loginView.findViewById(R.id.cmd);
        final CheckBox wifi = (CheckBox) loginView.findViewById(R.id.wifi);
        final EditText ip2 = (EditText) loginView.findViewById(R.id.ip2);
        final EditText port2 = (EditText) loginView.findViewById(R.id.port2);
        final EditText ssid = (EditText) loginView.findViewById(R.id.ssid);
        final TableRow ssidTr = loginView.findViewById(R.id.tableRow15_5);
        final TableRow up2r = loginView.findViewById(R.id.tableRow16);
        final TableRow portTr = loginView.findViewById(R.id.tableRow17);
        final Button wifiSSIDGet = loginView.findViewById(R.id.getWifiSSID);
        wifiSSIDGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                    ssid.setText(wifiInfo.getSSID().replaceAll("\"",""));
                }
            };
        });
        wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
                if (flag) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
                    ssidTr.setVisibility(View.VISIBLE);
                    up2r.setVisibility(View.VISIBLE);
                    portTr.setVisibility(View.VISIBLE);
                } else {
                    ssidTr.setVisibility(View.GONE);
                    up2r.setVisibility(View.GONE);
                    portTr.setVisibility(View.GONE);
                }
            }
        });
        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
                if (flag) {
                    pass.setEnabled(true);
                } else {
                    pass.setText("");
                    pass.setEnabled(false);
                }
            }
        });
        if(editMode){
            TextView tv1 = (TextView) loginView.findViewById(R.id.titleL);
            tv1.setText("Connection edit: ");
            TextView tv2 = (TextView) loginView.findViewById(R.id.hasloL);
            tv2.setText("Change password?");
            name.setText(k.getString(1));
            ip.setText(k.getString(2));
            port.setText(k.getString(3));
            pass.setEnabled(false);
            artime.setText(k.getString(6));
            chk2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
                    if (flag) {
                        chk.setChecked(false);
                        chk.setEnabled(false);
                    } else
                        chk.setEnabled(true);
                }

            });
            tab_output.setChecked(k.getInt(7) != 0);
            tab_input.setChecked(k.getInt(8) != 0);
            tab_pwm.setChecked(k.getInt(9) != 0);
            tab_history.setChecked(k.getInt(11) != 0);
            sensors.setChecked(k.getInt(12) != 0);
            notifications.setChecked(k.getInt(13) != 0);
            tab_ASA.setChecked(k.getInt(15) != 0);
            tab_Chains.setChecked(k.getInt(16) != 0);
            tcpOnly.setChecked(k.getInt(17) != 0);
            rf.setChecked(k.getInt(18) != 0);
            cmd.setChecked(k.getInt(19) != 0);
            loginView.findViewById(R.id.tableRow4_5).setVisibility(View.VISIBLE);
            ip2.setText(k.getString(20));
            port2.setText(k.getString(21));
            ssid.setText(k.getString(22));
            if(!k.isNull(23))wifi.setChecked(k.getInt(23) != 0);
        }
        AlertDialog.Builder db = new AlertDialog.Builder(MainActivity.this)
                .setView(loginView)
                .setPositiveButton("SAVE",
                        new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                //Do nothing here. We override the onclick
                            }
                        })
                .setNegativeButton("CANCEL", null);
        if(editMode)db.setNeutralButton("DELETE",
                new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        myDbHelper.usunUrzadzenie(k.getInt(0));
                        recreate();
                    }
                });
        final AlertDialog d = db.create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (name.getText().toString().matches("") || ip.getText().toString().matches("") || port.getText().toString().matches("") || artime.getText().toString().matches(""))
                            Toast.makeText(getApplicationContext(), "Fill in the required fields!", Toast.LENGTH_SHORT).show();
                        else if (pass.getText().toString().matches("") && chk.isChecked())
                            Toast.makeText(getApplicationContext(), "Fill in or disable password!", Toast.LENGTH_SHORT).show();
                        else if ((ip.getText().toString().matches("") || port.getText().toString().matches("")|| ssid.getText().toString().matches("")) && wifi.isChecked())
                            Toast.makeText(getApplicationContext(), "Fill in IP, PORT and SSID for wifi specific connection or turn it off!", Toast.LENGTH_SHORT).show();
                        else {
                            DataBaseHelper myDbHelper = new DataBaseHelper(getApplicationContext());
                            String passWd = pass.getText().toString();
                            String encWd = "";
                            if (!passWd.isEmpty()) {
                                passWd = sha256(passWd);
                                encWd = md5(pass.getText().toString());
                            }if(editMode){
                                if (chk.isChecked()) {
                                    myDbHelper.edytujUrzadzenie(k.getInt(0), name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()), passWd, encWd, Float.parseFloat(artime.getText().toString()), (tab_output.isChecked()) ? 1 : 0, (tab_input.isChecked()) ? 1 : 0, (tab_pwm.isChecked()) ? 1 : 0, (tab_history.isChecked()) ? 1 : 0,(sensors.isChecked()) ? 1 : 0,(notifications.isChecked()) ? 1 : 0, (tab_ASA.isChecked()) ? 1 : 0, (tab_Chains.isChecked()) ? 1 : 0, (tcpOnly.isChecked()) ? 1 : 0, (rf.isChecked()) ? 1 : 0, (cmd.isChecked()) ? 1 : 0,ip2.getText().toString(), port2.getText().toString(),ssid.getText().toString(),(wifi.isChecked()) ? 1 : 0);
                                } else if (!chk.isChecked() && !chk2.isChecked()) {
                                    myDbHelper.edytujUrzadzenie(k.getInt(0), name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()),null,null,Float.parseFloat(artime.getText().toString()), (tab_output.isChecked()) ? 1 : 0, (tab_input.isChecked()) ? 1 : 0, (tab_pwm.isChecked()) ? 1 : 0, (tab_history.isChecked()) ? 1 : 0,(sensors.isChecked()) ? 1 : 0,(notifications.isChecked()) ? 1 : 0, (tab_ASA.isChecked()) ? 1 : 0, (tab_Chains.isChecked()) ? 1 : 0, (tcpOnly.isChecked()) ? 1 : 0, (rf.isChecked()) ? 1 : 0, (cmd.isChecked()) ? 1 : 0,ip2.getText().toString(), port2.getText().toString(),ssid.getText().toString(),(wifi.isChecked()) ? 1 : 0);
                                } else if (chk2.isChecked()) {
                                    myDbHelper.edytujUrzadzenie(k.getInt(0), name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()), "", "", Float.parseFloat(artime.getText().toString()), (tab_output.isChecked()) ? 1 : 0, (tab_input.isChecked()) ? 1 : 0, (tab_pwm.isChecked()) ? 1 : 0,  (tab_history.isChecked()) ? 1 : 0,(sensors.isChecked()) ? 1 : 0,(notifications.isChecked()) ? 1 : 0, (tab_ASA.isChecked()) ? 1 : 0, (tab_Chains.isChecked()) ? 1 : 0, (tcpOnly.isChecked()) ? 1 : 0, (rf.isChecked()) ? 1 : 0, (cmd.isChecked()) ? 1 : 0,ip2.getText().toString(), port2.getText().toString(),ssid.getText().toString(),(wifi.isChecked()) ? 1 : 0);
                                }
                            }else {
                                myDbHelper.dodajUrzadzenie(name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()), passWd, encWd, Float.parseFloat(artime.getText().toString()), (tab_output.isChecked()) ? 1 : 0, (tab_input.isChecked()) ? 1 : 0, (tab_pwm.isChecked()) ? 1 : 0,
                                        (tab_history.isChecked()) ? 1 : 0, (sensors.isChecked()) ? 1 : 0, (notifications.isChecked()) ? 1 : 0, (tab_ASA.isChecked()) ? 1 : 0, (tab_Chains.isChecked()) ? 1 : 0, (tcpOnly.isChecked()) ? 1 : 0, (rf.isChecked()) ? 1 : 0, (cmd.isChecked()) ? 1 : 0, ip2.getText().toString(), port2.getText().toString(),ssid.getText().toString(),(wifi.isChecked()) ? 1 : 0);
                                Toast.makeText(getApplicationContext(), "Added: " + name.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                            recreate();
                            myDbHelper.close();
                            d.dismiss();
                        }
                    }
                });
            }
        });
        d.show();

    }

    public static final String sha256(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return "";
        }
    }

    public static final String md5(final String toEncrypt) {
        try {
            byte[] keyBytes = toEncrypt.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(keyBytes);
            keyBytes = md.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < keyBytes.length; i++) {
                sb.append(String.format("%02X", keyBytes[i]));
            }
            //Log.d("mdi9", sb.toString().toLowerCase());
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return "";
        }
    }

    public class MyClientTask extends AsyncTask<Void, Void, Boolean> {

        String dstName;
        String dstAddress;
        String dstPass, encKey;
        int dstPort, id_U, selectedTab;
        float arTime;
        String response = "";
        boolean succes;
        boolean passwd,tcpOnly;
        List<String> list;
        String Message;
        Connection c;
        MaterialDialog pDialog;

        MyClientTask(String name, String addr, int port, String pass, String enc_key, float artime, int id_u,int selected, String message, boolean tcpOnly) {
            dstName = name;
            dstAddress = addr;
            dstPort = port;
            dstPass = pass;
            encKey = enc_key;
            arTime = artime;
            id_U = id_u;
            selectedTab = selected;
            Message = message;
            this.tcpOnly = tcpOnly;

        }


        @Override
        protected  void onCancelled(){
            pb.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            try {
                pb.setVisibility(View.VISIBLE);
            }catch (NullPointerException c){}
            final MyClientTask that = this;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        //Log.d("Mdi",myClientTask.getStatus().toString());
                        if(that.getStatus() != AsyncTask.Status.FINISHED){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        pDialog = new MaterialDialog.Builder(MainActivity.this)
                                                .content(dstName+" not responding...")
                                                .title("Connecting...")
                                                .progress(true, 0)
                                                .negativeText("Cancel connection")
                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                                        that.cancel(true);
                                                        pb.setVisibility(View.INVISIBLE);
                                                        if(c != null)
                                                            c.cancel();
                                                    }
                                                })
                                                .show();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {
                //c = new Connection(dstAddress, dstPort, dstPass, encKey,tcpOnly,dstAddress2,dstPort2);
                c = new Connection(myDbHelper,id_U,-1,getApplicationContext());
                cC=c;
                if(Message.matches("Server_logs|Server_status"))
                    response = c.sendStringTCP(Message,true);
                else
                    response = c.sendString(Message, 1024);
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
            if(pDialog!=null)if(pDialog.isShowing())pDialog.dismiss();
            if (result == true && passwd == false)
                if (list.get(1).equals("Conection OK, but no compabile method found, probably encryption error"))
                    Toast.makeText(MainActivity.this, list.get(1) + " or server update is needed", Toast.LENGTH_LONG).show();
                else Toast.makeText(MainActivity.this, list.get(1), Toast.LENGTH_LONG).show();
            else if (result == false)
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
            try {
                pb.setVisibility(View.INVISIBLE);
            }catch (NullPointerException c){}
            if (result == true && passwd == true) {
                if (list.get(1).equals("version_check")) {
                    if(verCode == Integer.parseInt(list.get(2))){
                        Intent i = new Intent(MainActivity.this, PagerTabStripActivity.class);
                        i.putExtra("connection",c);
                        i.putExtra("nazwa", dstName);
                        i.putExtra("ip", dstAddress);
                        i.putExtra("port", dstPort);
                        i.putExtra("password", dstPass);
                        int temp = Math.round(arTime * 1000);
                        i.putExtra("id_u", id_U);
                        i.putExtra("enc_key", encKey);
                        i.putExtra("artime", temp);
                        i.putExtra("selectedTab", selectedTab);
                        startActivity(i);
                    }else if (Integer.parseInt(list.get(2)) >= 5)
                        new MaterialDialog.Builder(MainActivity.this)
                                .title("Incompatible version of the server.")
                                .content("Would you like to update ?")
                                .positiveText("Update")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        GetAsyncData execad = new GetAsyncData(new GetAsyncData.AsyncResponse() {
                                            @Override
                                            public void processFinish(List<String> list) {
                                                Toast.makeText(getApplicationContext(), list.get(2), Toast.LENGTH_LONG).show();
                                            }
                                            @Override
                                            public void processFail(String error) {
                                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                                            }
                                        },getApplicationContext(),c,id_U,256,pb,null);
                                        execad.execute("ServerUpdateFromGH");
//                                        byte[] bytes;
//                                        byte[] buffer = new byte[16384];
//                                        int bytesRead;
//                                        ByteArrayOutputStream output = new ByteArrayOutputStream();
//                                        try {
//                                            InputStream inputStream = getAssets().open("rgc-server");
//                                            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                                                output.write(buffer, 0, bytesRead);
//                                            }
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                            Log.i("Mdi","ERROR: " + e);
//                                        }
//                                        bytes = output.toByteArray();
//                                        final String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
//                                        pb.setVisibility(View.VISIBLE);
//                                        new Thread(new Runnable() {
//                                            public void run() {
//                                                boolean succes = true;String error="";
//                                                try{
//                                                    response = c.sendString("Server_status_code",128);
//                                                    list = new ArrayList<String>(Arrays.asList(response.split(";")));
//                                                    if(list.get(2).equals("0")){
//                                                        c.timeout = 120000;
//                                                        c.sendStringTCP("Server_update;"+encodedString,false);
//                                                        Thread.sleep(10000);
//                                                    }else{
//                                                        succes = false;
//                                                        error = "Server not running as a service";
//                                                    }
//                                                }catch (IOException e) {
//                                                    succes=false;
//                                                    error="ERROR"+e;
//                                                } catch (Exception e) {
//                                                    succes=false;
//                                                    error="ERROR"+e;
//                                                }
//                                                final String errorF = error;
//                                                MyClientTask myClientTask1 = new MyClientTask(dstName,dstAddress,dstPort,dstPass,encKey,arTime,id_U,selectedTab,"Server_status",tcpOnly);
//                                                if(succes)myClientTask1.execute();
//                                                else
//                                                runOnUiThread(new Runnable() {
//                                                    public void run() {
//                                                        Toast.makeText(getApplicationContext(), errorF, Toast.LENGTH_LONG).show();
//                                                        pb.setVisibility(View.INVISIBLE);
//                                                    }
//                                                });
//                                            }
//                                        }).start();
                                    }
                                })
                                .negativeText("Cancel")
                                .show();
                    else
                        Toast.makeText(getApplicationContext(), "Incompatible version of the server. (Automatic update from version 2.x to 3.x is not possible, check the docs !)", Toast.LENGTH_LONG).show();

                }
                else if (list.get(1).matches("Server_logs|Server_status")){
                    TextView logs = new TextView(getApplicationContext());
                    logs.setText(response);
                    MaterialDialog d = new MaterialDialog.Builder(MainActivity.this)
                            .title(list.get(1))
                            .customView(logs, true)
                            .positiveText("OK")
                            .show();
                }
            }
            super.onPostExecute(result);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (DEVELOPER_MODE) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
        //}
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        //Toast.makeText(getApplicationContext(), "V="+android.os.Build.MODEL, Toast.LENGTH_LONG).show();
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            verCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        getApplicationContext().setTheme(R.style.AppTheme);

        myDbHelper = new DataBaseHelper(getApplicationContext());
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }


        listView = findViewById(R.id.listView1);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                if(cC != null)
                    cC.cancel();
                final Cursor k = (Cursor) parent.getItemAtPosition(position);
                reconnect(k.getInt(0));
                //k.close();
//                final String serverName = k.getString(1);
//                final MyClientTask myClientTask = new MyClientTask(k.getString(1), k.getString(2), k.getInt(3), k.getString(4), k.getString(5), k.getFloat(6), k.getInt(0),k.getInt(14),"version_check");
//                myClientTask.execute();
            }
        });
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {


                final Cursor k = (Cursor) parent.getItemAtPosition(position);
                boolean tcpOnly = false;
                if(!k.isNull(17))
                    tcpOnly = (k.getInt(17)==1);
                final boolean tcpOnlyF = tcpOnly;
                //final Connection c = new Connection(k.getString(2), k.getInt(3), k.getString(4), k.getString(5), tcpOnly,k.getString(20),k.getString(21));
                final Connection c = new Connection(myDbHelper,k.getInt(0),-1,getApplicationContext());
                final PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                popup.getMenuInflater().inflate(R.menu.device_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(
                        new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.rmnotif:
                                        Cursor kn = myDbHelper.dajPowiadomienia(false,k.getInt(0));
                                        while(kn.moveToNext()){
                                            Notification nD = new Notification(kn.getInt(0),getApplicationContext());
                                            nD.cancelAlarm();
                                        }
                                        myDbHelper.usunPowiadomieniePoCID(k.getInt(0));
                                        break;
                                    case R.id.rebootServer:
                                        pb.setVisibility(View.VISIBLE);
                                        new Thread(new Runnable() {
                                            public void run() {
                                                boolean succes = true;String error = "";
                                                try{
                                                    c.sendString("Server_reboot");
                                                }catch (IOException e) {
                                                    succes=false;
                                                    error="ERROR"+e;
                                                } catch (Exception e) {
                                                    succes=false;
                                                    error="ERROR"+e;
                                                }
                                                final boolean succesF = succes;
                                                final String errorF = error;
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        if(succesF)Toast.makeText(getApplicationContext(), "Server will reboot.", Toast.LENGTH_LONG).show();
                                                        else Toast.makeText(getApplicationContext(), errorF, Toast.LENGTH_LONG).show();
                                                        pb.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                            }
                                        }).start();
                                        break;
                                    case R.id.restartServer:
                                        pb.setVisibility(View.VISIBLE);
                                        new Thread(new Runnable() {
                                            public void run() {
                                                boolean succes = true;String error = "";
                                                try{
                                                    c.sendString("Server_restart");
                                                    Thread.sleep(5000);
                                                }catch (IOException e) {
                                                    succes=false;
                                                    error="ERROR"+e;
                                                } catch (Exception e) {
                                                    succes=false;
                                                    error="ERROR"+e;
                                                }
                                                final String errorF = error;
                                                MyClientTask myClientTask1 = new MyClientTask(k.getString(1), k.getString(2), k.getInt(3), k.getString(4), k.getString(5), k.getFloat(6), k.getInt(0),k.getInt(14),"Server_status",tcpOnlyF);
                                                if(succes)myClientTask1.execute();
                                                else
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), errorF, Toast.LENGTH_LONG).show();
                                                        pb.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                            }
                                        }).start();
                                        break;
                                    case R.id.serverLogs:
                                        MyClientTask myClientTask = new MyClientTask(k.getString(1), k.getString(2), k.getInt(3), k.getString(4), k.getString(5), k.getFloat(6), k.getInt(0),k.getInt(14),"Server_logs",tcpOnlyF);
                                        myClientTask.execute();
                                        break;
                                    case R.id.connLogs:
                                        LayoutInflater factory1 = LayoutInflater.from(getApplicationContext());
                                        final View loginView1 = factory1.inflate(R.layout.errorlog_view, null);
                                        final ListView listView = (ListView) loginView1.findViewById(R.id.errorlist);
                                        myDbHelper.czyscLogi(k.getInt(0));
                                        final Cursor k2 = myDbHelper.dajLogi(k.getInt(0));
                                        if (k2.moveToNext()) {
                                            k2.moveToPrevious();
                                            new Handler().post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String[] columns = new String[]{"timestamp", "data"};
                                                    int[] to = new int[]{R.id.time, R.id.data};
                                                    ListAdapter customAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list3, k2, columns, to,0);
                                                    listView.setAdapter(customAdapter);
                                                }
                                            });
                                        }
                                        final AlertDialog d1 = new AlertDialog.Builder(MainActivity.this)
                                                .setView(loginView1)
                                                .setNegativeButton("OK", null)
                                                .create();
                                        d1.show();
                                        break;
                                    case R.id.edit:
                                            addConnection(true,k);
//                                        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
//                                        final View loginView = factory.inflate(R.layout.addconnection, null);
//                                        final int idu = k.getInt(0);
//                                        final DataBaseHelper myDbHelper = new DataBaseHelper(getApplicationContext());
//                                        TextView tv1 = (TextView) loginView.findViewById(R.id.titleL);
//                                        tv1.setText("Connection edit: ");
//                                        TextView tv2 = (TextView) loginView.findViewById(R.id.hasloL);
//                                        tv2.setText("Change password?");
//                                        final EditText name = (EditText) loginView.findViewById(R.id.name);
//                                        name.setText(k.getString(1));
//                                        final EditText ip = (EditText) loginView.findViewById(R.id.ip);
//                                        ip.setText(k.getString(2));
//                                        final EditText port = (EditText) loginView.findViewById(R.id.port);
//                                        port.setText(k.getString(3));
//                                        final EditText pass = (EditText) loginView.findViewById(R.id.password);//pass.setText(k.getString(4));
//                                        pass.setEnabled(false);
//                                        final EditText artime = (EditText) loginView.findViewById(R.id.artime);
//                                        artime.setText(k.getString(6));
//                                        final CheckBox chk = (CheckBox) loginView.findViewById(R.id.setpass);
//                                        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                            @Override
//                                            public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
//                                                if (flag)
//                                                    pass.setEnabled(true);
//                                                else {
//                                                    pass.setText("");
//                                                    pass.setEnabled(false);
//                                                }
//                                            }
//                                        });
//
//                                        final CheckBox chk2 = (CheckBox) loginView.findViewById(R.id.disablepass);
//                                        chk2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                                            public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
//                                                if (flag) {
//                                                    chk.setChecked(false);
//                                                    chk.setEnabled(false);
//                                                } else
//                                                    chk.setEnabled(true);
//                                            }
//
//                                        });
//                                        final CheckBox tab_output = (CheckBox) loginView.findViewById(R.id.GPIO_output);
//                                        final CheckBox tab_input = (CheckBox) loginView.findViewById(R.id.GPIO_input);
//                                        final CheckBox tab_pwm = (CheckBox) loginView.findViewById(R.id.GPIO_pwm);
//                                        final CheckBox tab_history = (CheckBox) loginView.findViewById(R.id.GPIO_history);
//                                        final CheckBox sensors = (CheckBox) loginView.findViewById(R.id.sensors);
//                                        final CheckBox notifications = (CheckBox) loginView.findViewById(R.id.notifications);
//                                        final CheckBox tab_ASA = (CheckBox) loginView.findViewById(R.id.GPIO_ASA);
//                                        final CheckBox tab_Chains = (CheckBox) loginView.findViewById(R.id.GPIO_chains);
//                                        final CheckBox tcpOnlyCh = (CheckBox) loginView.findViewById(R.id.tcpOnly);
//                                        final CheckBox rf = (CheckBox) loginView.findViewById(R.id.rf);
//                                        final CheckBox cmd = (CheckBox) loginView.findViewById(R.id.cmd);
//                                        tab_output.setChecked(k.getInt(7) != 0);
//                                        tab_input.setChecked(k.getInt(8) != 0);
//                                        tab_pwm.setChecked(k.getInt(9) != 0);
//                                        tab_history.setChecked(k.getInt(11) != 0);
//                                        sensors.setChecked(k.getInt(12) != 0);
//                                        notifications.setChecked(k.getInt(13) != 0);
//                                        tab_ASA.setChecked(k.getInt(15) != 0);
//                                        tab_Chains.setChecked(k.getInt(16) != 0);
//                                        tcpOnlyCh.setChecked(k.getInt(17) != 0);
//                                        rf.setChecked(k.getInt(18) != 0);
//                                        cmd.setChecked(k.getInt(19) != 0);
//                                        loginView.findViewById(R.id.tableRow4_5).setVisibility(View.VISIBLE);
//                                        final EditText ip2 = (EditText) loginView.findViewById(R.id.ip2);
//                                        ip2.setText(k.getString(20));
//                                        final EditText port2 = (EditText) loginView.findViewById(R.id.port2);
//                                        port2.setText(k.getString(21));
//                                        final AlertDialog d = new AlertDialog.Builder(MainActivity.this)
//                                                .setView(loginView)
//                                                .setPositiveButton("SAVE",
//                                                        new Dialog.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface d, int which) {
//                                                                //Do nothing here. We override the onclick
//                                                            }
//                                                        })
//                                                .setNeutralButton("DELETE",
//                                                        new Dialog.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface d, int which) {
//                                                                myDbHelper.usunUrzadzenie(idu);
//                                                                recreate();
//                                                            }
//                                                        })
//                                                .setNegativeButton("CANCEL", null)
//                                                .create();
//
//
//                                        d.setOnShowListener(new DialogInterface.OnShowListener() {
//
//                                            @Override
//                                            public void onShow(DialogInterface dialog) {
//
//                                                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
//                                                b.setOnClickListener(new View.OnClickListener() {
//
//                                                    @Override
//                                                    public void onClick(View view) {
//                                                        if (name.getText().toString().matches("") || ip.getText().toString().matches("") || port.getText().toString().matches(""))
//                                                            Toast.makeText(getApplicationContext(), "Fill Name,Ip and Port!", Toast.LENGTH_SHORT).show();
//                                                        else if (pass.getText().toString().matches("") && chk.isChecked())
//                                                            Toast.makeText(getApplicationContext(), "Fill or disable password!", Toast.LENGTH_SHORT).show();
//                                                        else if (!ip.getText().toString().matches("") && port.getText().toString().matches(""))
//                                                            Toast.makeText(getApplicationContext(), " Alternative port necessary !", Toast.LENGTH_SHORT).show();
//                                                        else {
//                                                            String passWd = pass.getText().toString();
//                                                            String encWd = "";
//                                                            if (!passWd.isEmpty()) {
//                                                                passWd = sha256(passWd);
//                                                                encWd = md5(pass.getText().toString());
//                                                            }
//                                                            if (chk.isChecked()) {
//                                                                myDbHelper.edytujUrzadzenie(idu, name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()), passWd, encWd, Float.parseFloat(artime.getText().toString()), (tab_output.isChecked()) ? 1 : 0, (tab_input.isChecked()) ? 1 : 0, (tab_pwm.isChecked()) ? 1 : 0, (tab_history.isChecked()) ? 1 : 0,(sensors.isChecked()) ? 1 : 0,(notifications.isChecked()) ? 1 : 0, (tab_ASA.isChecked()) ? 1 : 0, (tab_Chains.isChecked()) ? 1 : 0, (tcpOnlyCh.isChecked()) ? 1 : 0, (rf.isChecked()) ? 1 : 0, (cmd.isChecked()) ? 1 : 0,ip2.getText().toString(), port2.getText().toString());
//                                                            } else if (!chk.isChecked() && !chk2.isChecked()) {
//                                                                myDbHelper.edytujUrzadzenie(idu, name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()),null,null,Float.parseFloat(artime.getText().toString()), (tab_output.isChecked()) ? 1 : 0, (tab_input.isChecked()) ? 1 : 0, (tab_pwm.isChecked()) ? 1 : 0, (tab_history.isChecked()) ? 1 : 0,(sensors.isChecked()) ? 1 : 0,(notifications.isChecked()) ? 1 : 0, (tab_ASA.isChecked()) ? 1 : 0, (tab_Chains.isChecked()) ? 1 : 0, (tcpOnlyCh.isChecked()) ? 1 : 0, (rf.isChecked()) ? 1 : 0, (cmd.isChecked()) ? 1 : 0,ip2.getText().toString(), port2.getText().toString());
//                                                            } else if (chk2.isChecked()) {
//                                                                myDbHelper.edytujUrzadzenie(idu, name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()), "", "", Float.parseFloat(artime.getText().toString()), (tab_output.isChecked()) ? 1 : 0, (tab_input.isChecked()) ? 1 : 0, (tab_pwm.isChecked()) ? 1 : 0,  (tab_history.isChecked()) ? 1 : 0,(sensors.isChecked()) ? 1 : 0,(notifications.isChecked()) ? 1 : 0, (tab_ASA.isChecked()) ? 1 : 0, (tab_Chains.isChecked()) ? 1 : 0, (tcpOnlyCh.isChecked()) ? 1 : 0, (rf.isChecked()) ? 1 : 0, (cmd.isChecked()) ? 1 : 0,ip2.getText().toString(), port2.getText().toString());
//                                                            }
//                                                            recreate();
//                                                            d.dismiss();
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        });
//                                        d.show();
                                        break;
                                }
                                return true;
                            }
                        });
                popup.show();

                return true;
            }
        });
        final Cursor k = myDbHelper.dajUrzadzenia();
        Button b1 = (Button) findViewById(R.id.btn);
        if (k.moveToNext()) {
            k.moveToPrevious();
            b1.setVisibility(View.GONE);

            String[] columns = new String[]{"nazwa", "ip", "port","port"};
            int[] to = new int[]{R.id.nazwa, R.id.ip, R.id.port, R.id.list_item_image};
            customAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list1, k, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            customAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor c, int columnIndex) {
                    if (view.getId() == R.id.list_item_image) {
                        final ImageView image = (ImageView) view;
                        //image.setImageResource(R.drawable.red);
                        //Toast.makeText(getApplicationContext(), "check!", Toast.LENGTH_SHORT).show();
                        //Connection cQuick = new Connection(c.getString(2), c.getInt(3), c.getString(4), c.getString(5), false,2000,c.getString(20),c.getString(21));
                        Connection cQuick = new Connection(myDbHelper,c.getInt(0),2000,getApplicationContext());
                        GetAsyncData execad = new GetAsyncData(new GetAsyncData.AsyncResponse() {
                            @Override
                            public void processFinish(List<String> list) {
                                image.setImageResource(R.drawable.green);
                            }
                            @Override
                            public void processFail(String error) {
                                image.setImageResource(R.drawable.red);
                            }
                        },getApplicationContext(),cQuick,c.getInt(0),256,null,null);
                        execad.execute("version_check");
                        return true;
                    }
                    return false;
                }
            });
            listView.setAdapter(customAdapter);
        } else b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addConnection(false,null);
            }
        });
        //checkIfUp();
        reconnect(getIntent().getIntExtra("ID_U",-1));
        myDbHelper.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        MenuItem actionViewItem = menu.findItem(R.id.miActionButton);
        View v = MenuItemCompat.getActionView(actionViewItem);
        pb = (ProgressBar) v.findViewById(R.id.pbProgressAction);
        pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cC.cancel();
            }
        });
        Button b = (Button) v.findViewById(R.id.btnCustomAction);
        Button r = (Button) v.findViewById(R.id.btnCustomAction3);
        r.setVisibility(View.GONE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addConnection(false,null);
            }
        });
        pb.setVisibility(View.INVISIBLE);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        if (customAdapter != null)
            customAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        reconnect(intent.getIntExtra("ID_U",-1));
        setIntent(intent);
    }
    private void reconnect(int id_u){
        if(id_u != -1){
            Cursor kU = myDbHelper.dajUrzadzenie(id_u);
            boolean tcpOnly = false;
            if(kU.moveToFirst()){
                if(!kU.isNull(17))
                    tcpOnly = (kU.getInt(17)==1);
                MyClientTask myClientTask = new MyClientTask(kU.getString(1), kU.getString(2), kU.getInt(3), kU.getString(4), kU.getString(5), kU.getFloat(6), kU.getInt(0),kU.getInt(14),"version_check",tcpOnly);
                myClientTask.execute();
            }
        }
    }


}
