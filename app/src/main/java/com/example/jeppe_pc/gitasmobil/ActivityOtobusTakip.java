package com.example.jeppe_pc.gitasmobil;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class ActivityOtobusTakip extends AppCompatActivity {

    private TextView mTextMessage;
    private OtobusBoxData otobus_box_data;

    private Fragment takip_orer_fragment, takip_iys_fragment, takip_mesaj_fragment, takip_not_fragment;


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
                case R.id.detay_nav_not:
                    if( takip_not_fragment == null ) takip_not_fragment = new FragmentTakipNot();
                    init_tab_fragment( takip_not_fragment );
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otobus_takip);

        Intent i = getIntent();
        otobus_box_data = (OtobusBoxData)i.getSerializableExtra("otobus_data");

        // ilk acilista orer fragment
        takip_orer_fragment = new FragmentTakipOrer();
        init_tab_fragment( takip_orer_fragment );


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void init_tab_fragment( Fragment fragment ){
        FragmentManager fmanager = getSupportFragmentManager();
        fmanager.beginTransaction().replace(R.id.otobus_takip_content, fragment, fragment.getTag()).commit();
    }

}
