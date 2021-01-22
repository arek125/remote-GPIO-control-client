package com.rgc;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Chain {
    public int id,status;
    public String nazwa,bonds,nazwaStatusu,execCd;
    public boolean keepLog;
    public List<ChainBond> bondsList = new ArrayList<>();

    Chain(){
    }

    Chain(int id, int status, String nazwaStatusu, String nazwa, String execCd,boolean keepLog, String bonds){
        this.id = id; this.status =status; this.nazwaStatusu=nazwaStatusu;this.bonds = bonds; this.nazwa = nazwa;this.execCd = execCd;this.keepLog = keepLog;
        parseBonds();
    }
    Chain(int id, int status, String nazwaStatusu, String nazwa,String execCd,boolean keepLog){
        this.id = id; this.status =status; this.nazwaStatusu=nazwaStatusu; this.nazwa = nazwa;this.execCd = execCd;this.keepLog = keepLog;
    }

    private void parseBonds(){
        bondsList.clear();
        List<String> bondsStrings = new ArrayList<>(Arrays.asList(bonds.split("\\$")));
        for (int i = 0;i<bondsStrings.size()-1;i+=28) {
               bondsList.add(new ChainBond(
                       bondsStrings.get(i),
                       bondsStrings.get(i+1),
                       bondsStrings.get(i+2),
                       bondsStrings.get(i+3),
                       bondsStrings.get(i+4),
                       bondsStrings.get(i+5),
                       bondsStrings.get(i+6),
                       bondsStrings.get(i+7),
                       bondsStrings.get(i+8),
                       bondsStrings.get(i+9),
                       bondsStrings.get(i+10),
                       bondsStrings.get(i+11),
                       bondsStrings.get(i+12),
                       bondsStrings.get(i+13),
                       bondsStrings.get(i+14),
                       bondsStrings.get(i+15),
                       bondsStrings.get(i+16),
                       bondsStrings.get(i+17),
                       bondsStrings.get(i+18),
                       bondsStrings.get(i+19),
                       bondsStrings.get(i+20),
                       bondsStrings.get(i+21),
                       bondsStrings.get(i+22),
                       bondsStrings.get(i+23),
                       bondsStrings.get(i+24),
                       bondsStrings.get(i+25),
                       bondsStrings.get(i+26),
                       bondsStrings.get(i+27)
                ));
//            if(bondsStrings.get(i+4).equals("action"))
//                bondsList.add(new ChainBond(
//                        Integer.parseInt(bondsStrings.get(i)),
//                        Integer.parseInt(bondsStrings.get(i+2)),
//                        Integer.parseInt(bondsStrings.get(i+1)),
//                        Integer.parseInt(bondsStrings.get(i+3)),
//                        Integer.parseInt(bondsStrings.get(i+5)),
//                        bondsStrings.get(i+4),
//                        bondsStrings.get(i+14)
//                ));
//            else if (bondsStrings.get(i+4).equals("output"))
//                bondsList.add(new ChainBond(
//                        Integer.parseInt(bondsStrings.get(i)),
//                        Integer.parseInt(bondsStrings.get(i+2)),
//                        Integer.parseInt(bondsStrings.get(i+1)),
//                        Integer.parseInt(bondsStrings.get(i+3)),
//                        Integer.parseInt(bondsStrings.get(i+6)),
//                        Integer.parseInt(bondsStrings.get(i+7)),
//                        bondsStrings.get(i+4),
//                        bondsStrings.get(i+12)
//                ));
//            else if (bondsStrings.get(i+4).equals("pwm"))
//                bondsList.add(new ChainBond(
//                        Integer.parseInt(bondsStrings.get(i)),
//                        Integer.parseInt(bondsStrings.get(i+2)),
//                        Integer.parseInt(bondsStrings.get(i+1)),
//                        Integer.parseInt(bondsStrings.get(i+3)),
//                        Integer.parseInt(bondsStrings.get(i+8)),
//                        Integer.parseInt(bondsStrings.get(i+11)),
//                        bondsStrings.get(i+9),
//                        bondsStrings.get(i+10),
//                        bondsStrings.get(i+4),
//                        bondsStrings.get(i+13)
//                ));
        }

    }

    public void chainDialog(final boolean editMode, final Context context){
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); ;
        View view = inflater.inflate(R.layout.chain_dialog, null);
        TextView title = view.findViewById(R.id.titleL);
        final EditText name = view.findViewById(R.id.name);
        final EditText ecd = view.findViewById(R.id.ecd);
        final CheckBox keeplogsCh = view.findViewById(R.id.keepLogs);
        if(editMode) {
            name.setText(nazwa);
            title.setText("Edit chain:");
            ecd.setText(execCd);
            keeplogsCh.setChecked(this.keepLog);
        }
        final Chains.ChainsTask exec = new Chains.ChainsTask(new Chains.AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });

        MaterialDialog d = new MaterialDialog.Builder(context)
                //.title(R.string.title)
                .customView(view, false)
                .autoDismiss(false)
                .positiveText("SAVE")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(!name.getText().toString().isEmpty()&&!ecd.getText().toString().isEmpty()){
                            dialog.dismiss();
                            if(editMode)exec.execute("GPIO_ChainUpdate",String.valueOf(id),name.getText().toString(),ecd.getText().toString(),(keeplogsCh.isChecked()?"1":"0"));
                            else exec.execute("GPIO_ChainAdd",name.getText().toString(),ecd.getText().toString(),(keeplogsCh.isChecked()?"1":"0"));
                        }else
                            Toast.makeText(context, "Fill all fields !", Toast.LENGTH_SHORT).show();
                    }
                })
                .neutralText("DELETE")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        exec.execute("GPIO_ChainDelete",String.valueOf(id));
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

        if(!editMode||status > 0)
            d.getActionButton(DialogAction.NEUTRAL).setVisibility(View.INVISIBLE);




    }

    private LinearLayout reorderBonds(final Context context, final SimpleDateFormat sdfSeconds,final LinearLayout ll){
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        int i = 0;
        for (ChainBond bond : bondsList) {
            final int j = i;
            final RelativeLayout bondView = (RelativeLayout) layoutInflater.inflate(R.layout.chain_bond_elem, null);
            TextView lp = bondView.findViewById(R.id.lp);
            bond.lp = i+1;
            lp.setText(String.valueOf(bond.lp) + ".");
            bondView.setPadding(5, 5, 0, 10);
            TextView targetName = bondView.findViewById(R.id.targetname);
            targetName.setText(bond.targetName);
            TextView targetSet = bondView.findViewById(R.id.set);
            targetSet.setText(bond.targetSet);
            TextView daley = bondView.findViewById(R.id.daley);
            Date Ddate = new Date((long) (bond.deley * 1000));
            daley.setText(sdfSeconds.format(Ddate));
            Button bup = bondView.findViewById(R.id.bup);
            if (i > 0) bup.setVisibility(View.VISIBLE);
            bup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Collections.swap(bondsList,j,j-1);
                    ll.removeAllViews();
                    reorderBonds(context,sdfSeconds,ll);
                }
            });
            Button bdown = bondView.findViewById(R.id.bdown);
            if (i < bondsList.size()-1) bdown.setVisibility(View.VISIBLE);
            bdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Collections.swap(bondsList,j,j+1);
                    ll.removeAllViews();
                    reorderBonds(context,sdfSeconds,ll);
                }
            });
            ll.addView(bondView);
            i++;
        }
        return ll;
    }
    public void setBondOrderDialog(final Context context, final SimpleDateFormat sdfSeconds){
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll =  reorderBonds(context,sdfSeconds,ll);
        MaterialDialog d = new MaterialDialog.Builder(context)
                .title("Change bonds execution order:")
                .customView(ll, true)
                .autoDismiss(false)
                .positiveText("SAVE ORDER")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String lpPlusid="";
                        int i =0;
                        for (ChainBond bond : bondsList) {
                            lpPlusid+=bond.lp+"$"+bond.id;
                            if(i<bondsList.size()-1)lpPlusid+="$";
                            i++;
                        }
                        final Chains.ChainsTask exec = new Chains.ChainsTask(new Chains.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                            }
                        });
                        exec.execute("GPIO_ChainBondsOrder",String.valueOf(id),lpPlusid);
                        dialog.dismiss();
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


}
