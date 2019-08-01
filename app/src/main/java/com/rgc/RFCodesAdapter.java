package com.rgc;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RFCodesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RFCode> rfCodes;

    RFCodesAdapter(Context context, ArrayList<RFCode> rfCodes){
        this.rfCodes = rfCodes;
        this.context = context;
    }


    @Override
    public int getCount() {
        return rfCodes.size();
    }

    @Override
    public Object getItem(int position) {
        return rfCodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rfCodes.get(position).id;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        RFCode rf = rfCodes.get(position);
        if (convertView == null) {
            gridView = inflater.inflate( R.layout.grid_item_rf , null);
            TextView tv_name = gridView.findViewById(R.id.grid_item_name);
            TextView tv_code = gridView.findViewById(R.id.grid_item_code);
            ImageView iv = gridView.findViewById(R.id.grid_item_image);
            tv_name.setText(rf.name);
            tv_code.setText(String.valueOf(rf.code));
            if(rf.type.equals("Transmit"))iv.setImageResource(R.drawable.transfer_up);
            else iv.setImageResource(R.drawable.transfer_down);

        } else {
            gridView = (View) convertView;
        }



        return gridView;
    }
}