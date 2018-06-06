package com.obarey.jeppe_pc.gitasfilotakipmobil;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import java.util.ArrayList;

public class ActivityOtobusTakip extends AppCompatActivity {

    private String oto;
    private OtobusPopupData otobus_box_data;

    private android.support.v4.app.Fragment takip_orer_fragment, takip_iys_fragment, takip_mesaj_fragment, takip_suruculer_fragment, takip_hiz_fragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.detay_nav_orer:
                    if( takip_orer_fragment == null ) takip_orer_fragment = new FragmentTakipOrer();
                    init_tab_fragment( takip_orer_fragment );
                    return true;
                case R.id.detay_nav_mesaj:
                    if( takip_mesaj_fragment == null ) takip_mesaj_fragment = new FragmentTakipMesaj();
                    init_tab_fragment( takip_mesaj_fragment );
                    return true;
                case R.id.detay_nav_iys:
                    if( takip_iys_fragment == null ) takip_iys_fragment = new FragmentTakipIYS();
                    init_tab_fragment( takip_iys_fragment );
                    return true;
                /*case R.id.detay_nav_surucu:
                    if( takip_suruculer_fragment == null ) takip_suruculer_fragment = new FragmentTakipSuruculer();
                    init_tab_fragment( takip_suruculer_fragment );
                    return true;
                case R.id.detay_nav_hiz:
                    if( takip_hiz_fragment == null ) takip_hiz_fragment = new FragmentTakipHiz();
                    init_tab_fragment( takip_hiz_fragment );
                    return true;*/
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otobus_takip);

        Intent i = getIntent();
        otobus_box_data = (OtobusPopupData)i.getSerializableExtra("otobus_data");

        // ilk acilista orer fragment
        takip_orer_fragment = new FragmentTakipOrer();
        init_tab_fragment( takip_orer_fragment );


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public OtobusPopupData get_otobus_box_data(){
        return otobus_box_data;
    }

    private void init_tab_fragment( android.support.v4.app.Fragment fragment ){
        FragmentManager fmanager = getSupportFragmentManager();
        fmanager.beginTransaction().replace(R.id.otobus_takip_content, fragment, fragment.getTag()).commit();
    }

}


