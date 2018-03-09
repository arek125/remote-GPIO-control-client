package com.rgc;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class TabsPagerAdapter extends FragmentPagerAdapter {

	  String dstName;
	  String dstAddress;
	  String dstPass,encKey;
	  int dstPort,arTime,t0 =1,t1 =1,t2 =1,t3 =1,t4 =1,count,id_U;
	  CharSequence ch0,ch1,ch2,ch3,ch4;
	  Fragment fr0,fr1,fr2,fr3,fr4;
	  private Context mContext;
	
    public TabsPagerAdapter(FragmentManager fm,String name, String addr, int port, String pass, Context Context, String enc_key,int artime, int  id_u) {
    	super(fm);
    	dstName = name;
 	    dstAddress = addr;
 	    dstPort = port;
 	    dstPass = pass;
 	   mContext = Context;
 	   encKey = enc_key;
 	   arTime= artime;
		id_U = id_u;
		final DataBaseHelper myDbHelper = new DataBaseHelper(Context);
		myDbHelper.czyscLogi(id_U);
		final Cursor k = myDbHelper.dajUrzadzenie(id_u);
		if (k.moveToFirst()){
			t0 =k.getInt(7);t1 =k.getInt(8);t2 =k.getInt(9);t3 =k.getInt(10);t4 =k.getInt(11);
		}
		count = t0+t1+t2+t3+t4;
		for(int i=0;i<count;i++){
			if (i == 0){
				if (t0 == 1) {ch0 = "GPIO Output" ; fr0=GPIO_Status.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime,id_u);}
				else if (t1 == 1) {ch0 = "GPIO Input"; fr0=GPIO_Input.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime,id_u); t1=0;}
				else if (t2 == 1) {ch0 = "GPIO PWM"; fr0=GPIO_Pwm.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime,id_u); t2=0;}
				else if (t3 == 1) {ch0 = "Scheduled actions"; fr0=ScheduledActions.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime,id_u); t3=0;}
				else if (t4 == 1) {ch0 = "History Log";fr0=History.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime); t4=0;}
			}
			else if (i == 1){
				if (t1 == 1) {ch1 = "GPIO Input";fr1=GPIO_Input.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime,id_u);}
				else if (t2 == 1) {ch1 = "GPIO PWM";fr1=GPIO_Pwm.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime,id_u);t2=0;}
				else if (t3 == 1) {ch1 = "Scheduled actions";fr1=ScheduledActions.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime,id_u);t3=0;}
				else if (t4 == 1) {ch1 = "History Log";fr1=History.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime);t4=0;}
			}
			else if (i == 2){
				if (t2 == 1) {ch2 = "GPIO PWM";fr2=GPIO_Pwm.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime,id_u);}
				else if (t3 == 1) {ch2 = "Scheduled actions";fr2=ScheduledActions.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime,id_u);t3=0;}
				else if (t4 == 1) {ch2 = "History Log";fr2=History.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime);t4=0;}
			}
			else if (i == 3){
				if (t3 == 1) {ch3 = "Scheduled actions";fr3=ScheduledActions.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime,id_u);}
				else if (t4 == 1) {ch3 = "History Log";fr3=History.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime);t4=0;}
			}
			else if (i == 4){
				if (t4 == 1) {ch4 = "History Log";fr4=History.newInstance(i + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime);}
			}
		}
    }
    @Override
    public Fragment getItem(int position) {
    	switch(position){
		case 0: return fr0;
    	case 1: return fr1;
    	case 2: return fr2;
    	case 3: return fr3;
    	case 4: return fr4;
    	}
        return GPIO_Status.newInstance(position + 1,dstName,dstAddress,dstPort,dstPass,mContext,encKey,arTime, id_U);
    }

    @Override
    public int getCount() {return count;}

    @Override
    public CharSequence getPageTitle(int position) {
    	switch(position){
		case 0: return ch0;
		case 1: return ch1;
    	case 2: return ch2;
    	case 3: return ch3;
    	case 4: return ch4;
    	}
        return "TAB " + (position + 1);
    }
}