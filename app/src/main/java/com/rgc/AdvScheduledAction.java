package com.rgc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvScheduledAction {
    public int id,out_id,pwm_id,chain_id;
    public boolean keepLog;
    public String typ,out_stan,rodzaj,koniunkcja,nazwa,triggers,op_nazwa,pwm_ss,pwm_fr,pwm_dc;
    public List<AdvScheduledActionTrigger> triggersList = new ArrayList<>();



    AdvScheduledAction(){
        //this.adv = adv;
    }

    AdvScheduledAction(int id, String typ,String rodzaj,String koniunkcja,String nazwa,String triggers,int out_id,String out_stan,String op_nazwa,boolean keepLog){
        this.id = id; this.typ =typ; this.rodzaj=rodzaj;this.koniunkcja = koniunkcja; this.nazwa = nazwa; this.triggers=triggers; this.out_id = out_id;this.out_stan = out_stan;this.op_nazwa=op_nazwa;this.keepLog=keepLog;
        parseTriggers();
    }
    AdvScheduledAction(int id, String typ,String rodzaj,String koniunkcja,String nazwa,String triggers,int pwm_id,String pwm_ss,String pwm_fr,String pwm_dc,String op_nazwa,boolean keepLog){
        this.id = id; this.typ =typ; this.rodzaj=rodzaj;this.koniunkcja = koniunkcja; this.nazwa = nazwa; this.triggers=triggers; this.pwm_id = pwm_id;this.pwm_ss = pwm_ss; this.pwm_fr = pwm_fr;this.pwm_dc = pwm_dc;this.op_nazwa=op_nazwa;this.keepLog=keepLog;
        parseTriggers();
    }
    AdvScheduledAction(int id, String typ,String rodzaj,String koniunkcja,String nazwa,String triggers,int chain_id,String op_nazwa,boolean keepLog){
        this.id = id; this.typ =typ; this.rodzaj=rodzaj;this.koniunkcja = koniunkcja; this.nazwa = nazwa; this.triggers=triggers; this.chain_id = chain_id;this.op_nazwa=op_nazwa;this.keepLog=keepLog;
        out_stan = "EXECUTE";
        parseTriggers();
    }

    private void parseTriggers(){
        triggersList.clear();
        List<String> triggersStrings = new ArrayList<>(Arrays.asList(triggers.split("\\$")));
        for (int i = 0;i<triggersStrings.size()-1;i+=11) {
            if(triggersStrings.get(i+1).equals("None")||triggersStrings.get(i+1).isEmpty())
                triggersList.add(new AdvScheduledActionTrigger(
                        Integer.parseInt(triggersStrings.get(i)),
                        id,
                        Integer.parseInt(triggersStrings.get(i+2)),
                        triggersStrings.get(i+3),
                        triggersStrings.get(i+4),
                        triggersStrings.get(i+5)
                ));
            else {
                String iosp_name = "";
                if(triggersStrings.get(i + 3).equals("i/o"))
                    iosp_name = triggersStrings.get(i + 6);
                else if(triggersStrings.get(i + 3).matches("pwm"))
                    iosp_name = triggersStrings.get(i + 8);
                else if(triggersStrings.get(i + 3).equals("sensor"))
                    iosp_name = triggersStrings.get(i + 9);
                triggersList.add(new AdvScheduledActionTrigger(
                        Integer.parseInt(triggersStrings.get(i)),
                        id,
                        Integer.parseInt(triggersStrings.get(i + 2)),
                        triggersStrings.get(i + 3),
                        triggersStrings.get(i + 4),
                        triggersStrings.get(i + 5),
                        triggersStrings.get(i + 1),
                        iosp_name,
                        triggersStrings.get(i + 10)
                ));
            }
        }
    }

    public void conjDialog(final Context context){
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); ;
        View view = inflater.inflate(R.layout.conj_edit, null);
        final EditText conj = view.findViewById(R.id.conj);
        if(koniunkcja != null)
            if(!koniunkcja.equals("None"))
                conj.setText(koniunkcja);
        MaterialDialog d = new MaterialDialog.Builder(context)
                //.title(R.string.title)
                .customView(view, true)
                .autoDismiss(false)
                .positiveText("SAVE")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        int trNumberMax = triggersList.size();
                        boolean valid = true;
                        String conjS = conj.getText().toString();

                        Matcher syntax = Pattern.compile("(\\(*#|#)[0-9]*(#\\)*|#)( and | or |)").matcher(conjS);
                        int syntaxLenght = 0;
                        while (syntax.find()) {
                            //System.out.println("Found: " + syntax.group(0));
                            syntaxLenght += syntax.group(0).length();
                        }
                        if(syntaxLenght != conjS.length()) {
                            valid = false;
                            Toast.makeText(context, "Bad syntax !", Toast.LENGTH_SHORT).show();
                        }
                        Matcher numbers = Pattern.compile("#[0-9]*#").matcher(conjS);
                        while (numbers.find()) {
                            int trNumber = Integer.parseInt(numbers.group(0).replace("#",""));
                            if (trNumber > trNumberMax ){
                                valid = false;
                                Toast.makeText(context, "Trigger number "+trNumber+" not exist !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        Matcher leftBracket = Pattern.compile("\\(").matcher(conjS);
                        int leftBracketN = 0;
                        while (leftBracket.find())
                            leftBracketN++;
                        Matcher rightBracket = Pattern.compile("\\)").matcher(conjS);
                        int rightBracketN = 0;
                        while (rightBracket.find())
                            rightBracketN++;
                        if(leftBracketN != rightBracketN){
                            valid = false;
                            Toast.makeText(context, "Bracket not closed ?!", Toast.LENGTH_SHORT).show();
                        }

                        if(valid){
                            dialog.dismiss();
                            AdvScheduledActions.AdvScheduledActionsTask edit_Conj = new AdvScheduledActions.AdvScheduledActionsTask(new AdvScheduledActions.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                }
                            });
                            edit_Conj.execute("GPIO_ASA_SetConj",conjS,String.valueOf(id));
                        }


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

    }

    public void actionDialog(final boolean editMode, final Context context,final SparseArray targetOList,final SparseArray targetPWMList, final SparseArray targetChainList){
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); ;
        View view = inflater.inflate(R.layout.action_edit, null);
        TextView title = view.findViewById(R.id.titleL);
        final EditText name = view.findViewById(R.id.name);
        final Spinner type = view.findViewById(R.id.type);
        List<String> typeList = new ArrayList<String>();
        typeList.add("output");
        typeList.add("pwm");
        typeList.add("chain");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, typeList);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        type.setAdapter(typeAdapter);
        final Spinner targetO = view.findViewById(R.id.targeto);
        ArrayAdapter<String> targetOAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, ConvertToList(targetOList));
        targetOAdapter.setDropDownViewResource(R.layout.spinner_item);
        targetO.setAdapter(targetOAdapter);
        final Spinner targetPWM = view.findViewById(R.id.targetpwm);
        ArrayAdapter<String> targetPWMAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, ConvertToList(targetPWMList));
        targetPWMAdapter.setDropDownViewResource(R.layout.spinner_item);
        targetPWM.setAdapter(targetPWMAdapter);
        final Spinner targetChain = view.findViewById(R.id.targetChain);
        ArrayAdapter<String> targetChainAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, ConvertToList(targetChainList));
        targetChainAdapter.setDropDownViewResource(R.layout.spinner_item);
        targetChain.setAdapter(targetChainAdapter);
        final EditText noe = view.findViewById(R.id.noe);
        final Spinner state = (Spinner) view.findViewById(R.id.state);
        final ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(context, R.array.states, android.R.layout.simple_spinner_item);
        final EditText pwmFR = view.findViewById(R.id.pwmFR);
        final EditText pwmDC = view.findViewById(R.id.pwmDC);
        final CheckBox keeplogsCh = view.findViewById(R.id.keepLogs);
        final TableRow targetoRow = view.findViewById(R.id.targetoRow);
        final TableRow targetpwmRow = view.findViewById(R.id.targetpwmRow);
        final TableRow pwmFRRow = view.findViewById(R.id.pwmFRRow);
        final TableRow pwmDCRow = view.findViewById(R.id.pwmDCRow);
        final TableRow targetChainRow = view.findViewById(R.id.targetChainRow);
        final TableRow stateRow = view.findViewById(R.id.stateRow);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    targetoRow.setVisibility(View.VISIBLE);
                    targetpwmRow.setVisibility(View.GONE);
                    pwmFRRow.setVisibility(View.GONE);
                    pwmDCRow.setVisibility(View.GONE);
                    targetChainRow.setVisibility(View.GONE);
                    stateRow.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    targetoRow.setVisibility(View.GONE);
                    targetpwmRow.setVisibility(View.VISIBLE);
                    pwmFRRow.setVisibility(View.VISIBLE);
                    pwmDCRow.setVisibility(View.VISIBLE);
                    targetChainRow.setVisibility(View.GONE);
                    stateRow.setVisibility(View.VISIBLE);
                }else if (position == 2) {
                    targetoRow.setVisibility(View.GONE);
                    targetpwmRow.setVisibility(View.GONE);
                    pwmFRRow.setVisibility(View.GONE);
                    pwmDCRow.setVisibility(View.GONE);
                    targetChainRow.setVisibility(View.VISIBLE);
                    stateRow.setVisibility(View.GONE);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(editMode){
            title.setText("Edit action:");
            type.setSelection(typeAdapter.getPosition(typ));
            name.setText(nazwa);
            if(type.getSelectedItemPosition() == 0) {
                targetO.setSelection(targetOList.indexOfKey(out_id));
                state.setSelection(stateAdapter.getPosition(out_stan));
            }
            else if (type.getSelectedItemPosition() == 1){
                targetPWM.setSelection(targetPWMList.indexOfKey(pwm_id));
                if(pwm_fr != null)pwmFR.setText(pwm_fr);
                if(pwm_dc != null)pwmDC.setText(pwm_dc);
                state.setSelection(stateAdapter.getPosition(pwm_ss));
            }else if(type.getSelectedItemPosition() == 2) {
                targetChain.setSelection(targetChainList.indexOfKey(chain_id));
            }
            noe.setText(rodzaj);
            keeplogsCh.setChecked(keepLog);
        }
        final AdvScheduledActions.AdvScheduledActionsTask exec = new AdvScheduledActions.AdvScheduledActionsTask(new AdvScheduledActions.AsyncResponse() {
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
                        if((type.getSelectedItemPosition() == 0 &&(targetO.getSelectedItem() == null||name.getText().toString().isEmpty()||noe.getText().toString().isEmpty()))||
                                (type.getSelectedItemPosition() == 1 &&(targetPWM.getSelectedItem() == null||name.getText().toString().isEmpty()||noe.getText().toString().isEmpty()))||
                                (type.getSelectedItemPosition() == 2 &&(targetChain.getSelectedItem() == null||name.getText().toString().isEmpty()||noe.getText().toString().isEmpty()))
                                )
                            Toast.makeText(context, "Fill Name, target and number of executions !", Toast.LENGTH_SHORT).show();
                        else{
                            dialog.dismiss();
                            int target = -1;
                            switch (type.getSelectedItemPosition()){
                                case 0: target = targetOList.keyAt(targetO.getSelectedItemPosition());break;
                                case 1: target = targetPWMList.keyAt(targetPWM.getSelectedItemPosition());break;
                                case 2: target = targetChainList.keyAt(targetChain.getSelectedItemPosition());break;
                            }
                            if(!editMode)
                                exec.execute("GPIO_ASA_Add",name.getText().toString(),type.getSelectedItem().toString(),String.valueOf(target)
                                        ,noe.getText().toString(),String.valueOf(state.getSelectedItemPosition()),pwmFR.getText().toString(),pwmDC.getText().toString(),(keeplogsCh.isChecked()?"1":"0"));
                            else
                                exec.execute("GPIO_ASA_Update",name.getText().toString(),type.getSelectedItem().toString(),String.valueOf(target)
                                        ,noe.getText().toString(),String.valueOf(state.getSelectedItemPosition()),pwmFR.getText().toString(),pwmDC.getText().toString(),String.valueOf(id),(keeplogsCh.isChecked()?"1":"0"));
                        }
                    }
                })
                .neutralText("DELETE")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        exec.execute("GPIO_ASA_Delete",String.valueOf(id));
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

    public static <C> List<C> ConvertToList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());

        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }


}
