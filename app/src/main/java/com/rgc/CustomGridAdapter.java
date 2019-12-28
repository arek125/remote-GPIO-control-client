package com.rgc;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomGridAdapter extends BaseAdapter {
 
        private Context context;
        private ArrayList<String> gpios;
        private ArrayList<String> stany;
        private ArrayList<String> nazwy;
        private ArrayList<String> reverses;
        private ArrayList<String> methods = new ArrayList<String>();
        private ArrayList<String> outtypes = new ArrayList<String>();
     
        public CustomGridAdapter(Context context, ArrayList<String> gpios, ArrayList<String> stany, ArrayList<String> nazwy, ArrayList<String> reverses, ArrayList<String> outtypes) {
 
            this.context        = context;
            this.gpios     		= gpios;
            this.stany     		= stany;
            this.nazwy     		= nazwy;
            this.reverses     	= reverses;
            this.outtypes     	= outtypes;
        }
        public CustomGridAdapter(Context context, ArrayList<String> gpios, ArrayList<String> stany, ArrayList<String> nazwy, ArrayList<String> reverses, ArrayList<String> methods, int dod) {
        	 
            this.context        = context;
            this.gpios     		= gpios;
            this.stany     		= stany;
            this.nazwy     		= nazwy;
            this.reverses     	= reverses;
            this.methods     	= methods;
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
         
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gridView;
     
            if (convertView == null) {
     
                //gridView = new View(context);
                gridView = inflater.inflate( R.layout.grid_item , null);
                 String reverse= "";String bindtype = "";String outtype = "";
                TextView tv1 = (TextView) gridView.findViewById(R.id.grid_item_name);
                TextView tv2 = (TextView) gridView.findViewById(R.id.grid_item_gpio);
                if(reverses.get(position).equals("1"))reverse="(r)";
                if (!methods.isEmpty()){if(methods.get(position).equals("inloop"))bindtype = "(l)";else if (methods.get(position).equals("ined"))bindtype = "(e)";}
                if (!outtypes.isEmpty()){if(outtypes.get(position).equals("0"))outtype = "(o)";else if (outtypes.get(position).equals("1"))outtype = "(p)";}
                tv1.setText(nazwy.get(position));tv2.setText("GPIO: "+gpios.get(position)+bindtype+outtype+reverse);
                ImageView iv = (ImageView) gridView.findViewById(R.id.grid_item_image);
                if ((stany.get(position).equals("1")&&reverses.get(position).equals("0"))||(stany.get(position).equals("0")&&reverses.get(position).equals("1")))
                	iv.setImageResource(R.drawable.green);
                else if ((stany.get(position).equals("0")&&reverses.get(position).equals("0"))||(stany.get(position).equals("1")&&reverses.get(position).equals("1")))
                	iv.setImageResource(R.drawable.red);
                else iv.setImageResource(R.drawable.yelow);

            } else {
               gridView = (View) convertView;
            }
     
            
            
            return gridView;
        }
}