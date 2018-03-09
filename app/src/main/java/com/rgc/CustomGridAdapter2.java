package com.rgc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class CustomGridAdapter2 extends BaseAdapter {
 
        private Context context;
        private ArrayList<String> idki;
        private ArrayList<String> gpios;
        private ArrayList<String> gpios_in;
        private ArrayList<String> gpios_out;
        private ArrayList<String> fr;
        private ArrayList<String> dc;
        private ArrayList<String> s_s;
        private ArrayList<String> nazwy;
        private ArrayList<String> reverses;
     
        public CustomGridAdapter2(Context context, ArrayList<String> idki,ArrayList<String> gpios, ArrayList<String> fr,ArrayList<String> dc, ArrayList<String> s_s, ArrayList<String> nazwy, ArrayList<String> reverses) {
 
            this.context        = context;
            this.idki     		= idki;
            this.gpios     		= gpios;
            this.fr     		= fr;
            this.dc     		= dc;
            this.s_s     		= s_s;
            this.nazwy     		= nazwy;
            this.reverses     	= reverses;
            this.gpios_out     		= new ArrayList<String>();
            this.gpios_in     		= new ArrayList<String>();
        }


         
        @Override
        public int getCount() {
             
            return gpios.size();
        }
     
        @Override
        public Object getItem(int position) {
             
            return null;
        }
     
        @Override
        public long getItemId(int position) {
             
            return 0;
        }
         
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     
            View gridView;
     
            if (convertView == null) {
     
                gridView = new View(context);
                gridView = inflater.inflate( R.layout.grid_item2 , null);
                 String reverse= "";
                TextView tv1 = (TextView) gridView.findViewById(R.id.grid_item_name);
                TextView tv2 = (TextView) gridView.findViewById(R.id.grid_item_gpio);
                final TextView tv3 = (TextView) gridView.findViewById(R.id.grid_item_dc);
                final TextView tv4 = (TextView) gridView.findViewById(R.id.grid_item_fr);
                if(reverses.get(position).equals("1"))reverse="(r)";
               
                tv1.setText(nazwy.get(position));tv2.setText("GPIO:"+gpios.get(position));
                tv3.setText("dc="+dc.get(position)+"%");tv4.setText("f="+fr.get(position)+"Hz");
                final ImageView iv = (ImageView) gridView.findViewById(R.id.grid_item_image);
                if (s_s.get(position).equals("1"))
                	iv.setImageResource(R.drawable.green);
                else if (s_s.get(position).equals("0"))
                	iv.setImageResource(R.drawable.red);
                else iv.setImageResource(R.drawable.yelow);
                final SeekBar sb1 = (SeekBar) gridView.findViewById(R.id.seekBar1);
                RelativeLayout rLayout = (RelativeLayout) gridView.findViewById(R.id.grid_item_rl);
                rLayout.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                    	LayoutInflater factory = LayoutInflater.from(context);
                		final View loginView = factory.inflate(R.layout.addpwm, null);
                        TextView tvT = (TextView) loginView.findViewById(R.id.titleL);tvT.setText("Edit GPIO PWM stering: ");
                        final EditText name = (EditText) loginView.findViewById(R.id.name);name.setText(nazwy.get(position));
                    	final EditText gpio = (EditText) loginView.findViewById(R.id.gpio);gpio.setText(gpios.get(position));
                    	final EditText frE = (EditText) loginView.findViewById(R.id.fr);frE.setText(fr.get(position));
                    	final EditText dcE = (EditText) loginView.findViewById(R.id.dc);dcE.setText(dc.get(position));
                    	final CheckBox reverse = (CheckBox) loginView.findViewById(R.id.reverse);
                        if (reverses.get(position).equals("1"))reverse.setChecked(true);
                        GPIO_Pwm.GPIO_PwmTask Allpins_GPIO_out = new GPIO_Pwm.GPIO_PwmTask(new GPIO_Pwm.AsyncResponse(){
                            @Override
                            public void processFinish(String output){
                                gpios_out.clear();
                                gpios_out = new ArrayList(Arrays.asList(output.split(",")));
                            }});
                        Allpins_GPIO_out.execute("Allpins_GPIO_out");
                        GPIO_Pwm.GPIO_PwmTask Allpins_GPIO_in = new GPIO_Pwm.GPIO_PwmTask(new GPIO_Pwm.AsyncResponse(){
                            @Override
                            public void processFinish(String output){
                                gpios_in.clear();
                                gpios_in = new ArrayList(Arrays.asList(output.split(",")));
                            }});
                        Allpins_GPIO_in.execute("Allpins_GPIO_in");
                		final AlertDialog d = new AlertDialog.Builder(context)
                                .setView(loginView)
                                .setPositiveButton("SAVE",
                                        new Dialog.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface d, int which) {
                                                //Do nothing here. We override the onclick
                                            }
                                        })
                                .setNeutralButton("DELETE", new Dialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface d, int which) {
                                    	GPIO_Pwm.GPIO_PwmTask Add_pwm = new GPIO_Pwm.GPIO_PwmTask(new GPIO_Pwm.AsyncResponse(){
                             	   		    @Override
                             	   			public void processFinish(String output){
                             	   		    	
                             	   		     }});
                             	       	Add_pwm.execute("Delete_GPIO_pwm",idki.get(position),gpios.get(position),nazwy.get(position));
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

                         	        	String reverseS = "0";if (reverse.isChecked())reverseS = "1";
                                        List<String> templist = new ArrayList(Arrays.asList(gpio.getText().toString().split(",")));
                                        List<String> templist2 = new ArrayList(Arrays.asList(gpios.get(position).split(",")));
                                        boolean cont = false;
                                        boolean notbcm = false;
                                        for (String temp : templist) {
                                            if (temp.matches("2|3|4|17|27|22|10|9|11|5|6|13|19|26|14|15|18|23|24|25|8|7|12|16|20|21")) {
                                                if (!templist2.contains(temp)) {
                                                    if (!gpios_out.contains(temp) && !gpios_in.contains(temp)) {
                                                        Iterator it = gpios.iterator();
                                                        while (it.hasNext()) {
                                                            if (new ArrayList(Arrays.asList(((String) it.next()).split(","))).contains(temp)) {
                                                                cont = true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    else{
                                                    cont = true;
                                                    break;}
                                                }
                                            } else {
                                                notbcm = true;
                                                break;
                                            }
                                        }
                         	        	if(name.getText().toString().matches("")||gpio.getText().toString().matches("")||frE.getText().toString().matches("")||dcE.getText().toString().matches(""))
                         	        		Toast.makeText(context, "Fill Name, GPIO, DC and f!", Toast.LENGTH_SHORT).show();
                         	        	else if (Integer.parseInt(dcE.getText().toString()) > 100 || Integer.parseInt(dcE.getText().toString()) < 0)
                         	        			Toast.makeText(context, "DC must bo beetwen 0 and 100!", Toast.LENGTH_SHORT).show();
                                        else if (cont)
                                            Toast.makeText(context, "Pin/s alredy in use!", Toast.LENGTH_SHORT).show();
                                        else if (notbcm)
                                            Toast.makeText(context, "Not GPIO BCM!", Toast.LENGTH_SHORT).show();
                         	        	else{
                         	        		GPIO_Pwm.GPIO_PwmTask Add_pwm = new GPIO_Pwm.GPIO_PwmTask(new GPIO_Pwm.AsyncResponse(){
                         	   		    @Override
                         	   			public void processFinish(String output){
                         	   		    	
                         	   		     }});
                         	       	Add_pwm.execute("Edit_GPIO_pwm",idki.get(position),gpios.get(position), gpio.getText().toString(),frE.getText().toString(),dcE.getText().toString(),name.getText().toString(),reverseS);

                                        d.dismiss();}
                                    }
                                });
                            }
                        });
                        d.show();
                          //Toast.makeText(context, "Long click!", Toast.LENGTH_SHORT).show();
                          return true;
                    }
                });
                OnClickListener oCL = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      	LayoutInflater factory = LayoutInflater.from(context);
                		final View loginView = factory.inflate(R.layout.editfr, null);
        	        	final EditText frE = (EditText) loginView.findViewById(R.id.fr);frE.setText(fr.get(position).toString());
         	        	final EditText dcE = (EditText) loginView.findViewById(R.id.dc);dcE.setText(dc.get(position).toString());
                        
                		final AlertDialog d = new AlertDialog.Builder(context)
                                .setView(loginView)
                                .setPositiveButton("CHANGE",
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


                         	        	
                         	        	if(frE.getText().toString().matches("")||dcE.getText().toString().matches(""))
                         	        		Toast.makeText(context, "Fill DC and FR!", Toast.LENGTH_SHORT).show();
                         	        	else if (Integer.parseInt(dcE.getText().toString()) > 100 || Integer.parseInt(dcE.getText().toString()) < 0)
                         	        			Toast.makeText(context, "DC must bo beetwen 0 and 100%!", Toast.LENGTH_SHORT).show();
                         	        	else if (s_s.get(position).equals("0"))
                     	        			Toast.makeText(context, "PWM is stopped!", Toast.LENGTH_SHORT).show();
                         	        	else{
                         	        		GPIO_Pwm.GPIO_PwmTask Edit_PFRDC = new GPIO_Pwm.GPIO_PwmTask(new GPIO_Pwm.AsyncResponse(){
                         	   		    @Override
                         	   			public void processFinish(String output){
                         	   		    List list = new ArrayList<String>(Arrays.asList(output.split(";")));
                         	   		    tv4.setText("f="+list.get(0).toString()+"Hz");fr.set(position, list.get(0).toString());
                         	   		    tv3.setText("dc="+list.get(1).toString()+"%");dc.set(position, list.get(1).toString());
                         	   		    sb1.setProgress(Integer.parseInt(list.get(1).toString()));
                         	   		     }});
                         	        		Edit_PFRDC.execute("GPIO_PFRDC", idki.get(position),gpios.get(position),frE.getText().toString(),dcE.getText().toString());

                                        d.dismiss();}
                                    }
                                });
                            }
                        });
                        d.show();
                         
                    }
                };
                tv3.setOnClickListener(oCL);
                tv4.setOnClickListener(oCL);
                
                sb1.setProgress(Integer.parseInt(dc.get(position)));
                if(reverses.get(position).equals("1"))sb1.setRotation(180);
                
                iv.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                       String s_sS = s_s.get(position);
                       if(s_sS.equals("0"))s_sS="1";
                   		else s_sS="0";
                    	GPIO_Pwm.GPIO_PwmTask GPIO_PSS = new GPIO_Pwm.GPIO_PwmTask(new GPIO_Pwm.AsyncResponse(){
              			    @Override
              				public void processFinish(String output){
              			    	List list = new ArrayList<String>(Arrays.asList(output.split(";")));
              			    	 if (list.get(1).equals("1"))
              	                	iv.setImageResource(R.drawable.green);
              	                else if (list.get(1).equals("0"))
              	                	iv.setImageResource(R.drawable.red);
              			    	 s_s.set(position, list.get(1).toString());
              			    	tv3.setText("dc="+list.get(0).toString()+"%");
              			    	dc.set(position, list.get(0).toString());
              			     }});
              			GPIO_PSS.execute("GPIO_PSS",idki.get(position),gpios.get(position),String.valueOf(sb1.getProgress()),s_sS,fr.get(position));
                    }
                });
                
                sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    	
                    	if (b == true && s_s.get(position).equals("1")){
                    	
              		  GPIO_Pwm.GPIO_PwmTask GPIO_PDC = new GPIO_Pwm.GPIO_PwmTask(new GPIO_Pwm.AsyncResponse(){
          			    @Override
          				public void processFinish(String output){
          			    	tv3.setText("dc="+output+"%");
          			    	dc.set(position, output);
          			     }});
          			GPIO_PDC.execute("GPIO_PDC",idki.get(position),gpios.get(position),String.valueOf(i));
                    	}
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    	if (s_s.get(position).equals("1")){
                        	
              		  GPIO_Pwm.GPIO_PwmTask GPIO_PDCu = new GPIO_Pwm.GPIO_PwmTask(new GPIO_Pwm.AsyncResponse(){
          			    @Override
          				public void processFinish(String output){

          			     }});
          			GPIO_PDCu.execute("GPIO_PDCu",idki.get(position),gpios.get(position),String.valueOf(seekBar.getProgress()));
                    	}
                    }

                });
                
            } else {
               gridView = (View) convertView;
            }
     
            
            
            return gridView;
        }
        
        @Override
        public boolean isEnabled(int position) {
           //check the position to be locked or not
           //if yes then return true, else return false
        return false;
        }
        

}