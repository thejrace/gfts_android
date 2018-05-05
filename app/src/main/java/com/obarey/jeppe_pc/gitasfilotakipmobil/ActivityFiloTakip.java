package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.GridLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ActivityFiloTakip extends AppCompatActivity {

    private ProgressDialog loader;
    private GridLayout otobus_box_container;
    private Map<String, Otobus> otobus_kutular = new HashMap<>();
    private  Map<Integer, String> profil_sort_data = new HashMap<>();

    private int otobus_box_counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filo_takip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        final Activity this_ref = this;
        if( otobus_box_counter == UserConfig.otobusler.length() ){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Otobus otobus;
                    for( int k = 0; k < UserConfig.otobusler.length(); k++ ){
                        try {
                            otobus = otobus_kutular.get(profil_sort_data.get(k));
                            otobus_box_container.addView( otobus.get_box() );
                        } catch( NullPointerException e ){ } // profilde pasif olan otobusler için for loop exception'i yakaliyorum
                    }
                    filo_download_init();
                }
            });
        }
    }


    private void filo_download_init(){
        final Activity this_ref = this;
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                while( true ){
                    WebRequest req = new WebRequest();
                    try {
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
                    } catch( JSONException e ){
                        e.printStackTrace();
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

    /*private void filo_download_init_old(){
        final Activity this_ref = this;
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                while( true ){
                    WebRequest req = new WebRequest();
                    try {
                        JSONObject data = req.req(WebRequest.MOBIL_SERVIS_URL, "req=mobil_orer_download").getJSONObject("data");
                        for (Map.Entry<String, Otobus> entry : otobus_kutular.entrySet()) {
                            try {
                                entry.getValue().set_data( data.getJSONArray(entry.getKey()), this_ref );
                            } catch( JSONException e ){
                                e.printStackTrace();
                            }
                        }
                    } catch( JSONException e ){
                        e.printStackTrace();
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
    }/*/

    /*@Override
    protected void onResume(){
        super.onResume();

        System.out.println("Geri geldim cookielerimi ver lan ipne");
    }*/

}
