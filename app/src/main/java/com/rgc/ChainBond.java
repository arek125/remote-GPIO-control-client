package com.rgc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private boolean editMode = false,bondLinked = false;
    public int id,lp,id_c,deley,A_id,Out_id,Out_Stan,Pwm_id,Pwm_ss,rfId,cmdId,linkId,C_id;
    public String typ="output",targetSet,targetName,Pwm_fr,Pwm_dc,outputName,pwmName,actionName,rfName,cmdName,linkName,linkedDeviceName;
    Connection c;
    int idU;
    Context context;
    Spinner targetO,targetPWM,targetActions,targetRF,targetCmd,targetChain;
    SparseArray<String> targetOList= new SparseArray<>(),targetPWMList= new SparseArray<>(),targetActionsList= new SparseArray<>(),targetRFsList= new SparseArray<>(),targetCmdsList= new SparseArray<>(),linkedPis = new SparseArray<>(),targetChainsList= new SparseArray<>();
    ChainBond(int id_c){
        this.id_c = id_c;
    }
//    ChainBond(int id, int lp, int id_c, int deley, int A_id, String typ, String targetName){
////        this.id = id;this.lp = lp;this.id_c = id_c;this.deley = deley;this.A_id = A_id;
////        this.typ = typ;this.targetName = targetName;
////        calcTargetSet();
////    }
////    ChainBond(int id, int lp, int id_c, int deley, int Out_id,int Out_Stan, String typ, String targetName){
////        this.id = id;this.lp = lp;this.id_c = id_c;this.deley = deley;this.Out_id = Out_id;this.Out_Stan = Out_Stan;
////        this.typ = typ;this.targetName = targetName;
////        calcTargetSet();
////    }
////    ChainBond(int id, int lp, int id_c, int deley, int Pwm_id,int Pwm_ss,String Pwm_fr,String Pwm_dc, String typ, String targetName){
////        this.id = id;this.lp = lp;this.id_c = id_c;this.deley = deley;this.Pwm_id = Pwm_id;this.Pwm_ss = Pwm_ss;this.Pwm_fr = Pwm_fr;this.Pwm_dc = Pwm_dc;
////        this.typ = typ;this.targetName = targetName;
////        calcTargetSet();
////    }
    ChainBond(String id, String id_c, String lp, String deley, String typ, String A_id, String Out_id, String Out_Stan, String Pwm_id, String Pwm_fr, String Pwm_dc, String Pwm_ss, String outputName, String pwmName, String actionName, String rfId, String rfName,
              String cmdId, String cmdName, String linkId, String linkName, String linkedDeviceName, String C_id){
        this.id = parseInt(id);
        this.id_c = parseInt(id_c);
        this.lp = parseInt(lp);
        this.deley = parseInt(deley);
        this.typ = typ;
        this.A_id = parseInt(A_id);
        this.Out_id = parseInt(Out_id);
        this.Out_Stan = parseInt(Out_Stan);
        this.Pwm_id = parseInt(Pwm_id);
        this.Pwm_fr = Pwm_fr;
        this.Pwm_dc = Pwm_dc;
        this.Pwm_ss = parseInt(Pwm_ss);
        this.outputName = outputName;
        this.pwmName = pwmName;
        this.actionName = actionName;
        this.rfId = parseInt(rfId);
        this.rfName = rfName;
        this.cmdId = parseInt(cmdId);
        this.cmdName = cmdName;
        this.linkId = parseInt(linkId);
        this.linkName = linkName;
        this.linkedDeviceName = linkedDeviceName;
        this.C_id = parseInt(C_id);
        switch (typ){
            case "output":
                targetName = outputName;
                if(this.Out_Stan == 0) targetSet = "OFF";
                else if(this.Out_Stan == 1)targetSet = "ON";
                else targetSet = "OPPOSITE";
                break;
            case "pwm":
                targetName = pwmName;
                if(this.Pwm_ss == 0) targetSet = "OFF";
                else if(this.Pwm_ss == 1)targetSet = "ON";
                else targetSet = "OPPOSITE";
                if(!Pwm_dc.isEmpty())targetSet+="/"+Pwm_dc+"%";
                if(!Pwm_fr.isEmpty())targetSet+="/"+Pwm_fr+"Hz";
                break;
            case "action":
                targetName = actionName;
                targetSet = "Execude";
                break;
            case "rfsend":
                targetName = rfName;
                targetSet = "Transmit";
                break;
            case "cmd":
                targetName = cmdName;
                targetSet = "Execude";
                break;
            case "chain":
                targetName = "?";
                targetSet = "Execude";
        }
        if(this.linkId!=0){
            targetName = linkedDeviceName+":"+linkName;
            bondLinked = true;
        }
    }


    public static Integer parseInt(String val){
        int parsedVal = 0;
        try{
            parsedVal = Integer.parseInt(val);
        }catch (NumberFormatException e){
        }
        return parsedVal;
    }

    public void reFetchTargetsData(){
        String linkedData = "";
        if(bondLinked && linkId !=0)linkedData="CallLinkedPi;"+String.valueOf(linkId)+";";
        targetO.setAdapter(null);
        new GetAsyncData(new GetAsyncData.AsyncResponse() {
            @Override
            public void processFinish(List<String> list) {
                targetOList.clear();
                for (int i = 2; i < list.size()-1; i+=4) {
                    targetOList.put(Integer.parseInt(list.get(i)),list.get(i+1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetOList));
                adapter.setDropDownViewResource(R.layout.spinner_item);
                targetO.setAdapter(adapter);
                if(editMode && typ.equals("output"))targetO.setSelection(targetOList.indexOfKey(Out_id));
            }
            @Override
            public void processFail(String error) {

            }
        },context,c,idU,1024,null,null).execute(linkedData+"GPIO_Oname");
        targetPWM.setAdapter(null);
        new GetAsyncData(new GetAsyncData.AsyncResponse() {
            @Override
            public void processFinish(List<String> list) {
                targetPWMList.clear();
                for (int i = 2; i < list.size()-1; i+=2) {
                    targetPWMList.put(Integer.parseInt(list.get(i)),list.get(i+1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetPWMList));
                adapter.setDropDownViewResource(R.layout.spinner_item);
                targetPWM.setAdapter(adapter);
                if(editMode && typ.equals("pwm"))targetPWM.setSelection(targetPWMList.indexOfKey(Pwm_id));
            }
            @Override
            public void processFail(String error) {

            }
        },context,c,idU,1024,null,null).execute(linkedData+"GPIO_PWMnames");
        targetActions.setAdapter(null);
        new GetAsyncData(new GetAsyncData.AsyncResponse() {
            @Override
            public void processFinish(List<String> list) {
                targetActionsList.clear();
                for (int i = 2; i < list.size()-1; i+=2) {
                    targetActionsList.put(Integer.parseInt(list.get(i)),list.get(i+1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetActionsList));
                adapter.setDropDownViewResource(R.layout.spinner_item);
                targetActions.setAdapter(adapter);
                if(editMode && typ.equals("action"))targetActions.setSelection(targetActionsList.indexOfKey(A_id));
            }
            @Override
            public void processFail(String error) {

            }
        },context,c,idU,1024,null,null).execute(linkedData+"GPIO_ActionsNames");
        targetRF.setAdapter(null);
        new GetAsyncData(new GetAsyncData.AsyncResponse() {
            @Override
            public void processFinish(List<String> list) {
                targetRFsList.clear();
                for (int i = 2; i < list.size()-1; i+=8) {
                    targetRFsList.put(Integer.parseInt(list.get(i)),list.get(i+1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetRFsList));
                adapter.setDropDownViewResource(R.layout.spinner_item);
                targetRF.setAdapter(adapter);
                if(editMode && typ.equals("rfsend"))targetRF.setSelection(targetRFsList.indexOfKey(rfId));
            }
            @Override
            public void processFail(String error) {

            }
        },context,c,idU,1024,null,null).execute(linkedData+"GetRfCodes");
        targetCmd.setAdapter(null);
        new GetAsyncData(new GetAsyncData.AsyncResponse() {
            @Override
            public void processFinish(List<String> list) {
                targetCmdsList.clear();
                for (int i = 2; i < list.size()-1; i+=4) {
                    targetCmdsList.put(Integer.parseInt(list.get(i)),list.get(i+1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetCmdsList));
                adapter.setDropDownViewResource(R.layout.spinner_item);
                targetCmd.setAdapter(adapter);
                if(editMode && typ.equals("rfsend"))targetCmd.setSelection(targetCmdsList.indexOfKey(cmdId));
            }
            @Override
            public void processFail(String error) {

            }
        },context,c,idU,1024,null,null).execute(linkedData+"GetCustomCmds");
        targetChain.setAdapter(null);
        new GetAsyncData(new GetAsyncData.AsyncResponse() {
            @Override
            public void processFinish(List<String> list) {
                targetChainsList.clear();
                for (int i = 2; i < list.size()-1; i+=2) {
                    if(Integer.parseInt(list.get(i)) != id_c)
                        targetChainsList.put(Integer.parseInt(list.get(i)),list.get(i+1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetChainsList));
                adapter.setDropDownViewResource(R.layout.spinner_item);
                targetChain.setAdapter(adapter);
                if(editMode && typ.equals("chain"))targetChain.setSelection(targetChainsList.indexOfKey(C_id));
            }
            @Override
            public void processFail(String error) {

            }
        },context,c,idU,1024,null,null).execute(linkedData+"Chain_names");
    }




    public void bondDialog(final boolean editMode, final Context context, Connection c,int idU){
        this.c = c;
        this.idU = idU;
        this.context = context;
        this.editMode = editMode;
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); ;
        View view = inflater.inflate(R.layout.bond_dialog, null);
        final Spinner linkedPi = view.findViewById(R.id.linkedpi);
        GetAsyncData execad = new GetAsyncData(new GetAsyncData.AsyncResponse() {
            @Override
            public void processFinish(List<String> list) {
                linkedPis.clear();
                for (int i = 2; i < list.size()-1; i+=6) {
                    linkedPis.put(Integer.parseInt(list.get(i)),list.get(i+1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(linkedPis));
                adapter.setDropDownViewResource(R.layout.spinner_item);
                linkedPi.setAdapter(adapter);
                if(editMode)linkedPi.setSelection(linkedPis.indexOfKey(linkId));
            }
            @Override
            public void processFail(String error) {

            }
        },context,c,idU,1024,null,null);
        execad.execute("GetLinkedPis","0");
        TextView title = view.findViewById(R.id.titleL);
        final Spinner type = view.findViewById(R.id.type);
        List<String> typeList = new ArrayList<String>();
        typeList.add("output");
        typeList.add("pwm");
        typeList.add("action");
        typeList.add("rfsend");
        typeList.add("cmd");
        typeList.add("chain");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, typeList);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        type.setAdapter(typeAdapter);
        final EditText daleyE = view.findViewById(R.id.daley);
        final CheckBox linkedPiCh = view.findViewById(R.id.linkedPiCh);
        targetO = view.findViewById(R.id.targeto);
//        ArrayAdapter<String> targetOAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetOList));
//        targetOAdapter.setDropDownViewResource(R.layout.spinner_item);
//        targetO.setAdapter(targetOAdapter);
        targetPWM = view.findViewById(R.id.targetpwm);
//        ArrayAdapter<String> targetPWMAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetPWMList));
//        targetPWMAdapter.setDropDownViewResource(R.layout.spinner_item);
//        targetPWM.setAdapter(targetPWMAdapter);
        targetActions = view.findViewById(R.id.targetActions);
//        ArrayAdapter<String> targetActionsAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, AdvScheduledAction.ConvertToList(targetActionsList));
//        targetActionsAdapter.setDropDownViewResource(R.layout.spinner_item);
//        targetActions.setAdapter(targetActionsAdapter);
        targetRF = view.findViewById(R.id.targetRF);
        targetCmd = view.findViewById(R.id.targetCmd);
        targetChain = view.findViewById(R.id.targetChain);
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
        final TableRow linkedPiRow = view.findViewById(R.id.linkedpiRow);
        final TableRow rfRow = view.findViewById(R.id.targetRFRow);
        final TableRow cmdRow = view.findViewById(R.id.targetCmdRow);
        final TableRow chainRow = view.findViewById(R.id.targetChainRow);
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
                    rfRow.setVisibility(View.GONE);
                    cmdRow.setVisibility(View.GONE);
                    chainRow.setVisibility(View.GONE);
                } else if (position == 1) {
                    targetoRow.setVisibility(View.GONE);
                    stateRow.setVisibility(View.VISIBLE);
                    targetpwmRow.setVisibility(View.VISIBLE);
                    pwmFRRow.setVisibility(View.VISIBLE);
                    pwmDCRow.setVisibility(View.VISIBLE);
                    targetActionsRow.setVisibility(View.GONE);
                    rfRow.setVisibility(View.GONE);
                    cmdRow.setVisibility(View.GONE);
                    chainRow.setVisibility(View.GONE);
                }else if (position == 2) {
                    targetoRow.setVisibility(View.GONE);
                    stateRow.setVisibility(View.GONE);
                    targetpwmRow.setVisibility(View.GONE);
                    pwmFRRow.setVisibility(View.GONE);
                    pwmDCRow.setVisibility(View.GONE);
                    targetActionsRow.setVisibility(View.VISIBLE);
                    rfRow.setVisibility(View.GONE);
                    cmdRow.setVisibility(View.GONE);
                    chainRow.setVisibility(View.GONE);
                }else if(position == 3){
                    targetoRow.setVisibility(View.GONE);
                    stateRow.setVisibility(View.GONE);
                    targetpwmRow.setVisibility(View.GONE);
                    pwmFRRow.setVisibility(View.GONE);
                    pwmDCRow.setVisibility(View.GONE);
                    targetActionsRow.setVisibility(View.GONE);
                    rfRow.setVisibility(View.VISIBLE);
                    cmdRow.setVisibility(View.GONE);
                    chainRow.setVisibility(View.GONE);
                }else if(position == 4){
                    targetoRow.setVisibility(View.GONE);
                    stateRow.setVisibility(View.GONE);
                    targetpwmRow.setVisibility(View.GONE);
                    pwmFRRow.setVisibility(View.GONE);
                    pwmDCRow.setVisibility(View.GONE);
                    targetActionsRow.setVisibility(View.GONE);
                    rfRow.setVisibility(View.GONE);
                    cmdRow.setVisibility(View.VISIBLE);
                    chainRow.setVisibility(View.GONE);
                }else if(position == 5){
                    targetoRow.setVisibility(View.GONE);
                    stateRow.setVisibility(View.VISIBLE);
                    targetpwmRow.setVisibility(View.GONE);
                    pwmFRRow.setVisibility(View.GONE);
                    pwmDCRow.setVisibility(View.GONE);
                    targetActionsRow.setVisibility(View.GONE);
                    rfRow.setVisibility(View.GONE);
                    cmdRow.setVisibility(View.GONE);
                    chainRow.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        linkedPiCh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    bondLinked = true;
                    linkedPiRow.setVisibility(View.VISIBLE);
                }else{
                    bondLinked = false;
                    linkId = 0;
                    linkedDeviceName = "";
                    linkedPiRow.setVisibility(View.GONE);
                }
                reFetchTargetsData();

            }
        });
        linkedPi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                linkId = linkedPis.keyAt(position);
                linkedDeviceName = linkedPis.valueAt(position);
                reFetchTargetsData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //linkId = 0;
                //reFetchTargetsData();
            }
        });
        if(editMode){
            title.setText("Edit chain bond:");
            type.setSelection(typeAdapter.getPosition(typ));
            daleyE.setText(String.valueOf(deley));
            linkedPiCh.setChecked(bondLinked);
            if(!bondLinked)reFetchTargetsData();
            if(type.getSelectedItemPosition() == 0) {
                //targetO.setSelection(targetOList.indexOfKey(Out_id));
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
        else
            reFetchTargetsData();

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
                                (type.getSelectedItemPosition() == 2 &&(targetActions.getSelectedItem() == null||daleyE.getText().toString().isEmpty()))||
                                (type.getSelectedItemPosition() == 3 &&(targetRF.getSelectedItem() == null||daleyE.getText().toString().isEmpty()))||
                                (type.getSelectedItemPosition() == 4 &&(targetCmd.getSelectedItem() == null||daleyE.getText().toString().isEmpty()))||
                                (type.getSelectedItemPosition() == 5 &&(targetChain.getSelectedItem() == null||daleyE.getText().toString().isEmpty()))
                                )
                            Toast.makeText(context, "Fill in daley and choose target !", Toast.LENGTH_SHORT).show();
                        else if(bondLinked && linkId == 0)Toast.makeText(context, "Choose lined device !", Toast.LENGTH_SHORT).show();
                        else{
                            dialog.dismiss();
                            int target = -1;//String.valueOf((type.getSelectedItemPosition() == 0)?targetOList.keyAt(targetO.getSelectedItemPosition()):targetPWMList.keyAt(targetPWM.getSelectedItemPosition())
                            String linkName = "";
                            if(type.getSelectedItemPosition() == 0){
                                target = targetOList.keyAt(targetO.getSelectedItemPosition());
                                linkName = targetO.getSelectedItem().toString();
                            }
                            else if(type.getSelectedItemPosition() == 1) {
                                target = targetPWMList.keyAt(targetPWM.getSelectedItemPosition());
                                linkName = targetPWM.getSelectedItem().toString();
                            }
                            else if(type.getSelectedItemPosition() == 2) {
                                target = targetActionsList.keyAt(targetActions.getSelectedItemPosition());
                                linkName = targetActions.getSelectedItem().toString();
                            }
                            else if(type.getSelectedItemPosition() == 3) {
                                target = targetRFsList.keyAt(targetRF.getSelectedItemPosition());
                                linkName = targetRF.getSelectedItem().toString();
                            }
                            else if(type.getSelectedItemPosition() == 4){
                                target = targetCmdsList.keyAt(targetCmd.getSelectedItemPosition());
                                linkName = targetCmd.getSelectedItem().toString();
                            }else if(type.getSelectedItemPosition() == 5){
                                target = targetChainsList.keyAt(targetChain.getSelectedItemPosition());
                                linkName = targetChain.getSelectedItem().toString();
                            }
                            if(!editMode)
                                exec.execute("GPIO_ChainBondAdd",String.valueOf(id_c),type.getSelectedItem().toString(),daleyE.getText().toString(),String.valueOf(target),String.valueOf(state.getSelectedItemPosition()),pwmFR.getText().toString(),pwmDC.getText().toString(),String.valueOf(linkId),linkName);
                            else
                                exec.execute("GPIO_ChainBondUpdate",String.valueOf(id_c),type.getSelectedItem().toString(),daleyE.getText().toString(),String.valueOf(target),String.valueOf(state.getSelectedItemPosition()),pwmFR.getText().toString(),pwmDC.getText().toString(),String.valueOf(id),String.valueOf(linkId),linkName);
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
