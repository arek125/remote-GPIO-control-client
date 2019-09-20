package com.rgc;


import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

public class PagerTabStripActivity extends AppCompatActivity {
private Context mContext;
private int positionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_tab_strip);
        LayoutInflater factory = LayoutInflater.from(this);
		final View titleview = factory.inflate(R.layout.actionbar_title, null);
		TextView tvtitle = (TextView) titleview.findViewById(R.id.mytitle);
		Bundle b = getIntent().getExtras();
		mContext=this;
        final DataBaseHelper myDbHelper = new DataBaseHelper(mContext);
		String nazwa_urzadzenia=b.getString("nazwa");
		Connection c =getIntent().getParcelableExtra("connection");
		//String dstAddress = b.getString("ip");
		//int port = b.getInt("port");
		int artime = b.getInt("artime");
        final int id_u = b.getInt("id_u");
		//String password = b.getString("password");
		//String enc_key = b.getString("enc_key");
		tvtitle.setText(nazwa_urzadzenia);

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME); 
		getSupportActionBar().setCustomView(titleview);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(),nazwa_urzadzenia,c,mContext,artime,id_u);
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        final PopupMenu popup = new PopupMenu(getApplicationContext(), titleview);
        //popup.getMenuInflater().inflate(R.menu.tabs_menu, popup.getMenu());
        final Cursor k = myDbHelper.dajUrzadzenie(id_u);
        if (k.moveToFirst()){
            //t0 =k.getInt(7);t1 =k.getInt(8);t2 =k.getInt(9);t3 =k.getInt(16);/*t4 =k.getInt(10);*/t5 =k.getInt(15);t6 =k.getInt(11);t7 =k.getInt(12);t8 =k.getInt(13);
            if(k.getInt(7) == 1) {
                popup.getMenu().add(Menu.NONE,positionCounter,positionCounter,"GPIO Outputs");
                positionCounter++;
            }
            if(k.getInt(8) == 1) {
                popup.getMenu().add(Menu.NONE,positionCounter,positionCounter,"GPIO Inputs");
                positionCounter++;
            }
            if(k.getInt(9) == 1) {
                popup.getMenu().add(Menu.NONE,positionCounter,positionCounter,"GPIO PWMs");
                positionCounter++;
            }
            if(k.getInt(16) == 1) {
                popup.getMenu().add(Menu.NONE,positionCounter,positionCounter,"Execution chains");
                positionCounter++;
            }
//            if(k.getInt(15) == 1) {
//                popup.getMenu().add(Menu.NONE,positionCounter,positionCounter,"Advanced scheduled actions");
//                positionCounter++;
//            }
            if(k.getInt(11) == 1) {
                popup.getMenu().add(Menu.NONE,positionCounter,positionCounter,"History log");
                positionCounter++;
            }
            if(k.getInt(12) == 1) {
                popup.getMenu().add(Menu.NONE,positionCounter,positionCounter,"Sensors");
                positionCounter++;
            }
            if(k.getInt(13) == 1) {
                popup.getMenu().add(Menu.NONE,positionCounter,positionCounter,"Notifications");
                positionCounter++;
            }
            if(k.getInt(18) == 1) {
                popup.getMenu().add(Menu.NONE,positionCounter,positionCounter,"RF TX/RX");
                positionCounter++;
            }
            if(k.getInt(19) == 1) {
                popup.getMenu().add(Menu.NONE,positionCounter,positionCounter,"Custom commands");
                positionCounter++;
            }
        }
        popup.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        pager.setCurrentItem(item.getItemId(),true);
                        return true;
                    }
                });
        titleview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
        pager.setAdapter(adapter);
        int selectedTab = b.getInt("selectedTab",0);
        pager.setCurrentItem(selectedTab,true);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                myDbHelper.edytujUrzadzenie(id_u,position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    getMenuInflater().inflate(R.menu.activity_main, menu);
	    return true;
	}
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
        // This overrides default action
    }
    @Override
    public void onPause() {
        super.onPause();
    }


    

}