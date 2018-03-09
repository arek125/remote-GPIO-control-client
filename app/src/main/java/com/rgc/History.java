package com.rgc;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;


public class History extends Fragment {
	static String dstName,dstAddress,dstPassword,encKey;
	static int Page,dstPort,arTime;
	int Records = 0;
	ArrayList<String> id = new ArrayList<String>();
	ArrayList<String> data = new ArrayList<String>();
	ArrayList<String> typ = new ArrayList<String>();
	ArrayList<String> nazwyN = new ArrayList<String>();
	ArrayList<String> state = new ArrayList<String>();
	//ArrayList<String> nazwy = new ArrayList<String>();
    ProgressBar pb;
    boolean menuCreated = false,VisibleToUser = true,running;
	boolean handlerOn = false;Button r;DataBaseHelper myDbHelper; static int id_U;
    ListView listview;final Calendar cfrom = Calendar.getInstance();final Calendar cto = Calendar.getInstance();
    private SimpleAdapter adapter;
	Handler handler = new Handler();
	Runnable runabble = null;
	String datefrom,dateto;
	public static Context mContext;
    public History() {
    }

    public static History newInstance(int page,String name, String address, int port, String password,Context Context, String enc_key,int artime) {
    	History fragment = new History();
        Page = page;
        dstName = name;
        dstAddress = address;
        dstPort = port;
        dstPassword = password;
   	   encKey = enc_key;
   	   arTime= artime;
        mContext = Context;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduled_actions, container, false);
		myDbHelper = new DataBaseHelper(mContext);
        listview =  (ListView)rootView.findViewById(R.id.listView2);
        setHasOptionsMenu(true);

        
        
        return rootView;
    }
    
	public void check_state(){
		if (running == false){
		HistoryTask myClientTask2_1 = new HistoryTask();
    	myClientTask2_1.execute("HR_count",datefrom,dateto);}
	}
    
	public class HistoryTask extends AsyncTask<String, Void, Boolean> {
		  
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
			  Connection c = new Connection();
			  if(params[0].equals("HR_count")) response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2],256,encKey);
			  else if(params[0].equals("HR_sel")) response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2]+";"+params[3],32384,encKey);
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

		  if (result == true && passwd == true){
			  r.setTextColor(Color.GREEN);
			  if (list.get(1).equals("HR_count")){
			      records = Integer.parseInt(list.get(2));
			  if (records != Records){
				  id.clear();data.clear();nazwyN.clear();state.clear();typ.clear();listview.setAdapter(null);
				  HistoryTask myClientTask2_2 = new HistoryTask();
			    	myClientTask2_2.execute("HR_sel",datefrom,dateto, String.valueOf(records));

				 
				  
			  }
			  Records = records;
			 
	  }

			  

		      else  if (list.get(1).equals("HR_sel")){
		    	  records = Integer.parseInt(list.get(2));

				  final SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				  final SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

						  for(int j=3;j<(list.size()-1);j=j+5){
						  id.add(list.get(j));
							  Date parsed = null; // => Date is in UTC now
							  try {
								  parsed = sourceFormat.parse(list.get(j+1));
							  } catch (ParseException e) {
								  e.printStackTrace();
							  }
						  data.add(destFormat.format(parsed));
						  typ.add("By: "+list.get(j+2));
						  nazwyN.add(list.get(j+3).matches("None") ? " " : list.get(j+3));
						  state.add(list.get(j+4));
						  if (j > 2000)break;
						  }

					  String[] from = new String[] {"dane","nazwa","stan","typ"};
					  int[] to = new int[] {R.id.dane, R.id.rodzaj, R.id.stan, R.id.warunek};
					   
					  List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

					  String[] dataA = new String[data.size()];
					  dataA = data.toArray(dataA);
					  String[] nazwyNA = new String[nazwyN.size()];
					  nazwyNA = nazwyN.toArray(nazwyNA);
					  String[] stateA = new String[state.size()];
					  stateA = state.toArray(stateA); 
					  String[] typA = new String[state.size()];
					  typA = typ.toArray(typA); 
					  
					  for(int i = 0; i < dataA.length; i++){
					  HashMap<String, String> map = new HashMap<String, String>();
					  map.put("dane", "" + dataA[i]);
					  map.put("nazwa", "" + nazwyNA[i]);
					  map.put("stan", "" + stateA[i]);
					  map.put("typ", "" + typA[i]);
					  fillMaps.add(map);
					  }
					  adapter = new SimpleAdapter(mContext, fillMaps, R.layout.list2, from, to);
					  listview.setAdapter(adapter);
					  
				  }
			  }
		  else if (result == false || passwd == false) {
			  if (result == false) {myDbHelper.dodajLog(id_U,response);r.setTextColor(Color.RED);}
			  else if (passwd == false) {myDbHelper.dodajLog(id_U,list.get(1));r.setTextColor(Color.RED);}
		  }
		  pb.setVisibility(View.INVISIBLE);running = false;
	   super.onPostExecute(result);
	  }
	  
	 }
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		MenuItem actionViewItem = menu.findItem(R.id.miActionButton);
		View v = MenuItemCompat.getActionView(actionViewItem);
		pb =  (ProgressBar) v.findViewById(R.id.pbProgressAction);
		Button b = (Button) v.findViewById(R.id.btnCustomAction);
		Button b2 = (Button) v.findViewById(R.id.btnCustomAction2);
		b.setVisibility(View.GONE);
		b2.setVisibility(View.VISIBLE);
		r = (Button) v.findViewById(R.id.btnCustomAction3);
		r.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
		pb.setVisibility(View.INVISIBLE);
		pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
    	Records = 0;
        //final Calendar c = Calendar.getInstance();
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
        cto.add(Calendar.DAY_OF_MONTH, +1);
        dateto = df.format(cto.getTime());
        cfrom.add(Calendar.DAY_OF_MONTH, -8);
        datefrom = df.format(cfrom.getTime());
		b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		LayoutInflater factory = LayoutInflater.from(mContext);
        		final View loginView = factory.inflate(R.layout.daterange, null);
        		final DatePicker dfrom = (DatePicker) loginView.findViewById(R.id.datePickerFrom);
        		dfrom.updateDate(cfrom.get(Calendar.YEAR), cfrom.get(Calendar.MONTH), cfrom.get(Calendar.DAY_OF_MONTH));
        		final DatePicker dto = (DatePicker) loginView.findViewById(R.id.datePickerTo);
				dto.updateDate(cto.get(Calendar.YEAR), cto.get(Calendar.MONTH), cto.get(Calendar.DAY_OF_MONTH));
        		final AlertDialog d = new AlertDialog.Builder(getActivity().getWindow().getContext())
                        .setView(loginView)
                        .setPositiveButton("SET DATE RANGE",
                                new Dialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface d, int which) {
										//Calendar calendar = Calendar.getInstance();
										cfrom.set(dfrom.getYear(), dfrom.getMonth(), dfrom.getDayOfMonth());
                                        datefrom =df.format(cfrom.getTime());
										cto.set(dto.getYear(), dto.getMonth(), dto.getDayOfMonth()+1);
                                        dateto = df.format(cto.getTime());
                                        
                                        Records =0;
                                        check_state();
                                    }
                                })
                        .setNegativeButton("CANCEL", null)
                        .create();
        		
        		
            	d.show();
            }
        });
		menuCreated=true;
		running = false;
		check_state();
		hendlerm2();
	    super.onCreateOptionsMenu(menu, inflater);
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

	@Override
	public void onStop() {
		handler.removeCallbacks(runabble);
		handlerOn = false;
		//menuCreated=false;
		super.onStop();
	}
	@Override
	public void onResume() {
		if (menuCreated && VisibleToUser) {hendlerm2();
			handlerOn = true;}
		super.onStop();
	}

	public void hendlerm2(){

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
	
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   
	    return false;
	}*/

	 

    
}