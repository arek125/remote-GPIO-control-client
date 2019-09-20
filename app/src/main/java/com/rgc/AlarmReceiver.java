package com.rgc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver{
    DataBaseHelper myDbHelper;

    @Override
    public void onReceive(final Context context,final Intent intent) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final Intent notificationIntent = new Intent(context, MainActivity.class);
        Bundle extras = intent.getExtras();
        final int notifId = extras.getInt("NOTIF_ID");
        notificationIntent.putExtras(intent);
        myDbHelper = new DataBaseHelper(context);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cursor powK = myDbHelper.dajPowiadomienie(notifId);
                    if(powK.moveToFirst()){
                        String response = "";
                        boolean succes=false;
                        boolean passwd=false,tcpOnly=false;
                        List<String> list = new ArrayList<String>();
                        int id_U=powK.getInt(1),dstPort=powK.getInt(13);
                        String dstAddress=powK.getString(12),dstPassword=powK.getString(14),encKey=powK.getString(15);
                        if(!powK.isNull(17))tcpOnly = powK.getInt(17)==1;
                        notificationIntent.putExtra("ID_U",id_U);
                        try {
                            Connection c = new Connection(dstAddress, dstPort, dstPassword,encKey,tcpOnly);
                            response = c.sendStringTCP("NOTIF_check;"+powK.getString(2)+";"+powK.getString(3)+";"+powK.getString(4)+";"+powK.getString(5)+";"+powK.getString(9), true);
                            list = new ArrayList<String>(Arrays.asList(response.split(";")));
                            if (list.get(0).equals("true")) passwd = true;
                            else if (list.get(0).equals("false")) passwd = false;
                            succes = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            response = "ERROR: " + e;
                            succes = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                            response = "ERROR: " + e;
                            succes = false;
                        }
                        if (succes && passwd) {
                            String notifText = "";
                            for (int i = 3; i < list.size()-1; i+=4) {
                                String formattedDate = AdvSAListAdapter.UTCtoLocalDate(list.get(i+1),"yyyy-MM-dd HH:mm:ss",false);
                                if(list.get(i+2).matches("ds18b20|dht|tsl2561"))
                                    notifText+=formattedDate+" "+list.get(i+3)+list.get(2)+"\n";
                                else
                                    notifText+=formattedDate+" "+list.get(i+2)+" "+list.get(i+3)+"\n";
                            }
                            if(!notifText.isEmpty()){
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                                stackBuilder.addParentStack(MainActivity.class);
                                stackBuilder.addNextIntent(notificationIntent);
                                PendingIntent pendingIntent = stackBuilder.getPendingIntent(notifId, PendingIntent.FLAG_UPDATE_CURRENT);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"M_CH_ID");
                                if(!powK.isNull(8)) {
                                    if (!powK.getString(8).isEmpty() && !powK.getString(8).equals("Not selected")) {
                                        Uri selectedSound = Uri.parse("content://media"+powK.getString(8));
                                        builder.setSound(selectedSound);
                                    }else builder.setDefaults(Notification.DEFAULT_SOUND);
                                }else builder.setDefaults(Notification.DEFAULT_SOUND);
                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel channel = new NotificationChannel("M_CH_ID"+notifId,"RGC Channel", NotificationManager.IMPORTANCE_DEFAULT);
                                    if(!powK.isNull(8)) {
                                        if (!powK.getString(8).isEmpty() && !powK.getString(8).equals("Not selected")) {
                                            Uri selectedSound = Uri.parse("content://media" + powK.getString(8));
                                            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                                    .setUsage(AudioAttributes.USAGE_ALARM)
                                                    .build();
                                            channel.setSound(selectedSound, audioAttributes);
                                        }
                                    }
                                    notificationManager.createNotificationChannel(channel);
                                }
                                Notification notification = builder.setContentTitle(list.get(3))
                                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notifText))
                                        .setContentText(notifText)
                                        .setTicker("from "+ list.get(3))
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setLights(Color.RED, 1000, 1000)
                                        .setAutoCancel(true)
                                        .setChannelId("M_CH_ID"+notifId)
                                        .setContentIntent(pendingIntent).build();


                                notificationManager.notify(notifId, notification);

                            }
                            myDbHelper.edutyjPowiadomienie(notifId);

                        } else if (!succes || !passwd) {
                            if (!succes) {
                                myDbHelper.dodajLog(id_U, response);
                            } else if (!passwd) {
                                myDbHelper.dodajLog(id_U, list.get(1));
                            }
                        }
                    }
                    powK.close();
                    myDbHelper.close();
                }
            }).start();
    }

}