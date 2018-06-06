package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jeppe-PC on 10/25/2017.
 */

public class UserConfig {

    public static String eposta;
    public static JSONArray otobusler = new JSONArray();
    public static ArrayList<String> otobus_kodlar_index = new ArrayList<>();

    public static void set_user_data(Context ctx, JSONObject data ){
        try {
            UserConfig.otobusler = data.getJSONArray("otobusler");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            JSONObject config = new JSONObject(data.getJSONObject("ayarlar").getString("data"));
            //System.out.println(config);
            // alarm aksiyonlari
            String alarmlar = config.getString("alarmlar");
            Set<String> alarm_sp_set = new HashSet<>();
            for( int k = 0; k < alarmlar.length(); k++ ){
                if( alarmlar.charAt(k) == '1' ){
                    alarm_sp_set.add(String.valueOf(k));
                }
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putStringSet("alarmlar", alarm_sp_set );
            editor.putBoolean("alarm_servis_durum", config.getBoolean("alarm_servis_durum"));
            editor.putString("alarm_kontrol_frekans", String.valueOf(config.getInt("alarm_kontrol_frekans")));
            editor.putString("surucu_calisma_saati", String.valueOf(config.getInt("surucu_calisma_saati")));
            editor.putString("eposta", UserConfig.eposta);
            editor.apply();
        } catch( JSONException e ){
            e.printStackTrace();
        }


    }




}
