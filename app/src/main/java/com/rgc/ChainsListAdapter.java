package com.rgc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChainsListAdapter extends BaseAdapter {

    List<Chain> chainsList;
    Context context;
    LayoutInflater layoutInflater;
    String[] namesOfDays = new String[] { "Monday", "Tuesday","Wednesday", "Thurseday","Friday", "Saturday", "Sunday" };
    String[] statuses = {"OFF","ON"};
    AlphaAnimation alpha = new AlphaAnimation(1F, 0.5F);
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdfSeconds = new SimpleDateFormat("HH:mm:ss");

    public ChainsListAdapter(List<Chain> chainsList, Context context) {
        super();
        this.chainsList = chainsList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        alpha.setDuration(250);
        //namesOfDays = DateFormatSymbols.getInstance().getWeekdays();
        sdfDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdfHour.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdfSeconds.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public int getCount() {

        return chainsList.size();
    }

    @Override
    public Chain getItem(int position) {
        return chainsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView= layoutInflater.inflate(R.layout.chain_elem, null);
        final Chain chain = chainsList.get(position);
        TextView nazwa = convertView.findViewById(R.id.name);
        nazwa.setText(chain.nazwa);
        TextView statusN = convertView.findViewById(R.id.status);
        statusN.setText(chain.nazwaStatusu);
        final Button bStartEnd = convertView.findViewById(R.id.startEndButton);
        if (chain.status > 0)bStartEnd.setText("Cancel");
        else if(chain.bondsList.size() == 0)bStartEnd.setEnabled(false);
        bStartEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bStartEnd.setEnabled(false);
                Chains.ChainsTask exec = new Chains.ChainsTask(new Chains.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                    }
                });
                if(chain.status == 0)exec.execute("GPIO_ChainExecute",String.valueOf(chain.id));
                else exec.execute("GPIO_ChainCancel",String.valueOf(chain.id));
            }
        });
        LinearLayout llBonds = convertView.findViewById(R.id.bonds);
        for (final ChainBond bond:chain.bondsList) {
            RelativeLayout bondView = (RelativeLayout) layoutInflater.inflate(R.layout.chain_bond_elem, null);
            TextView lp = bondView.findViewById(R.id.lp);
            lp.setText(String.valueOf(bond.lp)+".");
            //bondView.setPadding(5,5,0,10);
            bondView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(alpha);
                    bond.bondDialog(true,context,Chains.gpio_o,Chains.gpio_pwm,Chains.actions);
                }
            });
            TextView targetName = bondView.findViewById(R.id.targetname);
            targetName.setText(bond.targetName);
            TextView targetSet = bondView.findViewById(R.id.set);
            targetSet.setText(bond.targetSet);
            TextView daley = bondView.findViewById(R.id.daley);
            Date Ddate = new Date((long)( bond.deley*1000));
            daley.setText(sdfSeconds.format(Ddate));
            llBonds.addView(bondView);
        }

        final PopupMenu popup = new PopupMenu(context, convertView);
        popup.getMenuInflater().inflate(R.menu.chain_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //item.getActionView().startAnimation(alpha);
                        switch (item.getItemId()) {
                            case R.id.edit:
                                chain.chainDialog(true,context);
                                break;
                            case R.id.addBond:
                                ChainBond bond = new ChainBond(chain.id);
                                bond.bondDialog(false,context,Chains.gpio_o,Chains.gpio_pwm,Chains.actions);
                                break;
                            case R.id.setOrder:
                                chain.setBondOrderDialog(context,sdfSeconds);
                                break;

                        }
                        return true;
                    }
                });
        convertView.setClickable(true);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(alpha);
                if(chain.status == 0)popup.show();
            }
        });


        return convertView;
    }


}
