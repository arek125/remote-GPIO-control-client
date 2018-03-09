package com.rgc;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class GPIO_Pwm extends Fragment {
	static String dstName,dstAddress,dstPassword,encKey;
	static int Page,dstPort,arTime;
    static ProgressBar pb;
    static boolean menuCreated = false;
	Handler handler = new Handler();
	boolean handlerOn = false,VisibleToUser = true;
	static boolean running=false;
	Runnable runabble = null;
	static GridView gridView;static Button r; static DataBaseHelper myDbHelper; static int id_U;
	public static Context mContext;
	private static ArrayList<String> idki = new ArrayList<String>();
    private static ArrayList<String> gpios = new ArrayList<String>();
	private static ArrayList<String> gpios_in = new ArrayList<String>();
	private static ArrayList<String> gpios_out = new ArrayList<String>();
    private static ArrayList<String> s_s = new ArrayList<String>();
    private static ArrayList<String> nazwy = new ArrayList<String>();
    private static ArrayList<String> dc = new ArrayList<String>();
    private static ArrayList<String> fr = new ArrayList<String>();
    private static ArrayList<String> reverses = new ArrayList<String>();
	static Date edittime = new Date(1);
    public GPIO_Pwm() {
    }

    public static GPIO_Pwm newInstance(int page,String name, String address, int port, String password,Context Context, String enc_key,int artime, int id_u) {
        GPIO_Pwm fragment = new GPIO_Pwm();
        Page = page;
        dstName = name;
        dstAddress = address;
        dstPort = port;
        dstPassword = password;
        mContext = Context;
  	   encKey = enc_key;
  	   arTime= artime;id_U = id_u;
        return fragment;
    }
    
    public interface AsyncResponse {
        void processFinish(String output);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gpio_pwm, container, false);
        setHasOptionsMenu(true);
		myDbHelper = new DataBaseHelper(mContext);
        gridView = (GridView) rootView.findViewById(R.id.gridView1);
       
        return rootView;
    }

    
	public static void check_state(){
		if (running == false){
			GPIO_PwmTask edittimecheck = new GPIO_PwmTask(new AsyncResponse(){
		    @Override
			public void processFinish(String output){
		     }});
		edittimecheck.execute("GPIO_PEtime");

		}
	}
	public static void list_update(){
		  GPIO_PwmTask GPIO_ListGet = new GPIO_PwmTask(new AsyncResponse(){
			    @Override
				public void processFinish(String output){
			    	
			     }});
			GPIO_ListGet.execute("GPIO_Plist");
	}
	
	public static Date StringtoDate(String dtStart){
		  
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
	
	public static String DatetoString(Date date){
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
    
	public static class GPIO_PwmTask extends AsyncTask<String, String, Boolean> {
		  

		  public AsyncResponse delegate = null;

		    public GPIO_PwmTask(AsyncResponse delegate){
		        this.delegate = delegate;
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
			  Connection c = new Connection();
			  if(params[0].equals("GPIO_PEtime")) response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";",256,encKey);
			  else if(params[0].equals("GPIO_Plist"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";",16384,encKey);
			  else if(params[0].equals("GPIO_PDC"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2]+";"+params[3],128,encKey);
			  else if(params[0].equals("GPIO_PDCu"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2]+";"+params[3]+";"+DatetoString(new Date())+";"+android.os.Build.MODEL,512,encKey);
			  else if(params[0].equals("GPIO_PFRDC"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2]+";"+params[3]+";"+params[4]+";"+DatetoString(new Date())+";"+android.os.Build.MODEL,512,encKey);
			  else if(params[0].equals("GPIO_PSS"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2]+";"+params[3]+";"+DatetoString(new Date())+";"+params[4]+";"+params[5]+";"+android.os.Build.MODEL,512,encKey);
			  else if(params[0].equals("Add_GPIO_pwm"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2]+";"+params[3]+";"+params[4]+";"+params[5]+";"+params[6]+";"+android.os.Build.MODEL,1024,encKey);
			  else if(params[0].equals("Edit_GPIO_pwm"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2]+";"+params[3]+";"+params[4]+";"+params[5]+";"+params[6]+";"+params[7]+";"+DatetoString(new Date())+";"+android.os.Build.MODEL,1024,encKey);
			  else if(params[0].equals("Delete_GPIO_pwm"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2]+";"+params[3]+";"+android.os.Build.MODEL,256,encKey);
			  else if(params[0].equals("Allpins_GPIO_out"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";",1024,encKey);
			  else if(params[0].equals("Allpins_GPIO_in"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";",1024,encKey);
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
		  	catch (IndexOutOfBoundsException e) {
		  		response = "ERROR: "+ e;
		    }
			 catch (Exception e) {
		        e.printStackTrace();
		        response = "ERROR: "+ e;
		        succes = false;
		    } 
		  if (menuCreated == false) cancel(true);
			 
	   return succes;
	  }

	  @Override
	  protected void onPostExecute(Boolean result) {
		  running = false;
		  if (result == true && passwd == true){r.setTextColor(Color.GREEN);
			  if (list.get(1).equals("GPIO_PEtime")){
				  if(!list.get(2).equals("None")){
				  Date date = StringtoDate(list.get(2));
				  if (!date.equals(edittime)){
					list_update();
						edittime = date;
				  }
				  }
				  else gridView.setAdapter(null);
		  }
			  else if (list.get(1).equals("GPIO_Plist")){
				  idki.clear();gpios.clear();s_s.clear();nazwy.clear();reverses.clear();fr.clear();dc.clear();
				  for(int j=2;j<(list.size()-1);j=j+7){
					  idki.add(list.get(j));
					  gpios.add(list.get(j+1));
					  fr.add(list.get(j+2));
					  dc.add(list.get(j+3));
					  s_s.add(list.get(j+4));
					  nazwy.add(list.get(j+5));
					  reverses.add(list.get(j+6));
					  if(j>1000)break;
				  }
				  gridView.setAdapter(  new CustomGridAdapter2( mContext,idki ,gpios, fr, dc, s_s, nazwy, reverses ) );
			  		}
			  else if (list.get(1).equals("GPIO_PDC")){
				  
				  delegate.processFinish(list.get(2));
			  }
			  else if (list.get(1).equals("GPIO_PDCu")){
				  edittime= StringtoDate(list.get(3));
				  
			  }
			  else if (list.get(1).equals("GPIO_PSS")||list.get(1).equals("GPIO_PFRDC")){
				  edittime= StringtoDate(list.get(3));
				  delegate.processFinish(list.get(2)+";"+list.get(4));
			  }
			  else if (list.get(1).equals("Add_GPIO_pwm")||list.get(1).equals("Edit_GPIO_pwm")||list.get(1).equals("Delete_GPIO_pwm")){
				  edittime = new Date(1);
				  check_state();
			  }
			  else if (list.get(1).equals("Allpins_GPIO_out")) {
				  gpios_out.clear();
				  String pins = "";
				  for (int j = 2; j < (list.size() - 1); j++) {
					  GPIO_Pwm.gpios_out.add(list.get(j));
					  pins = pins + (list.get(j)+",");
				  }
				  this.delegate.processFinish(pins);
			  } else if (list.get(1).equals("Allpins_GPIO_in")) {
				  gpios_in.clear();
				   String pins = "";
				  for (int j = 2; j < (list.size() - 1); j++) {
					  gpios_in.add(list.get(j));
					  pins = pins + (list.get(j)+",");
				  }
				  this.delegate.processFinish(pins);
			  }
			  }
		  else if (result == false || passwd == false) {
			  if (result == false) {myDbHelper.dodajLog(id_U,response);r.setTextColor(Color.RED);}
			  else if (passwd == false) {myDbHelper.dodajLog(id_U,list.get(1));r.setTextColor(Color.RED);}
		  }
		 
		  pb.setVisibility(View.INVISIBLE);
		//  hendlerm();
		  
	   super.onPostExecute(result);
	  }
	  
	 }
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
	    // Do something that differs the Activity's menu here
		MenuItem actionViewItem = menu.findItem(R.id.miActionButton);
		View v = MenuItemCompat.getActionView(actionViewItem);
		pb =  (ProgressBar) v.findViewById(R.id.pbProgressAction);
		r = (Button) v.findViewById(R.id.btnCustomAction3);
		r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				  edittime = new Date(1);
				  check_state();
            }
        });
		r.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				LayoutInflater factory = LayoutInflater.from(mContext);
				final View loginView = factory.inflate(R.layout.errorlog_view, null);
				final ListView listView = (ListView) loginView.findViewById(R.id.errorlist);
				myDbHelper.czyscLogi(id_U);
				final Cursor k = myDbHelper.dajLogi(id_U);
				if (k.moveToNext()){ k.moveToPrevious();
					new Handler().post(new Runnable() {
						@Override
						public void run() {
							String[] columns = new String[] {"timestamp", "data" };
							int[] to = new int[] {R.id.time, R.id.data};
							ListAdapter customAdapter = new SimpleCursorAdapter(mContext,R.layout.list3,k,columns,to);
							listView.setAdapter(customAdapter);
						}
					});
				}
				final AlertDialog d = new AlertDialog.Builder(mContext)
						.setView(loginView)
						.setNegativeButton("OK", null)
						.create();
				d.show();
				return false;
			}
		});
		Button b = (Button) v.findViewById(R.id.btnCustomAction);
		b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	LayoutInflater factory = LayoutInflater.from(mContext);
        		final View loginView = factory.inflate(R.layout.addpwm, null);
				GPIO_PwmTask Allpins_GPIO_out = new GPIO_PwmTask(new AsyncResponse(){
					@Override
					public void processFinish(String output){}});
				Allpins_GPIO_out.execute("Allpins_GPIO_out");
				GPIO_PwmTask Allpins_GPIO_in = new GPIO_PwmTask(new AsyncResponse(){
					@Override
					public void processFinish(String output){}});
				Allpins_GPIO_in.execute("Allpins_GPIO_in");
                
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
                 	        	EditText fr = (EditText) loginView.findViewById(R.id.fr);
                 	        	EditText dc = (EditText) loginView.findViewById(R.id.dc);
                 	        	CheckBox reverse = (CheckBox) loginView.findViewById(R.id.reverse);
                 	        	String reverseS = "0";if (reverse.isChecked())reverseS = "1";
								boolean cont = false;
								boolean notbcm = false;
								for (String temp : new ArrayList<String>(Arrays.asList(gpio.getText().toString().split(",")))) {
									if (!gpios_out.contains(temp) && !gpios_in.contains(temp)) {
										Iterator it = gpios.iterator();
										while (it.hasNext()) {
											if (!new ArrayList<String>(Arrays.asList(((String) it.next()).split(","))).contains(temp)) {
												if (!temp.matches("2|3|4|17|27|22|10|9|11|5|6|13|19|26|14|15|18|23|24|25|8|7|12|16|20|21")) {
													notbcm = true;
													break;
												}
											}
											else{
											cont = true;
											break;}
										}
									}
									else {
									cont = true;
									break;}
								}
                 	        	if(name.getText().toString().matches("")||gpio.getText().toString().matches("")||fr.getText().toString().matches("")||dc.getText().toString().matches(""))
                 	        		Toast.makeText(mContext, "Fill Name, GPIO, DC and f!", Toast.LENGTH_SHORT).show();
								else if (cont)
									Toast.makeText(mContext, "Pin/s alredy in use!", Toast.LENGTH_SHORT).show();
								else if (notbcm)
									Toast.makeText(mContext, "Not GPIO BCM!", Toast.LENGTH_SHORT).show();
                 	        	else if (Integer.parseInt(dc.getText().toString()) > 100 || Integer.parseInt(dc.getText().toString()) < 0)
                 	        			Toast.makeText(mContext, "DC must bo beetwen 0 and 100!", Toast.LENGTH_SHORT).show();
                 	        	else{
                 	        		GPIO_PwmTask Add_Type = new GPIO_PwmTask(new AsyncResponse(){
                 	   		    @Override
                 	   			public void processFinish(String output){
                 	   		    	
                 	   		     }});
                 	       	Add_Type.execute("Add_GPIO_pwm", gpio.getText().toString(),fr.getText().toString(),dc.getText().toString(),name.getText().toString(),reverseS,DatetoString(new Date()));

                                d.dismiss();}
                            }
                        });
                    }
                });
                d.show();
            	
            }
        });
		pb.setVisibility(View.INVISIBLE);
		pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
		menuCreated=true;
		edittime = new Date(1);
    	running = false;
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
		if (menuCreated && VisibleToUser) {hendlerm();
			handlerOn = true;}
		super.onStop();
	}
	 
	 @Override
	 public void setUserVisibleHint(boolean isVisibleToUser) {
	     super.setUserVisibleHint(isVisibleToUser);
		 VisibleToUser = isVisibleToUser;
	     if (isVisibleToUser) {
	     }else{
	    	 handler.removeCallbacks(runabble);
	    	 handlerOn = false;
	    	
	     }
	 }
	 

	 
	 public void hendlerm(){
		 
		 if(handlerOn==false && menuCreated==true && arTime > 0){
				runabble = new Runnable() {
				    public void run() {
				    	if (menuCreated==true){
				    	check_state();
				        handler.postDelayed(this, arTime);} //now is every 11 sec (10 sec timeout)
				    }
				 }; 
				
				 handler.postDelayed(runabble, arTime);
				 handlerOn = true;}
	 }
}