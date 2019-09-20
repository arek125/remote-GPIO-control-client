package com.rgc;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class CustomCmds extends Fragment {
    String dstName;
    int Page, arTime,id_U;
    ProgressBar pb;
    Button r;
    boolean menuCreated = false, VisibleToUser = true,handlerOn = false, running = false;
    GridView gridView;
    Handler handler = new Handler();
    Runnable runabble = null;
    Context mContext;
    Connection c;
    ArrayList<CustomCmd> cmdsList = new ArrayList<>();

    public CustomCmds newInstance(int page, String name, Connection conn, Context Context, int artime, int id_u) {
        // RFCodes fragment = new RFCodes();
        Page = page;
        dstName = name;
        c = conn;
        arTime = artime;
        id_U = id_u;
        mContext = Context;
        return this;
    }
    public void getCustomCmds(){
        new GetAsyncData(new GetAsyncData.AsyncResponse() {
            @Override
            public void processFinish(List<String> list) {
                cmdsList.clear();
                for (int i = 2; i < list.size()-1; i+=4) {
                    cmdsList.add(new CustomCmd(
                            list.get(i),
                            list.get(i+1),
                            list.get(i+2),
                            list.get(i+3)
                    ));
                }
                gridView.setAdapter(new CustomCmdsAdapter(mContext,cmdsList));
            }
            @Override
            public void processFail(String error) {

            }
        },mContext,c,id_U,16384,pb,r).execute("GetCustomCmds");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rfcodes, container, false);
        setHasOptionsMenu(true);
        gridView = rootView.findViewById(R.id.gridView1);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View v,final int position, long id) {
                final CustomCmd cmd = cmdsList.get(position);
                new GetAsyncData(new GetAsyncData.AsyncResponse() {
                    @Override
                    public void processFinish(List<String> list) {
                        if(cmd.wait)Toast.makeText(mContext, list.get(2), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void processFail(String error) {

                    }
                },mContext,c,id_U,1024,pb,r).execute("ExecCustomCmd",String.valueOf(cmd.id),String.valueOf(cmd.wait));
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, final View v,final int position, long id) {
                final CustomCmd cmd = cmdsList.get(position);
                cmd.modalDialog(true,mContext,new GetAsyncData(new GetAsyncData.AsyncResponse() {
                    @Override
                    public void processFinish(List<String> list) {
                        getCustomCmds();
                    }
                    @Override
                    public void processFail(String error) {

                    }
                },mContext,c,id_U,512,pb,r));
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem actionViewItem = menu.findItem(R.id.miActionButton);
        View v = MenuItemCompat.getActionView(actionViewItem);
        pb = (ProgressBar) v.findViewById(R.id.pbProgressAction);
        r = (Button) v.findViewById(R.id.btnCustomAction3);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.cancel();
                getCustomCmds();
            }
        });
        r.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Chains.showLogs(mContext);
                return false;
            }
        });
        Button b = (Button) v.findViewById(R.id.btnCustomAction);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCmd cmd = new CustomCmd();
                cmd.modalDialog(false,mContext,new GetAsyncData(new GetAsyncData.AsyncResponse() {
                    @Override
                    public void processFinish(List<String> list) {
                        getCustomCmds();
                    }
                    @Override
                    public void processFail(String error) {

                    }
                },mContext,c,id_U,512,pb,r));
            }
        });
        pb.setVisibility(View.INVISIBLE);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        menuCreated = true;
        running = false;
        c.cancel();
        //check_state();
        getCustomCmds();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(runabble);
        handlerOn = false;
        super.onStop();
    }

    @Override
    public void onResume() {
        if (menuCreated && VisibleToUser) {
//            hendlerm();
            handlerOn = true;
        }
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        VisibleToUser = isVisibleToUser;
        if (!isVisibleToUser)  {
            handler.removeCallbacks(runabble);
            handlerOn = false;
        }
    }


//    public void hendlerm() {
//        if (!handlerOn && menuCreated && arTime > 0) {
//            runabble = new Runnable() {
//                public void run() {
//                    if (menuCreated) {
//                        getCustomCmds();
//                        handler.postDelayed(this, arTime);
//                    }
//                }
//            };
//
//            handler.postDelayed(runabble, arTime);
//            handlerOn = true;
//        }
//    }
}
