package com.example.jeppe_pc.gitasmobil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.GridLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActivityFiloTakip extends AppCompatActivity {

    private ProgressDialog loader;
    private GridLayout otobus_box_container;
    private Map<String, Otobus> otobus_kutular = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();

    private int otobus_box_counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filo_takip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        otobus_box_container = (GridLayout)findViewById(R.id.otobus_box_container);
        JSONObject temp_otobus_data;
        for( final String kod : UserConfig.otobus_kodlar_index ){
            try {
                temp_otobus_data = UserConfig.otobusler.getJSONObject(kod);
                final Otobus otobus = new Otobus( kod, temp_otobus_data.getString("ruhsat_plaka"), temp_otobus_data.getString("aktif_plaka"));
                otobus_kutular.put( kod, otobus );
                otobus.box_layout_olustur( this, new Otobus.UI_Listener() {
                    @Override
                    public void ui_finished() {
                        box_counter();
                        box_ui_callback();
                    }
                });
            } catch( JSONException e ){
                e.printStackTrace();
            }
            break;
        }
    }

    private synchronized void box_counter(){
        otobus_box_counter++;
    }

    private void box_ui_callback(){
        final Activity this_ref = this;
        if( otobus_box_counter == 1 ){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // kutulari alfabetik eklemek için hepsini oluşturduktan sonra ekliyoruz viewe
                    Otobus otobus;
                    for( final String kod : UserConfig.otobus_kodlar_index ){
                        otobus = otobus_kutular.get(kod);
                        otobus_box_container.addView( otobus.get_box() );
                        break;
                    }
                    loader = ProgressDialog.show(this_ref, "Lütfen bekleyin...", "Filoya giriş yapılıyor..", true);
                }
            });
            FiloLogin filo_login = new FiloLogin( UserConfig.filo5_data, new FiloLoginListener() {
                @Override
                public void on_finish(Map<String, String> _cookies) {
                    cookies = _cookies;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loader.dismiss();
                            filo_download_init();
                        }
                    });
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
                    if(  !cookies.get("A").equals("INIT") && !cookies.get("B").equals("INIT") && !cookies.get("C").equals("INIT") ){
                        int counter = 0;
                        for( String kod : UserConfig.otobus_kodlar_index ){
                            otobus_kutular.get(kod).filo_veri_download( this_ref, cookies.get( kod.substring(0,1)),counter);
                            counter++;
                            break;
                        }
                        break;
                    }
                    try {
                        Thread.sleep(5000);
                    } catch( InterruptedException e ){
                        e.printStackTrace();
                    }
                }
            }
        });
        th.setDaemon(true);
        th.start();
    }

    /*@Override
    protected void onResume(){
        super.onResume();

        System.out.println("Geri geldim cookielerimi ver lan ipne");
    }*/

}
