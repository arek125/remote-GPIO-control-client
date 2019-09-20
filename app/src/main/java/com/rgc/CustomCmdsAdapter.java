package com.rgc;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomCmdsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CustomCmd> cmds;

    CustomCmdsAdapter(Context context, ArrayList<CustomCmd> cmds){
        this.cmds = cmds;
        this.context = context;
    }


    @Override
    public int getCount() {
        return cmds.size();
    }

    @Override
    public Object getItem(int position) {
        return cmds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cmds.get(position).id;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        CustomCmd cmd = cmds.get(position);
        if (convertView == null) {
            gridView = inflater.inflate( R.layout.grid_item_cmd , null);
            TextView tv_name = gridView.findViewById(R.id.grid_item_name);
            TextView tv_cmd = gridView.findViewById(R.id.grid_item_cmd);
            tv_name.setText(cmd.name);
            tv_cmd.setText(String.valueOf(cmd.cmd));

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }
}
