package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

// https://fabcirablog.weebly.com/blog/creating-a-never-ending-background-service-in-android

public class AlarmService extends Service {
    private int counter = 0;
    private ActivityFiloTakip ctx;
    public AlarmService(Context applicationContext) {
        super();
        ctx = (ActivityFiloTakip)applicationContext;
        Log.i("GAlarmServis", "INIT");
    }

    public AlarmService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        // start sticky dondugumuz zaman, Android OS pil vs. yetersizken bizim servisi kapatırsa,
        // pil doldugunda tekrar baslatir
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        // kullanıcı programı kapadığında, bu service class ından AlarmServiceRestarter' e, Intent e
        // parametre olarak girdigimiz mesaji gonderiyoruz ( Manifestte de tanımlı olacak )
        // bunu goren BroadcastReciever servisimizi tekrar baslatacak
        // Ayarlardan Durmaya Zorla denilene kadar bu servis arkada çalışacakj
        Intent broadcastIntent = new Intent("com.obarey.jeppe_pc.gitasfilotakipmobil.ActivityRecognition.RestartAlarmService");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                if( counter == 3 ){

                    System.out.println( UserConfig.eposta );
                    //showNotification("Obarey", " Hederoy");
                }

                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    public void showNotification(String title, String content) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.drawable.app_ico) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                //.setSound(alarmSound) // set alarm sound for notification
                .setAutoCancel(true); // clear notification after click

        mNotificationManager.notify(0, mBuilder.build());



    }
    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
