package com.rgc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

public class RFCode {
    int id,code,pulseLength,protocol,repeatTransmit,bitLength;
    String name,type;

    RFCode(String id,String name,String type,String code,String pulseLength, String protocol, String repeatTransmit ,String bitLength){
        this.id = ChainBond.parseInt(id);
        this.name = name;
        this.type = type;
        this.code = ChainBond.parseInt(code);
        this.pulseLength = ChainBond.parseInt(pulseLength);
        this.protocol = ChainBond.parseInt(protocol);
        this.repeatTransmit = ChainBond.parseInt(repeatTransmit);
        this.bitLength = ChainBond.parseInt(bitLength);
    }
    RFCode(){}

    public void modalDialog(final boolean editMode, final Context context, int rx, int tx, final GetAsyncData execad){
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); ;
        View view = inflater.inflate(R.layout.edit_rf, null);
        final EditText name = view.findViewById(R.id.name);
        final Spinner type = view.findViewById(R.id.type);
        final EditText code = view.findViewById(R.id.code);
        final EditText pulseL = view.findViewById(R.id.pulse);
        final EditText protocol = view.findViewById(R.id.protocol);
        final EditText repT = view.findViewById(R.id.repeatT);
        final EditText bitL = view.findViewById(R.id.bitLength);
        List<String> typeList = new ArrayList<>();
        if(tx != 0)typeList.add("Transmit");
        if(rx != 0)typeList.add("Recive");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, typeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);
        final TableRow repTRow = view.findViewById(R.id.repeatTRow);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) repTRow.setVisibility(View.VISIBLE);
                else repTRow.setVisibility(View.GONE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(editMode){
            name.setText(this.name);
            if(this.type.equals("Recive"))type.setSelection(1);
            code.setText(String.valueOf(this.code));
            pulseL.setText(String.valueOf(this.pulseLength));
            protocol.setText(String.valueOf(this.protocol));
            repT.setText(String.valueOf(this.repeatTransmit));
            bitL.setText(String.valueOf(this.bitLength));
        }


        MaterialDialog d = new MaterialDialog.Builder(context)
                .customView(view, true)
                .autoDismiss(false)
                .positiveText("SAVE")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(name.getText().toString().isEmpty() || code.getText().toString().isEmpty())
                            Toast.makeText(context, "Name and code are required !", Toast.LENGTH_SHORT).show();
                        else {
                            dialog.dismiss();
                            if (editMode)
                                execad.execute("UpdateRfCode", String.valueOf(id), name.getText().toString(), type.getSelectedItem().toString(), code.getText().toString(), pulseL.getText().toString(), protocol.getText().toString(), repT.getText().toString(), bitL.getText().toString());
                            else
                                execad.execute("AddRfCode", name.getText().toString(), type.getSelectedItem().toString(), code.getText().toString(), pulseL.getText().toString(), protocol.getText().toString(), repT.getText().toString(), bitL.getText().toString());
                        }
                    }
                })
                .neutralText("DELETE")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        execad.execute("DeleteRfCode",String.valueOf(id));
                    }
                })
                .negativeText("CANCEL")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        execad.cancel(true);
                    }
                })
                .show();

        if(!editMode)
            d.getActionButton(DialogAction.NEUTRAL).setVisibility(View.INVISIBLE);

    }

}
