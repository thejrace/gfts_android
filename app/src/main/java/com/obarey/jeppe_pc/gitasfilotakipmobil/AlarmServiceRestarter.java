package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmServiceRestarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(AlarmServiceRestarter.class.getSimpleName(), "Alarm Servisi Durdur!");
        // mesaji aldik, servisi baslatiyoruz
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if( sp.getBoolean("alarm_servis_durum", true) ) context.startService(new Intent(context, AlarmService.class));

    }

}
