package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// https://fabcirablog.weebly.com/blog/creating-a-never-ending-background-service-in-android

public class AlarmService extends Service {
    private MainActivity ctx;
    private Timer timer;
    private TimerTask timer_task;

    public AlarmService(Context applicationContext) {
        super();
        ctx = (MainActivity)applicationContext;
        Log.i("GAlarmServis", "INIT");
    }
    // mecburi
    public AlarmService(){

    }

    @Override
    public void onCreate(){
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "GITAS_NOTF_CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("GITAS_NOTF_CHANNEL");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default");
        Notification notf = mBuilder.setOngoing(true).setSmallIcon(R.drawable.app_ico)
                .setContentTitle("Gitaş Filo Takip")
                .setContentText("Alarm Servisi Aktif")
                .setAutoCancel(true)
                .build();
        startForeground(3301, notf);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        servis_action();
        // start sticky dondugumuz zaman, Android OS pil vs. yetersizken bizim servisi kapatırsa,
        // pil doldugunda tekrar baslatir
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("GAlarmServis", "ondestroy!");
        // kullanıcı programı kapadığında, bu service class ından AlarmServiceRestarter' e,
        // Intent' e parametre olarak girdigimiz mesaji gonderiyoruz ( Manifestte de tanımlı olacak )
        // bunu goren BroadcastReciever servisimizi tekrar baslatacak
        // Ayarlardan Durmaya Zorla denilene kadar bu servis arkada çalışacak
        Intent broadcast_intent = new Intent("com.obarey.jeppe_pc.gitasfilotakipmobil.ActivityRecognition.RestartAlarmService");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            sendBroadcast(broadcast_intent);
            stop_alarm_servis();
            return;
        }
        PackageManager pm = getPackageManager();
        List<ResolveInfo> broadcastReceivers  = pm.queryBroadcastReceivers(broadcast_intent, 0);
        for(ResolveInfo info : broadcastReceivers) {
            broadcast_intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            sendBroadcast(broadcast_intent);
        }
        stop_alarm_servis();
    }

    private void servis_action(){
       timer = new Timer();
       init_timer_task();
       SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       timer.schedule( timer_task, 5, Integer.valueOf(sp.getString("alarm_kontrol_frekans", "60"))*1000 );
    }

    public void init_timer_task() {
        timer_task = new TimerTask() {
            public void run() {
                int alarm_id = 0;
                try {
                    WebRequest req = new WebRequest();
                    JSONObject data = req.req(WebRequest.MOBIL_SERVIS_URL, "req=mobil_alarm_download").getJSONObject("data");
                    JSONObject temp_otobus_data;
                    for( int k = 0; k < UserConfig.otobusler.length(); k++ ){
                        temp_otobus_data = UserConfig.otobusler.getJSONObject(k);
                        if( data.has(temp_otobus_data.getString("kapi_kodu")) ){
                            JSONArray alarm_array = data.getJSONArray(temp_otobus_data.getString("kapi_kodu"));
                            for( int j = 0; j < alarm_array.length(); j++ ) {
                                JSONObject alarm_data = alarm_array.getJSONObject(j);
                                show_notification( alarm_data.getString("oto")+ " - " + alarm_data.getString("alarm_mesaj"), Common.rev_datetime(alarm_data.getString("tarih")), alarm_id );
                                alarm_id++;
                            }
                        }
                    }
                } catch( JSONException | NullPointerException e ){
                    Log.i("GAlarmServis", "Alarm Yok!");
                    //e.printStackTrace();
                }
            }
        };
    }
    public void show_notification(String title, String content, int id) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "GITAS_NOTF_CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("GITAS_NOTF_CHANNEL");
            mNotificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.drawable.app_ico) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setContentIntent( pIntent )
                //.setSound(alarmSound) // set alarm sound for notification
                .setAutoCancel(true); // clear notification after click
        mNotificationManager.notify(id, mBuilder.build());
    }

    public void stop_alarm_servis() {
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
