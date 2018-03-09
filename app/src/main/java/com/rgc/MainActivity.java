package com.rgc;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
	MenuItem miActionProgressItem;
	ProgressBar pb;
	int verCode = 2;
	public void addConnection(){
		
		LayoutInflater factory = LayoutInflater.from(this);
		final View loginView = factory.inflate(R.layout.addconnection, null);
		final EditText name = (EditText) loginView.findViewById(R.id.name);
		final EditText ip = (EditText) loginView.findViewById(R.id.ip);
		final EditText port = (EditText) loginView.findViewById(R.id.port);
		final EditText pass = (EditText) loginView.findViewById(R.id.password);
		pass.setEnabled(false);
		final EditText artime = (EditText) loginView.findViewById(R.id.artime);
		final CheckBox chk = (CheckBox)loginView.findViewById(R.id.setpass);
		final CheckBox tab_output = (CheckBox)loginView.findViewById(R.id.GPIO_output);
		final CheckBox tab_input = (CheckBox)loginView.findViewById(R.id.GPIO_input);
		final CheckBox tab_pwm = (CheckBox)loginView.findViewById(R.id.GPIO_pwm);
		final CheckBox tab_SA = (CheckBox)loginView.findViewById(R.id.GPIO_SA);
		final CheckBox tab_history = (CheckBox)loginView.findViewById(R.id.GPIO_history);
		tab_SA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
			{
				if (flag)
				{
					tab_output.setChecked(true);
					tab_output.setEnabled(false);
				} else
				{
					tab_output.setEnabled(true);
				}
			}
		});
		chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
			{
				if (flag)
				{
					pass.setEnabled(true);
				} else
				{
					pass.setText("");
					pass.setEnabled(false);
				}
			}
		});
		final AlertDialog d = new AlertDialog.Builder(MainActivity.this)
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
         	        	if(name.getText().toString().matches("")||ip.getText().toString().matches("")||port.getText().toString().matches("")||artime.getText().toString().matches(""))
         	        		Toast.makeText(getApplicationContext(), "Fill in the required fields!", Toast.LENGTH_SHORT).show();
						else if (pass.getText().toString().matches("") && chk.isChecked())
						Toast.makeText(getApplicationContext(), "Fill or disable password!", Toast.LENGTH_SHORT).show();
         	        	else{
         	        	DataBaseHelper myDbHelper = new DataBaseHelper(getApplicationContext());
         	        	String passWd = pass.getText().toString();
         	        	String encWd = "";
         	        	if (passWd.isEmpty() == false){
         	        		passWd = sha256(passWd);
         	        		encWd = md5(pass.getText().toString());
         	        	}
         	        	myDbHelper.dodajUrzadzenie(name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()), passWd, encWd, Float.parseFloat(artime.getText().toString()),(tab_output.isChecked()) ? 1 : 0,(tab_input.isChecked()) ? 1 : 0,(tab_pwm.isChecked()) ? 1 : 0,(tab_SA.isChecked()) ? 1 : 0,(tab_history.isChecked()) ? 1 : 0 );
         	        	Toast.makeText(getApplicationContext(), "Added: "+name.getText().toString(), Toast.LENGTH_SHORT).show();
         	        	recreate();
                        d.dismiss();}
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
    Log.d("mdi9", sb.toString().toLowerCase());
    return sb.toString().toLowerCase();
	    } catch (Exception exc) {
	        return "";
	    }
	}
	public class MyClientTask extends AsyncTask<Void, Void, Boolean> {
		  
			String dstName;
		  String dstAddress;
		  String dstPass,encKey;
		  int dstPort,id_U;
		  float arTime;
		  String response = "";
		  boolean succes;
		  boolean passwd;
		  List<String> list;
		  
		 MyClientTask(String name, String addr, int port, String pass, String enc_key,float artime, int id_u){
			dstName = name;
		   dstAddress = addr;
		   dstPort = port;
		   dstPass = pass;
		   encKey = enc_key;
		   arTime = artime;
			 id_U = id_u;

		  }
		 
		 @Override
		  protected void onPreExecute() {
			 pb.setVisibility(View.VISIBLE);
		   super.onPreExecute();
		  }

		  @Override
		  protected Boolean doInBackground(Void... arg0) {
			  
			  try {
				  Connection c = new Connection();
				 response = c.sendString(dstAddress, dstPort, dstPass,"version_check",1024,encKey);
				 list = new ArrayList<String>(Arrays.asList(response.split(";")));
				 if(list.get(0).equals("true"))passwd = true;
				 else if(list.get(0).equals("false"))passwd = false;
				 succes = true;
			    } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response = "ERROR: "+ e;
					succes = false;
				}        	
				 catch (Exception e) {
			        e.printStackTrace();
			        response = "ERROR: "+ e;
			        succes = false;
			    }
				 
		   return succes;
		  }

		  @Override
		  protected void onPostExecute(Boolean result) {
			  if (result == true && passwd == false)
				  if (list.get(1).equals("Conection OK, but no compabile method found, probably encryption error"))
			  Toast.makeText(getApplicationContext(), list.get(1)+ " or server update is needed", Toast.LENGTH_LONG).show();
				  else Toast.makeText(getApplicationContext(), list.get(1), Toast.LENGTH_LONG).show();
			  else if (result == false) Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
			  pb.setVisibility(View.INVISIBLE);
			  if (result == true && passwd == true){
				  if (list.get(1).equals("version_check") && verCode == Integer.parseInt(list.get(2))){
				  Intent i = new Intent(MainActivity.this,PagerTabStripActivity.class);
				  i.putExtra("nazwa", dstName);i.putExtra("ip", dstAddress);i.putExtra("port", dstPort);i.putExtra("password", dstPass);
				  int temp = Math.round(arTime*1000);i.putExtra("id_u", id_U);
				  i.putExtra("enc_key", encKey);i.putExtra("artime", temp);
				  startActivity(i);}
				  else Toast.makeText(getApplicationContext(), "Incompatible version of the server.", Toast.LENGTH_LONG).show();
			  }
		   super.onPostExecute(result);
		  }

		 }
	 
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		
		final DataBaseHelper myDbHelper = new DataBaseHelper(getApplicationContext());
        try {
        	myDbHelper.createDataBase();
  	} catch (IOException ioe) { 
  		throw new Error("Unable to create database");
  	}
  	try {	 
  		myDbHelper.openDataBase();
  	}catch(SQLException sqle){
  		throw sqle;
  	}
  	
  	
		final ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
				// TODO Auto-generated method stub
        		
        		
        		
        		final Cursor k = (Cursor) parent.getItemAtPosition(position);
        		MyClientTask myClientTask = new MyClientTask(k.getString(1),k.getString(2),k.getInt(3),k.getString(4),k.getString(5),k.getFloat(6),k.getInt(0));
        		  myClientTask.execute();
        
        			  
			}
        });
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


        		LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        		final View loginView = factory.inflate(R.layout.addconnection, null);
        		final Cursor k = (Cursor) parent.getItemAtPosition(position);final int idu = k.getInt(0);
        		final DataBaseHelper myDbHelper = new DataBaseHelper(getApplicationContext());
        		TextView tv1 = (TextView) loginView.findViewById(R.id.titleL);tv1.setText("Connection edit: ");
        		TextView tv2 = (TextView) loginView.findViewById(R.id.hasloL);tv2.setText("Change password?");
 	        	final EditText name = (EditText) loginView.findViewById(R.id.name);name.setText(k.getString(1));
 	        	final EditText ip = (EditText) loginView.findViewById(R.id.ip);ip.setText(k.getString(2));
 	        	final EditText port = (EditText) loginView.findViewById(R.id.port);port.setText(k.getString(3));
 	        	final EditText pass = (EditText) loginView.findViewById(R.id.password);//pass.setText(k.getString(4));
				pass.setEnabled(false);
 	        	final EditText artime = (EditText) loginView.findViewById(R.id.artime);artime.setText(k.getString(6));
				final CheckBox chk = (CheckBox)loginView.findViewById(R.id.setpass);
				chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
					{
						if (flag)
						pass.setEnabled(true);
						 else
						{
							pass.setText("");
							pass.setEnabled(false);
						}
					}
				});

				final CheckBox chk2 = (CheckBox)loginView.findViewById(R.id.disablepass);
				chk2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
					{
						if (flag)
						{
							chk.setChecked(false);
							chk.setEnabled(false);
						} else
						chk.setEnabled(true);
					}

				});
				final CheckBox tab_output = (CheckBox)loginView.findViewById(R.id.GPIO_output);
				final CheckBox tab_input = (CheckBox)loginView.findViewById(R.id.GPIO_input);
				final CheckBox tab_pwm = (CheckBox)loginView.findViewById(R.id.GPIO_pwm);
				final CheckBox tab_SA = (CheckBox)loginView.findViewById(R.id.GPIO_SA);
				final CheckBox tab_history = (CheckBox)loginView.findViewById(R.id.GPIO_history);
				tab_SA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
					{
						if (flag)
						{
							tab_output.setChecked(true);
							tab_output.setEnabled(false);
						} else
						{
							tab_output.setEnabled(true);
						}
					}
				});
				tab_output.setChecked(k.getInt(7) != 0);
				tab_input.setChecked(k.getInt(8) != 0);
				tab_pwm.setChecked(k.getInt(9) != 0);
				tab_SA.setChecked(k.getInt(10) != 0);
				tab_history.setChecked(k.getInt(11) != 0);
				((TableRow)loginView.findViewById(R.id.tableRow4_5)).setVisibility(View.VISIBLE);

        		final AlertDialog d = new AlertDialog.Builder(MainActivity.this)
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
                                    	myDbHelper.usunUrzadzenie(idu);
                          	        	recreate();
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
                 	        	if(name.getText().toString().matches("")||ip.getText().toString().matches("")||port.getText().toString().matches(""))
									Toast.makeText(getApplicationContext(), "Fill Name,Ip and Port!", Toast.LENGTH_SHORT).show();
								else if (pass.getText().toString().matches("") && chk.isChecked())
								Toast.makeText(getApplicationContext(), "Fill or disable password!", Toast.LENGTH_SHORT).show();
                 	        	else{
                 	        		String passWd = pass.getText().toString();
                     	        	String encWd = "";
                     	        	if (passWd.isEmpty() == false){
                     	        		passWd = sha256(passWd);
                     	        		encWd = md5(pass.getText().toString());
                     	        	}
									if (chk.isChecked()) {
										myDbHelper.edytujUrzadzenie(idu,name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()),passWd, encWd,Float.parseFloat(artime.getText().toString()),(tab_output.isChecked()) ? 1 : 0,(tab_input.isChecked()) ? 1 : 0,(tab_pwm.isChecked()) ? 1 : 0,(tab_SA.isChecked()) ? 1 : 0,(tab_history.isChecked()) ? 1 : 0);
									} else if (!chk.isChecked() && !chk2.isChecked()) {
										myDbHelper.edytujUrzadzenie(idu, name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()), Float.parseFloat(artime.getText().toString()),(tab_output.isChecked()) ? 1 : 0,(tab_input.isChecked()) ? 1 : 0,(tab_pwm.isChecked()) ? 1 : 0,(tab_SA.isChecked()) ? 1 : 0,(tab_history.isChecked()) ? 1 : 0);
									} else if (chk2.isChecked()) {
										myDbHelper.edytujUrzadzenie(idu,name.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()),"", "",Float.parseFloat(artime.getText().toString()),(tab_output.isChecked()) ? 1 : 0,(tab_input.isChecked()) ? 1 : 0,(tab_pwm.isChecked()) ? 1 : 0,(tab_SA.isChecked()) ? 1 : 0,(tab_history.isChecked()) ? 1 : 0);
									}

                     	        	
                     	        	recreate();
                                d.dismiss();}
                            }
                        });
                    }
                });
                d.show();
        		return true;
			}
        });
        final Cursor k = myDbHelper.dajUrzadzenia();
        Button b1 = (Button) findViewById(R.id.btn);
        if (k.moveToNext()){ k.moveToPrevious();
        b1.setVisibility(View.GONE);
		new Handler().post(new Runnable() {
            @Override
            public void run() {
            	
            	String[] columns = new String[] {"nazwa", "ip", "port" };
                int[] to = new int[] {R.id.nazwa, R.id.ip, R.id.port};
                ListAdapter customAdapter = new SimpleCursorAdapter(getApplicationContext(),R.layout.list1,k,columns,to);
                listView.setAdapter(customAdapter);
            }
        });
        }else b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	addConnection();
            }
        });
        
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
		pb =  (ProgressBar) v.findViewById(R.id.pbProgressAction);
		Button b = (Button) v.findViewById(R.id.btnCustomAction);
		Button r = (Button) v.findViewById(R.id.btnCustomAction3);
		r.setVisibility(View.GONE);
		b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	addConnection();
            }
        });
		pb.setVisibility(View.INVISIBLE);
		pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
	   
	        return super.onPrepareOptionsMenu(menu);
	    }
	
	
}
