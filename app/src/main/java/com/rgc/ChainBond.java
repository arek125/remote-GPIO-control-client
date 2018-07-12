package com.rgc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChainBond {
    public int id,lp,id_c,deley,A_id,Out_id,Out_Stan,Pwm_id,Pwm_ss;
    public String typ,targetName,targetSet,Pwm_fr,Pwm_dc;;
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");
    ChainBond(int id_c){
        this.id_c = id_c;
    }
    ChainBond(int id, int lp, int id_c, int deley, int A_id, String typ, String targetName){
        this.id = id;this.lp = lp;this.id_c = id_c;this.deley = deley;this.A_id = A_id;
        this.typ = typ;this.targetName = targetName;
        calcTargetSet();
    }
    ChainBond(int id, int lp, int id_c, int deley, int Out_id,int Out_Stan, String typ, String targetName){
        this.id = id;this.lp = lp;this.id_c = id_c;this.deley = deley;this.Out_id = Out_id;this.Out_Stan = Out_Stan;
        this.typ = typ;this.targetName = targetName;
        calcTargetSet();
    }
    ChainBond(int id, int lp, int id_c, int deley, int Pwm_id,int Pwm_ss,String Pwm_fr,String Pwm_dc, String typ, String targetName){
        this.id = id;this.lp = lp;this.id_c = id_c;this.deley = deley;this.Pwm_id = Pwm_id;this.Pwm_ss = Pwm_ss;this.Pwm_fr = Pwm_fr;this.Pwm_dc = Pwm_dc;
        this.typ = typ;this.targetName = targetName;
        calcTargetSet();
    }

    private void calcTargetSet(){
        if (typ.equals("action")) targetSet = "Execude";
        else if (typ.equals("output")){
            if(Out_Stan == 0) targetSet = "OFF";
            else if(Out_Stan == 1)targetSet = "ON";
            else targetSet = "OPPOSITE";
        }
        else if (typ.equals("pwm")){
            if(Pwm_ss == 0) targetSet = "OFF";
            else if(Pwm_ss == 1)targetSet = "ON";
            else targetSet = "OPPOSITE";
            if(!Pwm_dc.isEmpty())targetSet+="/"+Pwm_dc+"%";
            if(!Pwm_fr.isEmpty())targetSet+="/"+Pwm_fr+"Hz";
        }
    }

    public void bondDialog(final boolean editMode, final Context context, final SparseArray targetOList,final SparseArray targetPWMList,final SparseArray targetActionsList){
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); ;
        View view = inflater.inflate(R.layout.bond_dialog, null);
        TextView title = view.findViewById(R.id.titleL);
        final Spinner type = view.findViewById(R.id.type);
        List<String> typeList = new ArrayList<String>();
        typeList.add("output");
        typeList.add("pwm");
        typeList.add("action");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, typeList);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        type.setAdapter(typeAdapter);
        final EditText daleyE = view.findViewById(R.id.daley);
        final Spinner targetO = view.findViewById(R.id.targeto);
        ArrayAdapter<String> targetOAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetOList));
        targetOAdapter.setDropDownViewResource(R.layout.spinner_item);
        targetO.setAdapter(targetOAdapter);
        final Spinner targetPWM = view.findViewById(R.id.targetpwm);
        ArrayAdapter<String> targetPWMAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetPWMList));
        targetPWMAdapter.setDropDownViewResource(R.layout.spinner_item);
        targetPWM.setAdapter(targetPWMAdapter);
        final Spinner targetActions = view.findViewById(R.id.targetActions);
        ArrayAdapter<String> targetActionsAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetActionsList));
        targetActionsAdapter.setDropDownViewResource(R.layout.spinner_item);
        targetActions.setAdapter(targetActionsAdapter);
        final Spinner state = (Spinner) view.findViewById(R.id.state);
        //final ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(context, R.array.states, android.R.layout.simple_spinner_item);
        final EditText pwmFR = view.findViewById(R.id.pwmFR);
        final EditText pwmDC = view.findViewById(R.id.pwmDC);
        final TableRow targetoRow = view.findViewById(R.id.targetoRow);
        final TableRow targetpwmRow = view.findViewById(R.id.targetpwmRow);
        final TableRow targetActionsRow = view.findViewById(R.id.targetActionsRow);
        final TableRow pwmFRRow = view.findViewById(R.id.pwmFRRow);
        final TableRow pwmDCRow = view.findViewById(R.id.pwmDCRow);
        final TableRow stateRow = view.findViewById(R.id.stateRow);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    targetoRow.setVisibility(View.VISIBLE);
                    stateRow.setVisibility(View.VISIBLE);
                    targetpwmRow.setVisibility(View.GONE);
                    pwmFRRow.setVisibility(View.GONE);
                    pwmDCRow.setVisibility(View.GONE);
                    targetActionsRow.setVisibility(View.GONE);
                } else if (position == 1) {
                    targetoRow.setVisibility(View.GONE);
                    stateRow.setVisibility(View.VISIBLE);
                    targetpwmRow.setVisibility(View.VISIBLE);
                    pwmFRRow.setVisibility(View.VISIBLE);
                    pwmDCRow.setVisibility(View.VISIBLE);
                    targetActionsRow.setVisibility(View.GONE);
                }else if (position == 2) {
                    targetoRow.setVisibility(View.GONE);
                    stateRow.setVisibility(View.GONE);
                    targetpwmRow.setVisibility(View.GONE);
                    pwmFRRow.setVisibility(View.GONE);
                    pwmDCRow.setVisibility(View.GONE);
                    targetActionsRow.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(editMode){
            title.setText("Edit chain bond:");
            type.setSelection(typeAdapter.getPosition(typ));
            daleyE.setText(String.valueOf(deley));
            if(type.getSelectedItemPosition() == 0) {
                targetO.setSelection(targetOList.indexOfKey(Out_id));
                state.setSelection(Out_Stan);
            }
            else if (type.getSelectedItemPosition() == 1){
                targetPWM.setSelection(targetPWMList.indexOfKey(Pwm_id));
                if(Pwm_fr != null)pwmFR.setText(Pwm_fr);
                if(Pwm_dc != null)pwmDC.setText(Pwm_dc);
                state.setSelection(Pwm_ss);
            }
            else if(type.getSelectedItemPosition() == 2) {
                targetActions.setSelection(targetActionsList.indexOfKey(A_id));
            }
        }

        final Chains.ChainsTask exec = new Chains.ChainsTask(new Chains.AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });

        MaterialDialog d = new MaterialDialog.Builder(context)
                //.title(R.string.title)
                .customView(view, true)
                .autoDismiss(false)
                .positiveText("SAVE")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if((type.getSelectedItemPosition() == 0 &&(targetO.getSelectedItem() == null||daleyE.getText().toString().isEmpty()))||
                                (type.getSelectedItemPosition() == 1 &&(targetPWM.getSelectedItem() == null||daleyE.getText().toString().isEmpty()))||
                                (type.getSelectedItemPosition() == 2 &&(targetActions.getSelectedItem() == null||daleyE.getText().toString().isEmpty()))
                                )
                            Toast.makeText(context, "Fill in daley and choose target !", Toast.LENGTH_SHORT).show();
                        else{
                            dialog.dismiss();
                            int target = -1;//String.valueOf((type.getSelectedItemPosition() == 0)?targetOList.keyAt(targetO.getSelectedItemPosition()):targetPWMList.keyAt(targetPWM.getSelectedItemPosition())
                            if(type.getSelectedItemPosition() == 0)
                                target = targetOList.keyAt(targetO.getSelectedItemPosition());
                            else if(type.getSelectedItemPosition() == 1)
                                target = targetPWMList.keyAt(targetPWM.getSelectedItemPosition());
                            else if(type.getSelectedItemPosition() == 2)
                                target = targetActionsList.keyAt(targetActions.getSelectedItemPosition());
                            if(!editMode)
                                exec.execute("GPIO_ChainBondAdd",String.valueOf(id_c),type.getSelectedItem().toString(),daleyE.getText().toString(),String.valueOf(target),String.valueOf(state.getSelectedItemPosition()),pwmFR.getText().toString(),pwmDC.getText().toString());
                            else
                                exec.execute("GPIO_ChainBondUpdate",String.valueOf(id_c),type.getSelectedItem().toString(),daleyE.getText().toString(),String.valueOf(target),String.valueOf(state.getSelectedItemPosition()),pwmFR.getText().toString(),pwmDC.getText().toString(),String.valueOf(id));
                        }
                    }
                })
                .neutralText("DELETE")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        exec.execute("GPIO_ChainBondDelete",String.valueOf(id),String.valueOf(id_c));
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

        if(!editMode)
            d.getActionButton(DialogAction.NEUTRAL).setVisibility(View.INVISIBLE);
    }
}
