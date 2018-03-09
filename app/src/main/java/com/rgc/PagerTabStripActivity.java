package com.rgc;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class PagerTabStripActivity extends AppCompatActivity {
private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_tab_strip);
        LayoutInflater factory = LayoutInflater.from(this);
		final View titleview = factory.inflate(R.layout.actionbar_title, null);
		TextView tvtitle = (TextView) titleview.findViewById(R.id.mytitle);
		Bundle b = getIntent().getExtras();
		mContext=this;
		String nazwa_urzadzenia=b.getString("nazwa");
		String dstAddress = b.getString("ip");
		int port = b.getInt("port");int artime = b.getInt("artime");int id_u = b.getInt("id_u");
		String password = b.getString("password");
		String enc_key = b.getString("enc_key");
		tvtitle.setText(nazwa_urzadzenia);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME); 
		getSupportActionBar().setCustomView(titleview);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(),nazwa_urzadzenia,dstAddress,port,password,mContext,enc_key,artime,id_u);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
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
    	//this.finish();
        
        super.onPause();
    }
    

}