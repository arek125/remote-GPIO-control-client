package com.rgc;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class TabsPagerAdapter extends FragmentPagerAdapter {

	  String dstName;
	  int /*t0 =1,t1 =1,t2 =1,t3 =1,t4 =1,t5 =1,t6=1,t7=1,t8=1,count,*/arTime,id_U,positionCounter = 0;
	  //CharSequence ch0,ch1,ch2,ch3,ch4,ch5,ch6,ch7,ch8;
	  //Fragment fr0,fr1,fr2,fr3,fr4,fr5,fr6,fr7,fr8;
	  ArrayList<Fragment> fragmentList = new ArrayList<>();
	  ArrayList<CharSequence> charSequenceList = new ArrayList<>();
	  private Context mContext;
	  Connection c;
	
    public TabsPagerAdapter(FragmentManager fm,String name, Connection c, Context Context,int artime, int  id_u) {
    	super(fm);
    	dstName = name;
 	    mContext = Context;
 	    arTime= artime;
		id_U = id_u;
		this.c = c;
		final DataBaseHelper myDbHelper = new DataBaseHelper(Context);
		myDbHelper.czyscLogi(id_U);
		final Cursor k = myDbHelper.dajUrzadzenie(id_u);
		if (k.moveToFirst()){
			//t0 =k.getInt(7);t1 =k.getInt(8);t2 =k.getInt(9);t3 =k.getInt(16);/*t4 =k.getInt(10);*/t5 =k.getInt(15);t6 =k.getInt(11);t7 =k.getInt(12);t8 =k.getInt(13);
			if(k.getInt(7) == 1) {
				fragmentList.add(GPIO_Status.newInstance(positionCounter + 1,dstName,c,mContext,arTime,id_u));
				charSequenceList.add("GPIO Outputs");
				positionCounter++;
			}
			if(k.getInt(8) == 1) {
				fragmentList.add(GPIO_Input.newInstance(positionCounter + 1,dstName,c,mContext,arTime,id_u));
				charSequenceList.add("GPIO Inputs");
				positionCounter++;
			}
			if(k.getInt(9) == 1) {
				fragmentList.add(GPIO_Pwm.newInstance(positionCounter + 1,dstName,c,mContext,arTime,id_u));
				charSequenceList.add("GPIO PWMs");
				positionCounter++;
			}
			if(k.getInt(16) == 1) {
				fragmentList.add(Chains.newInstance(positionCounter + 1,dstName,c,mContext,arTime,id_u));
				charSequenceList.add("Execution chains");
				positionCounter++;
			}
//			if(k.getInt(15) == 1) {
//				fragmentList.add(AdvScheduledActions.newInstance(positionCounter + 1,dstName,c,mContext,arTime,id_u));
//				charSequenceList.add("Advanced scheduled actions");
//				positionCounter++;
//			}
			if(k.getInt(11) == 1) {
				fragmentList.add(History.newInstance(positionCounter + 1,dstName,c,mContext,arTime,id_u));
				charSequenceList.add("History Log");
				positionCounter++;
			}
			if(k.getInt(12) == 1) {
				fragmentList.add(Sensors.newInstance(positionCounter + 1,dstName,c,mContext,arTime,id_u));
				charSequenceList.add("Sensors");
				positionCounter++;
			}
			if(k.getInt(13) == 1) {
				fragmentList.add(Notifications.newInstance(positionCounter + 1,dstName,c,mContext,arTime,id_u));
				charSequenceList.add("Notifications");
				positionCounter++;
			}
			if(k.getInt(18) == 1) {
				fragmentList.add(new RFCodes().newInstance(positionCounter + 1,dstName,c,mContext,arTime,id_u));
				charSequenceList.add("RF TX/RX");
				positionCounter++;
			}
			if(k.getInt(19) == 1) {
				fragmentList.add(new CustomCmds().newInstance(positionCounter + 1,dstName,c,mContext,arTime,id_u));
				charSequenceList.add("Custom commands");
				positionCounter++;
			}

		}
//		count = t0+t1+t2+t3/*+t4*/+t5+t6+t7+t8;
//		for(int i=0;i<count;i++){
//			if (i == 0){
//				if (t0 == 1) {ch0 = "GPIO Outpust" ; fr0=GPIO_Status.newInstance(i + 1,dstName,c,mContext,arTime,id_u);}
//				else if (t1 == 1) {ch0 = "GPIO Inputs"; fr0=GPIO_Input.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t1=0;}
//				else if (t2 == 1) {ch0 = "GPIO PWMs"; fr0=GPIO_Pwm.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t2=0;}
//				else if (t3 == 1) {ch0 = "Execution chains"; fr0=Chains.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t3=0;}
//				//else if (t4 == 1) {ch0 = "Scheduled actions"; fr0=ScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t4=0;}
//				else if (t5 == 1) {ch0 = "Advanced scheduled actions"; fr0=AdvScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t5=0;}
//				else if (t6 == 1) {ch0 = "History Log";fr0=History.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t6=0;}
//				else if (t7 == 1) {ch0 = "Sensors";fr0=Sensors.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t7=0;}
//                else if (t8 == 1) {ch0 = "Notifications";fr0=Notifications.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t8=0;}
//			}
//			else if (i == 1){
//				if (t1 == 1) {ch1 = "GPIO Input";fr1=GPIO_Input.newInstance(i + 1,dstName,c,mContext,arTime,id_u);}
//				else if (t2 == 1) {ch1 = "GPIO PWM";fr1=GPIO_Pwm.newInstance(i + 1,dstName,c,mContext,arTime,id_u);t2=0;}
//				else if (t3 == 1) {ch1 = "Execution chains"; fr1=Chains.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t3=0;}
//				//else if (t4 == 1) {ch1 = "Scheduled actions";fr1=ScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u);t4=0;}
//				else if (t5 == 1) {ch1 = "Advanced scheduled actions"; fr1=AdvScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t5=0;}
//				else if (t6 == 1) {ch1 = "History Log";fr1=History.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t6=0;}
//				else if (t7 == 1) {ch1 = "Sensors";fr1=Sensors.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t7=0;}
//				else if (t8 == 1) {ch1 = "Notifications";fr1=Notifications.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t8=0;}
//			}
//			else if (i == 2){
//				if (t2 == 1) {ch2 = "GPIO PWM";fr2=GPIO_Pwm.newInstance(i + 1,dstName,c,mContext,arTime,id_u);}
//				else if (t3 == 1) {ch2 = "Execution chains"; fr2=Chains.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t3=0;}
//				//else if (t4 == 1) {ch2 = "Scheduled actions";fr2=ScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u);t4=0;}
//				else if (t5 == 1) {ch2 = "Advanced scheduled actions"; fr2=AdvScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t5=0;}
//				else if (t6 == 1) {ch2 = "History Log";fr2=History.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t6=0;}
//				else if (t7 == 1) {ch2 = "Sensors";fr2=Sensors.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t7=0;}
//				else if (t8 == 1) {ch2 = "Notifications";fr2=Notifications.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t8=0;}
//			}
//			else if (i == 3){
//				if (t3 == 1) {ch3 = "Execution chains"; fr3=Chains.newInstance(i + 1,dstName,c,mContext,arTime,id_u);}
//				//else if (t4 == 1) {ch3 = "Scheduled actions";fr3=ScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u);t4=0;}
//				else if (t5 == 1) {ch3 = "Advanced scheduled actions"; fr3=AdvScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t5=0;}
//				else if (t6 == 1) {ch3 = "History Log";fr3=History.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t6=0;}
//				else if (t7 == 1) {ch3 = "Sensors";fr3=Sensors.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t7=0;}
//				else if (t8 == 1) {ch3 = "Notifications";fr3=Notifications.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t8=0;}
//			}
//			else if (i == 4){
//				//if (t4 == 1) {ch4 = "Scheduled actions";fr4=ScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u);}
//				if (t5 == 1) {ch4 = "Advanced scheduled actions"; fr4=AdvScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u);t5=0;}
//				else if (t6 == 1) {ch4 = "History Log";fr4=History.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t6=0;}
//				else if (t7 == 1) {ch4 = "Sensors";fr4=Sensors.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t7=0;}
//				else if (t8 == 1) {ch4 = "Notifications";fr4=Notifications.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t8=0;}
//			}
//			else if (i == 5){
//				if (t5 == 1) {ch5 = "Advanced scheduled actions"; fr5=AdvScheduledActions.newInstance(i + 1,dstName,c,mContext,arTime,id_u);}
//				else if (t6 == 1) {ch5 = "History Log";fr5=History.newInstance(i + 1,dstName,c,mContext,arTime,id_u);t6=0;}
//				else if (t7 == 1) {ch5 = "Sensors";fr5=Sensors.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t7=0;}
//				else if (t8 == 1) {ch5 = "Notifications";fr5=Notifications.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t8=0;}
//			}
//            else if (i == 6){
//				if (t6 == 1) {ch6 = "History Log";fr6=History.newInstance(i + 1,dstName,c,mContext,arTime,id_u);}
//				else if (t7 == 1) {ch6 = "Sensors";fr6=Sensors.newInstance(i + 1,dstName,c,mContext,arTime,id_u);t7=0;}
//				else if (t8 == 1) {ch6 = "Notifications";fr6=Notifications.newInstance(i + 1,dstName,c,mContext,arTime,id_u); t8=0;}
//            }
//			else if (i == 7){
//				if (t7 == 1) {ch7 = "Sensors";fr7=Sensors.newInstance(i + 1,dstName,c,mContext,arTime,id_u);}
//				else if (t8 == 1) {ch7 = "Notifications";fr7=Notifications.newInstance(i + 1,dstName,c,mContext,arTime,id_u);t8=0;}
//			}
//			else if (i == 8){
//				if (t8 == 1) {ch8 = "Notifications";fr8=Notifications.newInstance(i + 1,dstName,c,mContext,arTime,id_u);}
//			}
//		}
		k.close();
        myDbHelper.close();
    }
    @Override
    public Fragment getItem(int position) {
    	return fragmentList.get(position);
//    	switch(position){
//			case 0: return fr0;
//			case 1: return fr1;
//			case 2: return fr2;
//			case 3: return fr3;
//			case 4: return fr4;
//			case 5: return fr5;
//            case 6: return fr6;
//			case 7: return fr7;
//			case 8: return fr8;
//    	}
//        return GPIO_Status.newInstance(position + 1,dstName,c,mContext,arTime, id_U);
    }

    @Override
    public int getCount() {return positionCounter;}

    @Override
    public CharSequence getPageTitle(int position) {
    	return charSequenceList.get(position);
//    	switch(position){
//			case 0: return ch0;
//			case 1: return ch1;
//			case 2: return ch2;
//			case 3: return ch3;
//			case 4: return ch4;
//			case 5: return ch5;
//            case 6: return ch6;
//			case 7: return ch7;
//			case 8: return ch8;
//    	}
//        return "TAB " + (position + 1);
    }


}