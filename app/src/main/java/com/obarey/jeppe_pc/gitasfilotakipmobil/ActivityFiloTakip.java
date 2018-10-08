package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityFiloTakip extends AppCompatActivity {

    private ProgressDialog loader;
    private GridLayout otobus_box_container;
    private Switch zayi_switch;
    private Map<String, Otobus> otobus_kutular = new HashMap<>();
    private  Map<Integer, String> profil_sort_data = new HashMap<>();

    private int otobus_box_counter = 0;
    private Map<String, Boolean> active_filters = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filo_takip);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/


        zayi_switch = (Switch)findViewById(R.id.sw_zayi);
        otobus_box_container = (GridLayout)findViewById(R.id.otobus_box_container);
        JSONObject temp_otobus_data;
        try {
            for( int k = 0; k < UserConfig.otobusler.length(); k++ ){
                temp_otobus_data = UserConfig.otobusler.getJSONObject(k);
                final Otobus otobus = new Otobus( temp_otobus_data.getString("kapi_kodu"), temp_otobus_data.getString("ruhsat_plaka"), temp_otobus_data.getString("aktif_plaka"), temp_otobus_data.getString("sira"));
                otobus_kutular.put( temp_otobus_data.getString("kapi_kodu"), otobus );
                profil_sort_data.put( Integer.valueOf(temp_otobus_data.getString("sira")), temp_otobus_data.getString("kapi_kodu"));
                otobus.box_layout_olustur( this, new Otobus.UI_Listener() {
                    @Override
                    public void ui_finished() {
                        box_counter();
                        box_ui_callback();
                    }
                });
            }
        } catch( JSONException e ){
            e.printStackTrace();
        }
    }

    private synchronized void box_counter(){
        otobus_box_counter++;
    }

    private void box_ui_callback(){
        if( otobus_box_counter == UserConfig.otobusler.length() ){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Otobus otobus;
                    for( int k = 0; k < UserConfig.otobusler.length() +1; k++ ){
                        try {
                            otobus = otobus_kutular.get(profil_sort_data.get(k));
                            otobus_box_container.addView( otobus.get_box() );
                        } catch( NullPointerException e ){ } // profilde pasif olan otobusler için for loop exception'i yakaliyorum
                    }
                    filo_download_init();
                    init_filters();
                }
            });
        }
    }

    private void init_filters(){
        zayi_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    active_filters.put(OtobusBoxFilter.ZAYI, true);
                } else {
                    active_filters.put(OtobusBoxFilter.ZAYI, false);
                }
                filter_action();
            }
        });
    }

    private void filter_action(){
        boolean lojik;
        otobus_box_container.removeAllViews();
        Otobus otobus;
        for( int k = 0; k < UserConfig.otobusler.length() +1; k++ ){
            try {
                otobus = otobus_kutular.get(profil_sort_data.get(k));
                lojik = true;
                for (Map.Entry<String, Boolean> filter_data : active_filters.entrySet()) {
                    if( filter_data.getValue() ) lojik = lojik && otobus.get_filter_data(filter_data.getKey());
                }
                if( lojik ){
                    try {
                        otobus_box_container.addView( otobus.get_box());
                    } catch( IllegalStateException e ){
                        //e.printStackTrace();
                    }
                } else {
                    otobus_box_container.removeView( otobus.get_box());
                }
            } catch( NullPointerException e ){ } // profilde pasif olan otobusler için for loop exception'i yakaliyorum
        }


        for (Map.Entry<String, Otobus> entry : otobus_kutular.entrySet()) {



        }

    }

    private void filo_download_init(){
        final Activity this_ref = this;
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                while( true ){
                    try {
                        WebRequest req = new WebRequest();
                        JSONArray data = req.req(WebRequest.MOBIL_SERVIS_URL, "req=mobil_oadd_download").getJSONArray("data");
                        JSONObject temp;
                        for( int k = 0; k < data.length(); k++ ){
                            temp = data.getJSONObject(k);
                            try {
                                otobus_kutular.get( temp.getString("oto") ).set_data( temp, this_ref );
                            } catch( JSONException | NullPointerException e ){
                                // profilde kapali olan otobusleri nullpointer aticak
                                //e.printStackTrace();
                            }
                        }
                    } catch( JSONException | NullPointerException e ){
                        e.printStackTrace();
                        Toast.makeText(this_ref, "İnternet bağlantınızı kontrol edin!", Toast.LENGTH_LONG).show();
                    }
                    try {
                        Thread.sleep(60000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }
            }
        });
        th.setDaemon(true);
        th.start();
    }
}
