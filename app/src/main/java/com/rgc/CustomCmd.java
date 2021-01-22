package com.rgc;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class CustomCmd {
    int id;
    String name,cmd,lastOutput;
    boolean wait,running;

    CustomCmd(){}
    CustomCmd(String id,String name,String cmd,String wait){
        this.id = ChainBond.parseInt(id);
        this.name = name;
        this.cmd = cmd;
        this.wait = ChainBond.parseInt(wait) == 1;

    }
    public void modalDialog(final boolean editMode, final Context context, final GetAsyncData execad){
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); ;
        View view = inflater.inflate(R.layout.edit_cmd, null);
        final EditText name = view.findViewById(R.id.name);
        final EditText cmd = view.findViewById(R.id.cmd);
        final CheckBox wait = view.findViewById(R.id.wait);

        if(editMode){
            name.setText(this.name);
            wait.setChecked(this.wait);
            cmd.setText(String.valueOf(this.cmd));
        }


        MaterialDialog d = new MaterialDialog.Builder(context)
                .customView(view, true)
                .autoDismiss(false)
                .positiveText("SAVE")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(name.getText().toString().isEmpty() || cmd.getText().toString().isEmpty())
                            Toast.makeText(context, "Name and cmd are required !", Toast.LENGTH_SHORT).show();
                        else {
                            dialog.dismiss();
                            if (editMode)
                                execad.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"UpdateCustomCmd", String.valueOf(id), name.getText().toString(), cmd.getText().toString(), String.valueOf(wait.isChecked()?1:0));
                            else
                                execad.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"AddCustomCmd", name.getText().toString(), cmd.getText().toString(), String.valueOf(wait.isChecked()?1:0));
                        }
                    }
                })
                .neutralText("DELETE")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        execad.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"DeleteCustomCmd",String.valueOf(id));
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
