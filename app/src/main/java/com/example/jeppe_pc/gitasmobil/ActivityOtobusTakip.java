package com.example.jeppe_pc.gitasmobil;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class ActivityOtobusTakip extends AppCompatActivity {

    private TextView mTextMessage;
    private OtobusBoxData otobus_box_data;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.detay_nav_orer:

                    return true;
                case R.id.detay_nav_mesaj:

                    return true;
                case R.id.detay_nav_iys:

                    return true;
                case R.id.detay_nav_not:

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
        otobus_box_data = (OtobusBoxData)i.getSerializableExtra("otobus_dataaaaaa");

        System.out.println(otobus_box_data.get_oto());
        System.out.println(otobus_box_data.get_seferler());


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
