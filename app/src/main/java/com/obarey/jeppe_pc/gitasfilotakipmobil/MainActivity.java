package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog loader;

    private Intent service_intent;
    private AlarmService alarm_service;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String eposta = sp.getString("eposta", "obarey");

        ctx = this;
        if( eposta.equals("obarey") ){
            Intent intent = new Intent(this, ActivityGiris.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }

        loader = ProgressDialog.show(this, "Lütfen bekleyin...", "Sunucuyla iletişim kuruluyor..", true);
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WebRequest req = new WebRequest();
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    UserConfig.eposta = sp.getString("eposta", "obarey");
                    JSONObject data = req.req( WebRequest.MOBIL_SERVIS_URL, "req=app_data" ).getJSONObject("data");
                    UserConfig.set_user_data( getApplicationContext(), data );

                    alarm_service = new AlarmService(ctx);
                    service_intent = new Intent(ctx, alarm_service.getClass());
                    if( sp.getBoolean("alarm_servis_durum", true ) ){
                        // Alarm kontrol serivisini baslat
                        if (!alarm_servis_kontrol(alarm_service.getClass())) {
                            startService(service_intent);
                        }
                    } else {
                        if (alarm_servis_kontrol(alarm_service.getClass())) {
                            stopService(service_intent);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loader.dismiss();
                        }
                    });
                } catch( JSONException | NullPointerException e ){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loader.dismiss();
                            Toast.makeText(ctx, "İnternet bağlantınızı kontrol edin!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    });

                }
            }
        });
        th.setDaemon(true);
        th.start();
    }

    private boolean alarm_servis_kontrol(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("AlarmServisOn?", true+"");
                return true;
            }
        }
        Log.i ("AlarmServisOn?", false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
        try {
            stopService(service_intent);
        } catch( NullPointerException e ){
            Toast.makeText(ctx, "İnternet bağlantınızı kontrol edin!", Toast.LENGTH_LONG).show();
        }
        Log.i("ALARMSERVIS", "Patlat");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_filo_takip) {
            Intent intent = new Intent(this, ActivityFiloTakip.class);
            startActivity(intent);
        } else if (id == R.id.nav_cihazlar) {

        } else if (id == R.id.nav_ayarlar) {
            Intent intent = new Intent(this, ActivityAyarlar.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
