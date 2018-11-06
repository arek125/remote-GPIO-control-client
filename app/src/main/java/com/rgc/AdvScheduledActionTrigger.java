package com.rgc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class AdvScheduledActionTrigger {
    public int id,lp,id_a;
    public String id_s,warunek,operator,dane,iosp_name,sensor_unit;
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");
    AdvScheduledActionTrigger(int id_a){
        this.id_a = id_a;
    }

    AdvScheduledActionTrigger(int id,int id_a, int lp, String warunek, String operator, String dane){
        this.id = id; this.id_a = id_a; this.lp = lp; this.warunek = warunek; this.operator = operator; this.dane = dane;
    }
    AdvScheduledActionTrigger(int id,int id_a, int lp, String warunek, String operator, String dane, String id_s, String iosp_name, String sensor_unit){
        this.id = id; this.id_a = id_a; this.lp = lp; this.warunek = warunek; this.operator = operator; this.dane = dane;
        this.id_s = id_s; this.iosp_name=iosp_name; this.sensor_unit = sensor_unit;
    }

    public void triggerDialog(final boolean editmode,final Context context,final SparseArray sourceIOList, final SparseArray sourcePWMList, Map<String,String> sensors){
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sdfDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdfHour.setTimeZone(TimeZone.getTimeZone("UTC"));
        View view = inflater.inflate(R.layout.trigger_edit, null);
        final Calendar cal = Calendar.getInstance();
        final TextView title = view.findViewById(R.id.titleL);
        final Spinner type = view.findViewById(R.id.type);
        final ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(context, R.array.triggersType, android.R.layout.simple_spinner_item);
        //type.setAdapter(typeAdapter);
        final EditText op = view.findViewById(R.id.op);
        final DatePicker date = view.findViewById(R.id.date);
        final TimePicker time = view.findViewById(R.id.hour);
        time.setIs24HourView(true);
        final NumberPicker tmhours = (NumberPicker) view.findViewById(R.id.timerhours);
        tmhours.setMaxValue(23);
        tmhours.setMinValue(0);
        final NumberPicker tmminutes = (NumberPicker) view.findViewById(R.id.timerminutes);
        tmminutes.setMaxValue(59);
        tmminutes.setMinValue(0);
        final NumberPicker tmseconds = (NumberPicker) view.findViewById(R.id.timerseconds);
        tmseconds.setMaxValue(59);
        tmseconds.setMinValue(0);
        final Spinner weekday = view.findViewById(R.id.weekday);
        //final ArrayAdapter<CharSequence> weekdayAdapter = ArrayAdapter.createFromResource(context, R.array.weekdays, android.R.layout.simple_spinner_item);
        final Spinner sensor = view.findViewById(R.id.sensor);
        final List<String> sensorNames = new ArrayList<String>();
        sensorNames.addAll(sensors.values());
        final List<String> sensorIds = new ArrayList<String>();
        sensorIds.addAll(sensors.keySet());
        final ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, sensorNames);
        sensorAdapter.setDropDownViewResource(R.layout.spinner_item);
        sensor.setAdapter(sensorAdapter);
        final EditText sensorValue = view.findViewById(R.id.sensorValue);
        final Spinner io = view.findViewById(R.id.io);
        ArrayAdapter<String> ioAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item,AdvScheduledAction.ConvertToList(sourceIOList));
        ioAdapter.setDropDownViewResource(R.layout.spinner_item);
        io.setAdapter(ioAdapter);
        final Spinner ioState = view.findViewById(R.id.ioState);
        final ArrayAdapter<CharSequence> statesAdapter = ArrayAdapter.createFromResource(context, R.array.states_two, android.R.layout.simple_spinner_item);
        List<String> pwmNames = AdvScheduledAction.ConvertToList(sourcePWMList);
        final Spinner pwm_ss = view.findViewById(R.id.pwmSS);
        final Spinner pwm_ssState = view.findViewById(R.id.pwmSSState);
        ArrayAdapter<String> pwm_ssAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item,pwmNames);
        pwm_ssAdapter.setDropDownViewResource(R.layout.spinner_item);
        pwm_ss.setAdapter(pwm_ssAdapter);
        final Spinner pwm_fr = view.findViewById(R.id.pwmFR);
        pwm_fr.setAdapter(pwm_ssAdapter);
        final EditText pwm_frValue = view.findViewById(R.id.pwmFRValue);
        final Spinner pwm_dc = view.findViewById(R.id.pwmDC);
        pwm_dc.setAdapter(pwm_ssAdapter);
        final EditText pwm_dcValue = view.findViewById(R.id.pwmDCValue);
        final CheckBox trueFalse = view.findViewById(R.id.falseTrue);
        final CheckBox pingtrueFalse = view.findViewById(R.id.ping);
        final EditText pingHost = view.findViewById(R.id.pingHost);

        final TableRow dateRow = view.findViewById(R.id.dateRow);
        final TableRow hourRow = view.findViewById(R.id.hourRow);
        final TableRow timerRow = view.findViewById(R.id.timerRow);
        final TableRow weekdayRow = view.findViewById(R.id.weekdayRow);
        final TableRow sensorRow = view.findViewById(R.id.sensorRow);
        final TableRow ioRow = view.findViewById(R.id.ioRow);
        final TableRow pwm_ssRow = view.findViewById(R.id.pwmSSRow);
        final TableRow pwm_frRow = view.findViewById(R.id.pwmFRRow);
        final TableRow pwm_dcRow = view.findViewById(R.id.pwmDCRow);
        final TableRow trueFalseRow = view.findViewById(R.id.falseTrueRow);
        final TableRow pingRow = view.findViewById(R.id.pingRow);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cal.setTimeZone(TimeZone.getDefault());
                //typeAdapter.notifyDataSetChanged();
                switch (position){
                    case 0:
                        dateRow.setVisibility(View.VISIBLE);
                        hourRow.setVisibility(View.VISIBLE);
                        timerRow.setVisibility(View.GONE);
                        weekdayRow.setVisibility(View.GONE);
                        sensorRow.setVisibility(View.GONE);
                        ioRow.setVisibility(View.GONE);
                        pwm_ssRow.setVisibility(View.GONE);
                        pwm_frRow.setVisibility(View.GONE);
                        pwm_dcRow.setVisibility(View.GONE);
                        trueFalseRow.setVisibility(View.GONE);
                        pingRow.setVisibility(View.GONE);
                        break;
                    case 1:
                        dateRow.setVisibility(View.GONE);
                        hourRow.setVisibility(View.VISIBLE);
                        timerRow.setVisibility(View.GONE);
                        weekdayRow.setVisibility(View.GONE);
                        sensorRow.setVisibility(View.GONE);
                        ioRow.setVisibility(View.GONE);
                        pwm_ssRow.setVisibility(View.GONE);
                        pwm_frRow.setVisibility(View.GONE);
                        pwm_dcRow.setVisibility(View.GONE);
                        trueFalseRow.setVisibility(View.GONE);
                        pingRow.setVisibility(View.GONE);
                        break;
                    case 2:
                        dateRow.setVisibility(View.GONE);
                        hourRow.setVisibility(View.GONE);
                        timerRow.setVisibility(View.VISIBLE);
                        weekdayRow.setVisibility(View.GONE);
                        sensorRow.setVisibility(View.GONE);
                        ioRow.setVisibility(View.GONE);
                        pwm_ssRow.setVisibility(View.GONE);
                        pwm_frRow.setVisibility(View.GONE);
                        pwm_dcRow.setVisibility(View.GONE);
                        trueFalseRow.setVisibility(View.GONE);
                        pingRow.setVisibility(View.GONE);
                        break;
                    case 3:
                        dateRow.setVisibility(View.GONE);
                        hourRow.setVisibility(View.GONE);
                        timerRow.setVisibility(View.GONE);
                        weekdayRow.setVisibility(View.VISIBLE);
                        sensorRow.setVisibility(View.GONE);
                        ioRow.setVisibility(View.GONE);
                        pwm_ssRow.setVisibility(View.GONE);
                        pwm_frRow.setVisibility(View.GONE);
                        pwm_dcRow.setVisibility(View.GONE);
                        trueFalseRow.setVisibility(View.GONE);
                        pingRow.setVisibility(View.GONE);
                        break;
                    case 4:
                        dateRow.setVisibility(View.GONE);
                        hourRow.setVisibility(View.GONE);
                        timerRow.setVisibility(View.GONE);
                        weekdayRow.setVisibility(View.GONE);
                        sensorRow.setVisibility(View.VISIBLE);
                        ioRow.setVisibility(View.GONE);
                        pwm_ssRow.setVisibility(View.GONE);
                        pwm_frRow.setVisibility(View.GONE);
                        pwm_dcRow.setVisibility(View.GONE);
                        trueFalseRow.setVisibility(View.GONE);
                        pingRow.setVisibility(View.GONE);
                        break;
                    case 5:
                        dateRow.setVisibility(View.GONE);
                        hourRow.setVisibility(View.GONE);
                        timerRow.setVisibility(View.GONE);
                        weekdayRow.setVisibility(View.GONE);
                        sensorRow.setVisibility(View.GONE);
                        ioRow.setVisibility(View.VISIBLE);
                        pwm_ssRow.setVisibility(View.GONE);
                        pwm_frRow.setVisibility(View.GONE);
                        pwm_dcRow.setVisibility(View.GONE);
                        trueFalseRow.setVisibility(View.GONE);
                        pingRow.setVisibility(View.GONE);
                        break;
                    case 6:
                        dateRow.setVisibility(View.GONE);
                        hourRow.setVisibility(View.GONE);
                        timerRow.setVisibility(View.GONE);
                        weekdayRow.setVisibility(View.GONE);
                        sensorRow.setVisibility(View.GONE);
                        ioRow.setVisibility(View.GONE);
                        pwm_ssRow.setVisibility(View.VISIBLE);
                        pwm_frRow.setVisibility(View.GONE);
                        pwm_dcRow.setVisibility(View.GONE);
                        trueFalseRow.setVisibility(View.GONE);
                        pingRow.setVisibility(View.GONE);
                        break;
                    case 7:
                        dateRow.setVisibility(View.GONE);
                        hourRow.setVisibility(View.GONE);
                        timerRow.setVisibility(View.GONE);
                        weekdayRow.setVisibility(View.GONE);
                        sensorRow.setVisibility(View.GONE);
                        ioRow.setVisibility(View.GONE);
                        pwm_ssRow.setVisibility(View.GONE);
                        pwm_frRow.setVisibility(View.VISIBLE);
                        pwm_dcRow.setVisibility(View.GONE);
                        trueFalseRow.setVisibility(View.GONE);
                        pingRow.setVisibility(View.GONE);
                        break;
                    case 8:
                        dateRow.setVisibility(View.GONE);
                        hourRow.setVisibility(View.GONE);
                        timerRow.setVisibility(View.GONE);
                        weekdayRow.setVisibility(View.GONE);
                        sensorRow.setVisibility(View.GONE);
                        ioRow.setVisibility(View.GONE);
                        pwm_ssRow.setVisibility(View.GONE);
                        pwm_frRow.setVisibility(View.GONE);
                        pwm_dcRow.setVisibility(View.VISIBLE);
                        trueFalseRow.setVisibility(View.GONE);
                        pingRow.setVisibility(View.GONE);
                        break;
                    case 9:
                        dateRow.setVisibility(View.GONE);
                        hourRow.setVisibility(View.GONE);
                        timerRow.setVisibility(View.GONE);
                        weekdayRow.setVisibility(View.GONE);
                        sensorRow.setVisibility(View.GONE);
                        ioRow.setVisibility(View.GONE);
                        pwm_ssRow.setVisibility(View.GONE);
                        pwm_frRow.setVisibility(View.GONE);
                        pwm_dcRow.setVisibility(View.GONE);
                        trueFalseRow.setVisibility(View.VISIBLE);
                        pingRow.setVisibility(View.GONE);
                        break;
                    case 10:
                        dateRow.setVisibility(View.GONE);
                        hourRow.setVisibility(View.GONE);
                        timerRow.setVisibility(View.GONE);
                        weekdayRow.setVisibility(View.GONE);
                        sensorRow.setVisibility(View.GONE);
                        ioRow.setVisibility(View.GONE);
                        pwm_ssRow.setVisibility(View.GONE);
                        pwm_frRow.setVisibility(View.GONE);
                        pwm_dcRow.setVisibility(View.GONE);
                        trueFalseRow.setVisibility(View.GONE);
                        pingRow.setVisibility(View.VISIBLE);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(editmode){
            title.setText("Edit trigger:");
            type.setSelection(typeAdapter.getPosition(warunek));
            op.setText(operator);
            switch (warunek){
                case "date":
                    try {
                        cal.setTime(sdfDate.parse(dane));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    date.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    time.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
                    time.setCurrentMinute(cal.get(Calendar.MINUTE));
                    break;
                case "hour":
                    try {
                        cal.setTime(sdfDate.parse(AdvSAListAdapter.currentUTC("yyyy-MM-dd")+" "+dane));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    time.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
                    time.setCurrentMinute(cal.get(Calendar.MINUTE));
                    break;
                case "timer":
                    String[] timelist = dane.split(",");
                    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                    cal.setTimeInMillis(Long.parseLong( timelist[0])*1000);
                    tmhours.setValue(cal.get(Calendar.HOUR_OF_DAY));
                    tmminutes.setValue(cal.get(Calendar.MINUTE));
                    tmseconds.setValue(cal.get(Calendar.SECOND));
                    break;
                case "weekday":
                    weekday.setSelection(Integer.parseInt(dane));
                    break;
                case "sensor":
                    sensor.setSelection(sensorIds.indexOf(id_s));
                    sensorValue.setText(dane);
                    break;
                case "i/o":
                    io.setSelection(sourceIOList.indexOfKey(Integer.parseInt(id_s)));
                    ioState.setSelection(statesAdapter.getPosition(dane));
                    break;
                case "pwm state":
                    pwm_ss.setSelection(sourcePWMList.indexOfKey(Integer.parseInt(id_s)));
                    pwm_ssState.setSelection(statesAdapter.getPosition(dane));
                    break;
                case "pwm fr":
                    pwm_fr.setSelection(sourcePWMList.indexOfKey(Integer.parseInt(id_s)));
                    pwm_frValue.setText(dane);
                    break;
                case "pwm dc":
                    pwm_dc.setSelection(sourcePWMList.indexOfKey(Integer.parseInt(id_s)));
                    pwm_dcValue.setText(dane);
                    break;
                case "in chain":
                    trueFalse.setChecked(Boolean.parseBoolean(dane));
                    break;
                case "ping":
                    pingtrueFalse.setChecked(Boolean.parseBoolean(dane));
                    pingHost.setText(id_s);
                    break;
            }
        }

        MaterialDialog d = new MaterialDialog.Builder(context)
                //.title(R.string.title)
                .customView(view, true)
                .autoDismiss(false)
                .positiveText("SAVE")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        cal.set(date.getYear(), date.getMonth(), date.getDayOfMonth(), time.getCurrentHour(), time.getCurrentMinute());
                        switch (type.getSelectedItem().toString()){
                            case "date":
                                dane = sdfDate.format(cal.getTime());
                                break;
                            case "hour":
                                dane = sdfHour.format(cal.getTime());
                                break;
                            case "timer":
                                dane = String.valueOf(((tmhours.getValue() * 3600) + (tmminutes.getValue() * 60)) + tmseconds.getValue());
                                dane = dane + "," + dane+","+(Long)((new Date().getTime())/1000);
                                break;
                            case "weekday":
                                dane = String.valueOf(weekday.getSelectedItemPosition());
                                break;
                            case "sensor":
                                id_s = sensorIds.get(sensor.getSelectedItemPosition());
                                dane = sensorValue.getText().toString();
                                break;
                            case "i/o":
                                id_s = String.valueOf(sourceIOList.keyAt(io.getSelectedItemPosition()));
                                dane = String.valueOf(ioState.getSelectedItemPosition());
                                break;
                            case "pwm state":
                                id_s = String.valueOf(sourcePWMList.keyAt(pwm_ss.getSelectedItemPosition()));
                                dane = String.valueOf(pwm_ssState.getSelectedItemPosition());
                                break;
                            case "pwm fr":
                                id_s = String.valueOf(sourcePWMList.keyAt(pwm_fr.getSelectedItemPosition()));
                                dane = pwm_frValue.getText().toString();
                                break;
                            case "pwm dc":
                                id_s = String.valueOf(sourcePWMList.keyAt(pwm_dc.getSelectedItemPosition()));
                                dane = pwm_dcValue.getText().toString();
                                break;
                            case "in chain":
                                if(trueFalse.isChecked())
                                    dane = "True";
                                else
                                    dane = "False";
                                break;
                            case "ping":
                                if(pingtrueFalse.isChecked())
                                    dane = "True";
                                else
                                    dane = "False";
                                id_s = pingHost.getText().toString();
                                break;
                        }
                        if((dane == null || dane.isEmpty()) || (type.getSelectedItem().toString().matches("sensor|i/o|pwm|ping") && (id_s == null|| id_s.isEmpty())))
                            Toast.makeText(context, "Fill all visible fields !", Toast.LENGTH_SHORT).show();
                        else if(!op.getText().toString().matches("^==|<=|>=|<|>|!=$"))
                            Toast.makeText(context, "Not allowed operator, must be one of (==|<=|>=|<|>|!=) !", Toast.LENGTH_SHORT).show();
                        else if(!editmode){
                            dialog.dismiss();
                            AdvScheduledActions.AdvScheduledActionsTask add_Trigger = new AdvScheduledActions.AdvScheduledActionsTask(new AdvScheduledActions.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                            }
                            });
                            add_Trigger.execute("GPIO_ASA_AddTrigger",String.valueOf(id_a),String.valueOf(id_s),type.getSelectedItem().toString(),op.getText().toString(),dane);
                        }
                        else{
                            dialog.dismiss();
                            AdvScheduledActions.AdvScheduledActionsTask update_Trigger = new AdvScheduledActions.AdvScheduledActionsTask(new AdvScheduledActions.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                }
                            });
                            update_Trigger.execute("GPIO_ASA_UpdateTrigger",String.valueOf(id_a),String.valueOf(id_s),type.getSelectedItem().toString(),op.getText().toString(),dane,String.valueOf(id));
                        }

                    }
                })
                .neutralText("DELETE")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        AdvScheduledActions.AdvScheduledActionsTask delete_Trigger = new AdvScheduledActions.AdvScheduledActionsTask(new AdvScheduledActions.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                            }
                        });
                        delete_Trigger.execute("GPIO_ASA_DeleteTrigger",String.valueOf(id),String.valueOf(id_a));
                    }
                })
                .negativeText("CANCEL")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        if(!editmode)
            d.getActionButton(DialogAction.NEUTRAL).setVisibility(View.INVISIBLE);



    }
}
