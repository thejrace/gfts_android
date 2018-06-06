package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityAyarlar extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fragment i yukle
        getFragmentManager().beginTransaction().replace(android.R.id.content, new FragmentAyarlar()).commit();
    }

    public static class FragmentAyarlar extends PreferenceFragment {
        private SharedPreferences.OnSharedPreferenceChangeListener sp_listener;
        @Override
        public void onCreate( Bundle savedStateInstance ){
            super.onCreate( savedStateInstance );
            addPreferencesFromResource(R.xml.preferences);

            bind_summary_value(findPreference("alarm_kontrol_frekans"));
            bind_summary_value(findPreference("surucu_calisma_saati"));

            // ayarlar degistiginde web servise ayarlari göndermek icin degisiklikleri dinliyoruz
            sp_listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(final SharedPreferences prefs, String key) {
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                WebRequest req = new WebRequest();
                                final int ok = req.req( WebRequest.MOBIL_SERVIS_URL, "req=mobil_config_guncelle&" +
                                                "alarmlar=" + prefs.getStringSet("alarmlar", null) + "&" +
                                                "alarm_servis_durum=" + prefs.getBoolean("alarm_servis_durum", true ) + "&" +
                                                "surucu_calisma_saati=" + prefs.getString("surucu_calisma_saati", "8" ) + "&" +
                                                "alarm_kontrol_frekans=" + prefs.getString("alarm_kontrol_frekans", "60")
                                        ).getInt("ok");



                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String str_msg;
                                        if( ok == 1 ){
                                            str_msg = "Ayarlar güncellendi.";
                                        } else {
                                            str_msg = "Ayarlar sunucuya kaydedilirken bir hata oluştu.";
                                        }
                                        Toast.makeText(getActivity().getApplicationContext(), str_msg, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (JSONException | NullPointerException e ){
                                e.printStackTrace();
                            }
                        }
                    });
                    th.setDaemon(true);
                    th.start();
                }
            };
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            prefs.registerOnSharedPreferenceChangeListener(sp_listener);
        }
    }

    // ayar degistiginde alttaki tooltip i degistirme
    private static void bind_summary_value( Preference preference ){
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String val = newValue.toString();
            if( preference instanceof EditTextPreference ){
                preference.setSummary(val);
            }
            return true;
        }
    };















}
