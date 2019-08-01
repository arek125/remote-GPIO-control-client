package com.rgc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AdvSAListAdapter extends BaseAdapter {

    List<AdvScheduledAction> asaList;
    Context context;
    LayoutInflater layoutInflater;
    String[] namesOfDays = new String[] { "Monday", "Tuesday","Wednesday", "Thurseday","Friday", "Saturday", "Sunday" };
    String[] statuses = {"OFF","ON"};
    AlphaAnimation alpha = new AlphaAnimation(1F, 0.5F);
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdfSeconds = new SimpleDateFormat("HH:mm:ss");

    public AdvSAListAdapter(List<AdvScheduledAction> asaList, Context context) {
        super();
        this.asaList = asaList;
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

        return asaList.size();
    }

    @Override
    public AdvScheduledAction getItem(int position) {
        return asaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView= layoutInflater.inflate(R.layout.adv_sa_elem, null);
        //convertView.setBackgroundColor(Color.LTGRAY);
        final AdvScheduledAction advSA = asaList.get(position);
        TextView name = convertView.findViewById(R.id.name);
        name.setText(advSA.nazwa);
        TextView set = convertView.findViewById(R.id.set);
        set.setText(advSA.op_nazwa);
        TextView setted = convertView.findViewById(R.id.setted);
        if(advSA.typ.equals("output"))
            setted.setText(advSA.out_stan);
        else if (advSA.typ.equals("pwm")){
            setted.setText(advSA.pwm_ss);
            if(!advSA.pwm_dc.isEmpty())setted.append("/"+advSA.pwm_dc+"%");
            if(!advSA.pwm_fr.isEmpty())setted.append("/"+advSA.pwm_fr+"Hz");
        }
        else if (advSA.typ.equals("chain"))
            setted.setText(advSA.out_stan);

        TextView conj = convertView.findViewById(R.id.conj);
        LinearLayout llTriggers = convertView.findViewById(R.id.triggers);
        int i=1;
        String defConj="";
        for (final AdvScheduledActionTrigger triggerO:advSA.triggersList) {
            RelativeLayout triggerView = (RelativeLayout) layoutInflater.inflate(R.layout.adv_sa_trig, null);
            Calendar cal = Calendar.getInstance();
            TextView lp = triggerView.findViewById(R.id.lp);
            lp.setText(triggerO.lp+".");
            //triggerView.setPadding(5,5,0,10);
            triggerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    triggerO.triggerDialog(true,context,AdvScheduledActions.gpio_io,AdvScheduledActions.gpio_pwm,AdvScheduledActions.sensors);
                    v.startAnimation(alpha);
                }
            });
            TextView warunek = triggerView.findViewById(R.id.cond);
            if(triggerO.id_s != null)
                warunek.setText(triggerO.warunek + " "+ triggerO.iosp_name);
            else
                warunek.setText(triggerO.warunek);
            TextView op = triggerView.findViewById(R.id.op);
            op.setText(triggerO.operator);
            TextView dane = triggerView.findViewById(R.id.data);
            switch (triggerO.warunek){
                case "weekday":
                    dane.setText(namesOfDays[Integer.parseInt(triggerO.dane)]);
                    break;
                case "i/o":
                case "pwm state":
                    dane.setText(statuses[Integer.parseInt(triggerO.dane)]);
                    break;
                case "sensor":
                    dane.setText(triggerO.dane+triggerO.sensor_unit);
                    break;
                case "hour":
                    dane.setText(UTCtoLocalDate(triggerO.dane,"HH:mm",true));
                    break;
                case "date":
                    dane.setText(UTCtoLocalDate(triggerO.dane,"yyyy-MM-dd HH:mm",false));
                    break;
                case "timer":
                    String[] timelist = triggerO.dane.split(",");
                    Date cTdate = new Date((long)(Long.parseLong( timelist[1])*1000));
                    Date Tdate = new Date((long)(Long.parseLong( timelist[0])*1000));
                    dane.setText(sdfSeconds.format(Tdate)+ " | "+sdfSeconds.format(cTdate));
                    break;
                case "ping":
                    dane.setText(triggerO.dane+" | "+triggerO.id_s);
                    break;
                default:
                    dane.setText(triggerO.dane);
            }

            llTriggers.addView(triggerView);
            if(i==advSA.triggersList.size())
                defConj+="#"+i+"#";
            else
                defConj+="#"+i+"# and ";
            i++;
        }
        if(advSA.koniunkcja.equals("None")||advSA.koniunkcja.isEmpty())
            conj.setText(defConj);
        else
            conj.setText(advSA.koniunkcja);


        final PopupMenu popup = new PopupMenu(context, convertView);
        popup.getMenuInflater().inflate(R.menu.adv_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //item.getActionView().startAnimation(alpha);
                        switch (item.getItemId()) {
                            case R.id.edit:
                                advSA.actionDialog(true,context,AdvScheduledActions.gpio_o,AdvScheduledActions.gpio_pwm,AdvScheduledActions.chains);
                                break;
                            case R.id.addTrigger:
                                AdvScheduledActionTrigger advSAT = new AdvScheduledActionTrigger(advSA.id);
                                advSAT.triggerDialog(false,context,AdvScheduledActions.gpio_io,AdvScheduledActions.gpio_pwm,AdvScheduledActions.sensors);
                                break;
                            case R.id.setConj:
                                advSA.conjDialog(context);
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
                popup.show();
            }
        });


        return convertView;
    }

    public static String UTCtoLocalDate(String UTCdate,String pattern, boolean timeOnly){
        SimpleDateFormat oldFormatter = null;
        if(timeOnly){
            oldFormatter = new SimpleDateFormat("yyyy-MM-dd "+pattern);
            UTCdate = currentUTC("yyyy-MM-dd")+" "+UTCdate;
        }
        else
            oldFormatter = new SimpleDateFormat(pattern);
        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        String dueDateAsNormal ="";
        try {
            value = oldFormatter.parse(UTCdate);
            SimpleDateFormat newFormatter = new SimpleDateFormat(pattern);
            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dueDateAsNormal;
    }
    public static String UTCtoLocalDate(String UTCdate,String pattern, boolean timeOnly, String returnPattern){
        SimpleDateFormat oldFormatter = null;
        if(timeOnly){
            oldFormatter = new SimpleDateFormat("yyyy-MM-dd "+pattern);
            UTCdate = currentUTC("yyyy-MM-dd")+" "+UTCdate;
        }
        else
            oldFormatter = new SimpleDateFormat(pattern);
        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        String dueDateAsNormal ="";
        try {
            value = oldFormatter.parse(UTCdate);
            SimpleDateFormat newFormatter = new SimpleDateFormat(returnPattern);
            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dueDateAsNormal;
    }

    public static String currentUTC(String pattern){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }
    public static Date StringtoDate(String dtStart) {

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

    public static String DatetoString(Date date) {
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

    public static String SecondstoString(long l) {
        return String.format("%02d:%02d:%02d", new Object[]{
                Integer.valueOf((int) (l / 3600)), Integer.valueOf((int) ((l % 3600) / 60)), Integer.valueOf((int) (l % 60))
        });
    }

}
