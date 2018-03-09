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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TimePicker;
import android.widget.Toast;


public class ScheduledActions extends Fragment {
	static String dstName,dstAddress,dstPassword,encKey;
	static int Page,dstPort,arTime;
	int Records = -1;
	ArrayList<String> idki = new ArrayList<String>();
	ArrayList<String> cond1 = new ArrayList<String>();
	ArrayList<String> cond2 = new ArrayList<String>();
	ArrayList<String> kind = new ArrayList<String>();
	ArrayList<String> data = new ArrayList<String>();
	ArrayList<String> gpio = new ArrayList<String>();
	ArrayList<String> state = new ArrayList<String>();
	ArrayList<String> nazwy = new ArrayList<String>();
	ArrayList<String> reverses = new ArrayList();
	ArrayList<String> nazwy_out = new ArrayList();
	ArrayList<String> idki_out = new ArrayList();
	ArrayList<String> reverses_out = new ArrayList();
	Date edittime = new Date(1);
    ProgressBar pb;Button r;DataBaseHelper myDbHelper; static int id_U;
    boolean menuCreated = false,VisibleToUser = true;
	boolean handlerOn = false,running=false;
    ListView listview;
    private SimpleAdapter adapter;
	Handler handler = new Handler();
	Runnable runabble = null;
	public static Context mContext;
    public ScheduledActions() {
    }

    public static ScheduledActions newInstance(int page,String name, String address, int port, String password,Context Context, String enc_key,int artime, int id_u) {
    	ScheduledActions fragment = new ScheduledActions();
        Page = page;
        dstName = name;
        dstAddress = address;
        dstPort = port;
        dstPassword = password;
   	   encKey = enc_key;
   	   arTime= artime;id_U=id_u;
        mContext = Context;
        return fragment;
    }
	public interface AsyncResponse {
		void processFinish(String output);
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
			ScheduledActionsTask edittimecheck = new ScheduledActionsTask(new AsyncResponse(){
				@Override
				public void processFinish(String output){
				}});
			edittimecheck.execute("GPIO_SAEtime");

		}
	}
	public void list_update(){
		ScheduledActionsTask GPIO_ListGet = new ScheduledActionsTask(new AsyncResponse(){
			@Override
			public void processFinish(String output){

			}});
		GPIO_ListGet.execute("GPIO_SAlist");
	}

	public Date StringtoDate(String dtStart){

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date = null;
		try {
			date = format.parse(dtStart);
		} catch (android.net.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public String DatetoString(Date date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		String stringtime = null;
		try {
			stringtime = format.format(date);
		} catch (android.net.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringtime;
	}
	public String SecondstoString(long l)
	{
		return String.format("%02d:%02d:%02d", new Object[] {
				Integer.valueOf((int)(l / 3600)), Integer.valueOf((int)((l % 3600) / 60)), Integer.valueOf((int)(l % 60))
		});
	}
    
	public class ScheduledActionsTask extends AsyncTask<String, Void, Boolean> {

		public AsyncResponse delegate = null;
		public ScheduledActionsTask(AsyncResponse delegate){
			this.delegate = delegate;
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
			  Connection c = new Connection();
			  if(params[0].equals("GPIO_SAEtime")) response = c.sendString(dstAddress, dstPort, dstPassword,params[0],256,encKey);
			  else if(params[0].equals("GPIO_SAlist"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";",16384,encKey);
			  else if(params[0].equals("Insert_Action")) response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2]+";"+params[3]+";"+params[4]+";"+params[5]+";"+params[6]+ ";"+DatetoString(new Date()),1024,encKey);
			  else if(params[0].equals("Update_Action")) response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1]+";"+params[2]+";"+params[3]+";"+params[4]+";"+params[5]+";"+params[6]+";"+params[7]+";"+DatetoString(new Date()),2048,encKey);
			  else if(params[0].equals("Delete_Action")) response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";"+params[1],1024,encKey);
			  else if(params[0].equals("GPIO_Oname"))response = c.sendString(dstAddress, dstPort, dstPassword,params[0]+";",8192,encKey);
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
		  running = false;
		  if (result == true && passwd == true){r.setTextColor(Color.GREEN);
			  if (list.get(1).equals("GPIO_SAEtime")){
				  if (!(list.get(2)).equals("None"))
				  {
					  Date date = StringtoDate(list.get(2));
					  if (!date.equals(edittime))
					  {
						  list_update();
						  edittime = date;
					  }
				  } else
				  {
					  listview.setAdapter(null);
					  idki.clear();
					  cond1.clear();
					  nazwy.clear();
					  cond2.clear();
					  kind.clear();
					  data.clear();
					  state.clear();
					  reverses.clear();
				  }
			  }
			  if (list.get(1).equals("GPIO_SAlist"))
			  {
				  idki.clear();
				  cond1.clear();
				  nazwy.clear();
				  cond2.clear();
				  kind.clear();
				  data.clear();
				  state.clear();
				  reverses.clear();
				  int i = 2;
				  final SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				  sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				  final SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				  final SimpleDateFormat sourceFormatH = new SimpleDateFormat("HH:mm");
				  sourceFormatH.setTimeZone(TimeZone.getTimeZone("UTC"));
				  final SimpleDateFormat destFormatH = new SimpleDateFormat("HH:mm");
				  while (i < list.size() - 1)
				  {
					  idki.add(list.get(i));
					  cond1.add(list.get(i + 1));
					  if (!list.get(i + 1).matches("timer"))
					  {
						  cond2.add(list.get(i + 2));
					  }
					  kind.add(list.get(i + 3));
					  if (list.get(i + 1).matches("timer"))
					  {
						  ArrayList arraylist = new ArrayList(Arrays.asList(list.get(i + 4).split(",")));
						  data.add(SecondstoString(Long.parseLong((String)arraylist.get(1))));
						  cond2.add(SecondstoString(Long.parseLong((String)arraylist.get(0))));
					  } else
					  {
						  Date parsed = null; // => Date is in UTC now
						  try {
							  if (list.get(i + 1).matches("date")) parsed = sourceFormat.parse(list.get(i + 4)); else parsed = sourceFormatH.parse(list.get(i + 4));
						  } catch (ParseException e) {
							  e.printStackTrace();
						  }
						  if (list.get(i + 1).matches("date")) data.add(destFormat.format(parsed)); else data.add(destFormatH.format(parsed));

					  }
					  state.add(list.get(i + 5));
					  nazwy.add(list.get(i + 6));
					  reverses.add(list.get(i + 7));
					  if (i>1000)break;
					  i += 8;
				  }
				  List<HashMap<String, String>> fillMaps = new ArrayList();
				  String as[] = new String[cond1.size()];
				  as = (String[])cond1.toArray(as);
				  String as1[] = new String[cond2.size()];
				  as1 = (String[])cond2.toArray(as1);
				  String as2[] = new String[kind.size()];
				  as2 = (String[])kind.toArray(as2);
				  String as3[] = new String[data.size()];
				  as3 = (String[])data.toArray(as3);
				  i = 0;
				  while (i < state.size())
				  {
					  if (state.get(i).equals("0") && reverses.get(i).equals("0") || state.get(i).equals("1") && reverses.get(i).equals("1"))
					  {
						  state.set(i, "OFF");
					  } else
					  if (state.get(i).equals("1") && reverses.get(i).equals("0") || state.get(i).equals("0") && reverses.get(i).equals("1"))
					  {
						  state.set(i, "ON");
					  } else
					  if (state.get(i).equals("2"))
					  {
						  state.set(i, "OPPOSITE");
					  }
					  i++;
				  }
				  String as4[] = new String[nazwy.size()];
				  as4 = (String[])nazwy.toArray(as4);
				  String as5[] = new String[state.size()];
				  as5 = (String[])state.toArray(as5);
				  i = 0;
				  while (i < as.length)
				  {
					  HashMap hashmap = new HashMap();
					  hashmap.put("warunek", (new StringBuilder()).append("").append(as[i]).toString());
					  if (as[i].matches("timer"))
					  {
						  hashmap.put("podwarunek", (new StringBuilder()).append("from ").append(as1[i]).append(" to 0").toString());
					  } else
					  {
						  hashmap.put("podwarunek", (new StringBuilder()).append("").append(as1[i]).toString());
					  }
					  hashmap.put("rodzaj", (new StringBuilder()).append("").append(as2[i]).toString());
					  if (as[i].matches("temperature"))
					  {
						  hashmap.put("dane", (new StringBuilder()).append("").append(as3[i]).append("C").toString());
					  } else
					  {
						  hashmap.put("dane", (new StringBuilder()).append("").append(as3[i]).toString());
					  }
					  hashmap.put("nazwa", (new StringBuilder()).append("").append(as4[i]).toString());
					  hashmap.put("stan", (new StringBuilder()).append("").append(as5[i]).toString());
					  fillMaps.add(hashmap);
					  i++;
				  }
				  String[] from = new String[] {"dane","warunek", "podwarunek", "rodzaj", "gpio", "stan"};
				  int[] to = new int[] {R.id.dane, R.id.warunek, R.id.podwarunek, R.id.rodzaj,  R.id.gpio, R.id.stan};
				  adapter = new SimpleAdapter(mContext, fillMaps, R.layout.list2, from, to);

				  listview.setAdapter(adapter);

				  listview.setOnItemClickListener(new OnItemClickListener() {
					  public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
						  // TODO Auto-generated method stub

						  actionDialog(1,position);
					  }
				  });
			  }
			  if (list.get(1).equals("GPIO_Oname")){
				  try{
					  idki_out.clear();nazwy_out.clear();reverses_out.clear();
					  for(int j=2;j<(list.size()-1);j=j+4){
						  idki_out.add(list.get(j));
						  nazwy_out.add(list.get(j+1));
						  reverses_out.add(list.get(j+3));
					  }
					  delegate.processFinish("1");
				  }
				  catch (IndexOutOfBoundsException e) {
					  idki_out.clear();nazwy_out.clear();reverses_out.clear();
					  delegate.processFinish("0");
				  }
			  }
			  if (list.get(1).equals("Insert_Action") || list.get(1).equals("Update_Action") || list.get(1).equals("Delete_Action"))
			  {
				  edittime = new Date(1);
				  check_state();
			  }
			  }
		  else if (result == false || passwd == false) {
			  if (result == false) {myDbHelper.dodajLog(id_U,response);r.setTextColor(Color.RED);}
			  else if (passwd == false) {myDbHelper.dodajLog(id_U,list.get(1));r.setTextColor(Color.RED);}
		  }
		 
		  pb.setVisibility(View.INVISIBLE);
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
         	    actionDialog(0,-1);
            }
        });
		pb.setVisibility(View.INVISIBLE);
		pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
		menuCreated = true;
		edittime = new Date(1);
		running = false;
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
		if (menuCreated && VisibleToUser) {hendlerm2();
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



	public void hendlerm2(){

		if(handlerOn==false && menuCreated==true && arTime > 0){
			runabble = new Runnable() {
				public void run() {
					if (menuCreated==true){
						check_state();
						handler.postDelayed(this, arTime);}
				}
			};

			handler.postDelayed(runabble, arTime);
			handlerOn = true;}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   
	    return false;
	}

	 
	 public void actionDialog(final int mode, final int position){
		 LayoutInflater factory = LayoutInflater.from(mContext);
 		final View loginView = factory.inflate(R.layout.addaction, null);
 		final Spinner cond1s = (Spinner) loginView.findViewById(R.id.condition1);
        List<String> cond1slist = new ArrayList<String>();
        cond1slist.add("date");cond1slist.add("hour");cond1slist.add("timer");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item,cond1slist);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        cond1s.setAdapter(dataAdapter);
        
        final Spinner cond2s = (Spinner) loginView.findViewById(R.id.condition2);
        List<String> cond2slist = new ArrayList<String>();
        cond2slist.add("euqal");cond2slist.add("less");cond2slist.add("bigger");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(mContext, R.layout.spinner_item,cond2slist);
        dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
        cond2s.setAdapter(dataAdapter2);

		 final Spinner gpios = (Spinner) loginView.findViewById(R.id.gpio);
		 //ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(mContext, R.layout.spinner_item,nazwy);
		 //dataAdapter3.setDropDownViewResource(R.layout.spinner_item);
		 //gpios.setAdapter(dataAdapter3);
		 ScheduledActionsTask GPIO_NameGet = new ScheduledActionsTask(new AsyncResponse(){
			 @Override
			 public void processFinish(String output){
				 if (output.equals("1")){
					 String[] items = nazwy_out.toArray( new String[0] );
					 ArrayAdapter<String> adapterbi = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, items);
					 adapterbi.setDropDownViewResource(R.layout.spinner_item);
					 gpios.setAdapter(adapterbi);
					 if (mode == 1)
					 {
						 gpios.setSelection(adapterbi.getPosition(nazwy.get(position)));
					 }

				 }
			 }});
		 GPIO_NameGet.execute("GPIO_Oname");
        
        final DatePicker datepicker = (DatePicker) loginView.findViewById(R.id.datePicker1);
        final TimePicker timepicker = (TimePicker) loginView.findViewById(R.id.timePicker1);timepicker.setIs24HourView(true);
        final EditText tempet = (EditText) loginView.findViewById(R.id.temp);
        final Spinner statesp = (Spinner) loginView.findViewById(R.id.state);
		 ArrayList statesb_a = new ArrayList();
		 statesb_a.add("OFF");
		 statesb_a.add("ON");
		 statesb_a.add("OPPOSITE");
		 final ArrayAdapter<String> arrayadapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, statesb_a);
		 arrayadapter.setDropDownViewResource(R.layout.spinner_item);
		 statesp.setAdapter(arrayadapter);
        final CheckBox kindchk = (CheckBox) loginView.findViewById(R.id.kind);
		 final NumberPicker nphours = (NumberPicker)loginView.findViewById(R.id.hours);
		 nphours.setMaxValue(672);
		 nphours.setMinValue(0);
		 final NumberPicker npminutes = (NumberPicker)loginView.findViewById(R.id.minutes);
		 npminutes.setMaxValue(59);
		 npminutes.setMinValue(0);
		 final NumberPicker npseconds = (NumberPicker)loginView.findViewById(R.id.seconds);
		 npseconds.setMaxValue(59);
		 npseconds.setMinValue(0);
        cond1s.setOnItemSelectedListener(new OnItemSelectedListener() {
           
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				TableRow trtemp = (TableRow) loginView.findViewById(R.id.tableRow5);
				TableRow trdate = (TableRow) loginView.findViewById(R.id.tableRow3);
				TableRow trcond2 = (TableRow) loginView.findViewById(R.id.tableRow2);
				TableRow trtime = (TableRow) loginView.findViewById(R.id.tableRow4);
				TableRow trkind = (TableRow) loginView.findViewById(R.id.tableRow7);
				TableRow trtimer = (TableRow) loginView.findViewById(R.id.tableRow5_1);
				
				if (position == 0){
					trtemp.setVisibility(View.GONE);
					trdate.setVisibility(View.VISIBLE);
					trcond2.setVisibility(View.GONE);
					trtime.setVisibility(View.VISIBLE);
					trkind.setVisibility(View.GONE);
					trtimer.setVisibility(View.GONE);
				}
				else if (position == 1){
					trtemp.setVisibility(View.GONE);
					trdate.setVisibility(View.GONE);
					trcond2.setVisibility(View.GONE);
					trtime.setVisibility(View.VISIBLE);
					trkind.setVisibility(View.VISIBLE);
					trtimer.setVisibility(View.GONE);
				}
				else if (position == 2){
					trtemp.setVisibility(View.GONE);
					trdate.setVisibility(View.GONE);
					trcond2.setVisibility(View.GONE);
					trtime.setVisibility(View.GONE);
					trkind.setVisibility(View.VISIBLE);
					trtimer.setVisibility(View.VISIBLE);
				}
				else if (position == 3){
					trtemp.setVisibility(View.VISIBLE);
					trdate.setVisibility(View.GONE);
					trcond2.setVisibility(View.VISIBLE);
					trtime.setVisibility(View.GONE);
					trkind.setVisibility(View.VISIBLE);
					trtimer.setVisibility(View.GONE);
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}

        });
 		
 		if (mode==0){
 		final AlertDialog d = new AlertDialog.Builder(getActivity().getWindow().getContext())
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
                    	
                      String cond1 = cond1s.getSelectedItem().toString();
                      String cond2 = cond2s.getSelectedItem().toString();
                      String gpio = idki_out.get(gpios.getSelectedItemPosition());
                      String kind = "once";
                      String state = idki_out.get(gpios.getSelectedItemPosition());
                      if(kindchk.isChecked())kind="periodically";
           	          String dane="";
						Calendar dset = Calendar.getInstance();
						dset.set(datepicker.getYear(),datepicker.getMonth(),datepicker.getDayOfMonth(),timepicker.getCurrentHour(),timepicker.getCurrentMinute());
						final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						final SimpleDateFormat sfH = new SimpleDateFormat("HH:mm");
						sf.setTimeZone(TimeZone.getTimeZone("UTC"));sfH.setTimeZone(TimeZone.getTimeZone("UTC"));
           	          //String curTime = String.format("%02d:%02d", timepicker.getCurrentHour(), timepicker.getCurrentMinute());
           	          //String curDate = String.format("%02d-%02d", (datepicker.getMonth() + 1), datepicker.getDayOfMonth());
						if (cond1.equals("date")) {
							//dane = datepicker.getYear() + "-" + curDate + " " + curTime;
							dane = sf.format(dset.getTime());
						} else if (cond1.equals("hour")) {
							//dane = curTime;
							dane = sfH.format(dset.getTime());
						} else if (cond1.equals("timer")) {
							dane = String.valueOf(((nphours.getValue() * 3600) + (npminutes.getValue() * 60)) + npseconds.getValue());
							dane = dane + "," + dane;
						} else if (cond1.equals("temperature")) {
							dane = tempet.getText().toString();
						}
						int state_s = statesp.getSelectedItemPosition();
						String reverse_out = (String) reverses_out.get(gpios.getSelectedItemPosition());
						if (state_s < 2) {
							if ((state_s == 0 && reverse_out.equals("0")) || (state_s == 1 && reverse_out.equals("1"))) {
								state_s = 0;
							} else if ((state_s == 0 && reverse_out.equals("1")) || (state_s == 1 && reverse_out.equals("0"))) {
								state_s = 1;
							}
						}
						if (cond1.equals("temperature") && dane.matches("")) {
							Toast.makeText(mContext, "Fill temp !" + cond1 + dane, Toast.LENGTH_SHORT).show();
						} else if (cond1.equals("timer") && dane.matches("0,0")) {
							Toast.makeText(mContext, "Must be more then 0 seconds !" + cond1 + dane, Toast.LENGTH_SHORT).show();
						} else {

							ScheduledActionsTask GPIO_Insert = new ScheduledActionsTask(new AsyncResponse(){
								@Override public void processFinish(String output){}});
							GPIO_Insert.execute("Insert_Action", cond1, cond2, kind, dane, gpio, String.valueOf(state_s));
							d.dismiss();
							check_state();
						}
           	        	/*if(cond1.equals("date"))
           	        		dane=datepicker.getYear()+"-"+curDate+" "+curTime;
           	        	else if (cond1.equals("hour"))dane=curTime;
           	        	else if (cond1.equals("temperature"))dane=tempet.getText().toString();
           	           if(gpios.getSelectedItemPosition() == 0)gpio="22";
           	           else if (gpios.getSelectedItemPosition() == 1)gpio="10";
           	           else if (gpios.getSelectedItemPosition() == 2)gpio="9";
           	           else if (gpios.getSelectedItemPosition() == 3)gpio="11";
           	           if(state.matches("ON"))state="1";
           	           else state="0";
           	           if (cond1.equals("temperature")&&dane.matches(""))
           	        	Toast.makeText(mContext, "Fill temp !"+cond1+dane, Toast.LENGTH_SHORT).show();
           	           else{
           	        	MyClientTask2 myClientTask2_3 = new MyClientTask2();
				    	myClientTask2_3.execute("Insert_Action",cond1,cond2,kind,dane,gpio,state);
                        d.dismiss();
                        check_state();
           	           }*/
                    }
                });
            }
        });
        d.show();}
 		else if (mode==1){
 			final AlertDialog d = new AlertDialog.Builder(getActivity().getWindow().getContext())
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
 	                                //Do nothing here. We override the onclick
 	                            }
 	                        })
 	                .setNegativeButton("CANCEL", null)
 	                .create();
 			cond1s.setSelection(dataAdapter.getPosition(cond1.get(position)));
 			if(cond1s.getSelectedItem().toString().matches("temperature")){
 				cond2s.setSelection(dataAdapter2.getPosition(cond2.get(position)));
 				String path = data.get(position);
 				path = path.substring(0, path.length() - 2);
 				tempet.setText(path);
 			}
 			else if(cond1s.getSelectedItem().toString().matches("date")){
 				
 				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
 			    Date date = null;
				try {
					date = simpleDateFormat.parse(data.get(position));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 Calendar cal = Calendar.getInstance();
				    cal.setTime(date);
 				datepicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
 				timepicker.setCurrentHour(date.getHours());
 				timepicker.setCurrentMinute(date.getMinutes());
 				
 			}
 			else if(cond1s.getSelectedItem().toString().matches("hour")){
 				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
 			    Date date = null;
				try {
					date = simpleDateFormat .parse(data.get(position));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
 				timepicker.setCurrentHour(date.getHours());
 				timepicker.setCurrentMinute(date.getMinutes());
 			}
			else if (cond1s.getSelectedItem().toString().matches("timer")) {
				List<String> arrayList = new ArrayList(Arrays.asList(((String) this.cond2.get(position)).split(":")));
				nphours.setValue(Integer.parseInt((String) arrayList.get(0), 10));
				npminutes.setValue(Integer.parseInt((String) arrayList.get(1), 10));
				npseconds.setValue(Integer.parseInt((String) arrayList.get(2), 10));
			}
 			if(kind.get(position).matches("periodically"))kindchk.setChecked(true);
			statesp.setSelection(arrayadapter.getPosition(this.state.get(position)));

 	        d.setOnShowListener(new DialogInterface.OnShowListener() {

 	            @Override
 	            public void onShow(DialogInterface dialog) {

 	                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
 	                b.setOnClickListener(new View.OnClickListener() {

 	                    @Override
 	                    public void onClick(View view) {

							String cond1 = cond1s.getSelectedItem().toString();
							String cond2 = cond2s.getSelectedItem().toString();
							String gpio = idki_out.get(gpios.getSelectedItemPosition());
							String kind = "once";
							String state = idki_out.get(gpios.getSelectedItemPosition());
							if(kindchk.isChecked())kind="periodically";
							String dane="";
							Calendar dset = Calendar.getInstance();
							dset.set(datepicker.getYear(),datepicker.getMonth(),datepicker.getDayOfMonth(),timepicker.getCurrentHour(),timepicker.getCurrentMinute());
							final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
							final SimpleDateFormat sfH = new SimpleDateFormat("HH:mm");
							sf.setTimeZone(TimeZone.getTimeZone("UTC"));sfH.setTimeZone(TimeZone.getTimeZone("UTC"));
							//String curTime = String.format("%02d:%02d", timepicker.getCurrentHour(), timepicker.getCurrentMinute());
							//String curDate = String.format("%02d-%02d", (datepicker.getMonth() + 1), datepicker.getDayOfMonth());
							if (cond1.equals("date")) {
								//dane = datepicker.getYear() + "-" + curDate + " " + curTime;
								dane = sf.format(dset.getTime());
							} else if (cond1.equals("hour")) {
								dane = sfH.format(dset.getTime());
								//dane = curTime;
							} else if (cond1.equals("timer")) {
								dane = String.valueOf(((nphours.getValue() * 3600) + (npminutes.getValue() * 60)) + npseconds.getValue());
								dane = dane + "," + dane;
							} else if (cond1.equals("temperature")) {
								dane = tempet.getText().toString();
							}
							int state_s = statesp.getSelectedItemPosition();
							String reverse_out = (String) reverses_out.get(gpios.getSelectedItemPosition());
							if (state_s < 2) {
								if ((state_s == 0 && reverse_out.equals("0")) || (state_s == 1 && reverse_out.equals("1"))) {
									state_s = 0;
								} else if ((state_s == 0 && reverse_out.equals("1")) || (state_s == 1 && reverse_out.equals("0"))) {
									state_s = 1;
								}
							}
							if (cond1.equals("temperature") && dane.matches("")) {
								Toast.makeText(mContext, "Fill temp !" + cond1 + dane, Toast.LENGTH_SHORT).show();
							} else if (cond1.equals("timer") && dane.matches("0,0")) {
								Toast.makeText(mContext, "Must be more then 0 seconds !" + cond1 + dane, Toast.LENGTH_SHORT).show();
							} else {

								ScheduledActionsTask GPIO_Update = new ScheduledActionsTask(new AsyncResponse(){
									@Override public void processFinish(String output){}});
								GPIO_Update.execute("Update_Action", cond1, cond2, kind, dane, gpio, String.valueOf(state_s),idki.get(position));
								d.dismiss();
								check_state();
							}
 	                    	/*
 	                      String cond1 = cond1s.getSelectedItem().toString();
 	                      String cond2 = cond2s.getSelectedItem().toString();
 	                      String gpio = gpios.getSelectedItem().toString();
 	                      String kind = "once";
 	                      String state = statetb.getText().toString();
 	                      if(kindchk.isChecked())kind="periodically";
 	           	          String dane="";
 	           	          String curTime = String.format("%02d:%02d", timepicker.getCurrentHour(), timepicker.getCurrentMinute());
 	           	          String curDate = String.format("%02d-%02d", (datepicker.getMonth() + 1), datepicker.getDayOfMonth());
 	           	        	if(cond1.equals("date"))
 	           	        		dane=datepicker.getYear()+"-"+curDate+" "+curTime;
 	           	        	else if (cond1.equals("hour"))dane=curTime;
 	           	        	else if (cond1.equals("temperature"))dane=tempet.getText().toString();
 	           	           if(gpios.getSelectedItemPosition() == 0)gpio="22";
 	           	           else if (gpios.getSelectedItemPosition() == 1)gpio="10";
 	           	           else if (gpios.getSelectedItemPosition() == 2)gpio="9";
 	           	           else if (gpios.getSelectedItemPosition() == 3)gpio="11";
 	           	           if(state.matches("ON"))state="1";
 	           	           else state="0";
 	           	           if (cond1.equals("temperature")&&dane.matches(""))
 	           	        	Toast.makeText(mContext, "Fill temp !"+cond1+dane, Toast.LENGTH_SHORT).show();
 	           	           else{
 	           	        	MyClientTask2 myClientTask2_3 = new MyClientTask2();
 					    	myClientTask2_3.execute("Update_Action",cond1,cond2,kind,dane,gpio,state,idki.get(position));
 	                        d.dismiss();
 	                        check_state();
 	           	           }*/
 	                    }
 	                });
 	               Button c = d.getButton(AlertDialog.BUTTON_NEUTRAL);
	                c.setOnClickListener(new View.OnClickListener(){

 	                    @Override
 	                    public void onClick(View view) {

							ScheduledActionsTask GPIO_Delete = new ScheduledActionsTask(new AsyncResponse(){
								@Override public void processFinish(String output){}});
							GPIO_Delete.execute("Delete_Action",idki.get(position));
							d.dismiss();
 	                       listview.setAdapter(null);
 	                        check_state();
 	                   }
	     	        });
 	            }
 	        });
 	        d.show();
 			
 		}
	 }
    
}