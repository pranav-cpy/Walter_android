package com.team.riti.walter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pranav on 19/04/2017 AD.
 */

public class MyService extends Service{
    private static String TAG=MyService.class.getSimpleName();
    final String URL="http://192.168.43.29:3011/";
    private MyThread mythread;
    public boolean isrunning=false;
    JSONObject json=new JSONObject();
    JSONObject j=new JSONObject();
    String bookname1[];
    int count1;
    String barcode;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mythread=new MyThread();
    }
    @Override
    public synchronized void onDestroy(){
        super.onDestroy();
        if (!isrunning) {
            mythread.interrupt();
            mythread.stop();
        }
    }
    @Override
    public synchronized void onStart(Intent intent,int startId){
        super.onStart(intent,startId);
        if(!isrunning){
            mythread.start();
            isrunning=true;
        }
    }
    public void checkreminder() {
        SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
       barcode = settings.getString("barcode", "");
        if (isconnected()) {
            try {
                while(json.length()>0)
                    json.remove(json.keys().next());
                json.put("label", "bookarrived");
                json.put("barcode", barcode);



                j = HttpClient.SendHttpPost(URL, json);
                if (j.getString("label").equals("bookarrived")) {
                     count1= j.getInt("count");



                    bookname1=new String[count1];
                    for(int i=0;i<count1;i++) {
                        bookname1[i] = j.getString("bookname"+i);}
                    for(int i=0;i<count1;i++) {
                        Thread.sleep(4000);
                        addnotification(bookname1[i]);

                    }

                }
            } catch (JSONException e) {
            }catch (NullPointerException e){} catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void checkreturndate()
    {//try {
        SharedPreferences settings = getSharedPreferences(LoginActivity.prefs, 0);
        int count = settings.getInt("noofbookstoreturn", 0);
        int hour=settings.getInt("hour",7);
        int minute=settings.getInt("minute",30);
       /* for(int i=0;i<count;i++) {
            String d = settings.getString("returndate"+i, "");
           String date=new  SimpleDateFormat("dd-MM-yyyy").format(new Date());
            Calendar calendar=Calendar.getInstance();
            int hr=calendar.get(Calendar.HOUR_OF_DAY);
            int min=calendar.get(Calendar.MINUTE);

            if((date.equals(d)) && (hour==hr) && (minute==min)){
                addnotification_for_returndate(settings.getString("booktoreturn"+i,""));
            }*/
        for(int i=0;i<count;i++)
        {
          String d=settings.getString("returndate"+i,"");
          String str= d+"-"+hour+"-"+minute;
            DateFormat df=new SimpleDateFormat("dd-MM-yyyy-HH-mm");
            Date dateobj=new Date();
            String str1=df.format(dateobj);
            if(str.equals(str1))
                addnotification_for_returndate(settings.getString("booktoreturn"+i,""));


        }
       /* SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        for (int i = 0; i < count; i++) {
            String str = settings.getString("returndate" + i, "");
            Date d = sdf.parse(str);
            Date date = new Date();
            String s = sdf.format(date);
            date = sdf.parse(s);
            Calendar calendar=Calendar.getInstance();
            int hr=calendar.get(Calendar.HOUR_OF_DAY);
            int min=calendar.get(Calendar.MINUTE);

            if(d.equals(date) && hour==hr && minute==min)
                addnotification_for_returndate(settings.getString("booktoreturn"+i,""));

        }*/
    //}catch (ParseException e){}
    }
    class MyThread extends Thread{
        static final long delay=60000;

        @Override
        public void run() {
            while(isrunning){
                try {
                    checkreminder();
                    checkreturndate();
                    Thread.sleep(delay);
                }catch (InterruptedException e){
                    isrunning=false;
                }
            }
        }
    }
    void addnotification(String str)
    {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.walter1)
                        .setContentTitle("Now available")
                        .setContentText("Book "+str+" " + "\nfrom your reminder list is now available ")
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND);
      //  Intent notificationIntent = new Intent(this, MainActivity.class);
       // PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
      //          PendingIntent.FLAG_UPDATE_CURRENT);
      //  builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


       manager.notify(100,builder.build());
    }
    void addnotification_for_returndate(String str)
    { int z=0;
        z++;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.walter1)
                        .setContentTitle("it's the time to return")
                        .setContentText("return book "+str+" today to avoid fines").setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND);

         Intent intent=new Intent(this,returnd.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(z, builder.build());

    }
    public boolean isconnected()
    {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        }catch (Exception e){return false;}
    }
}
