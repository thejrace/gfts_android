package com.obarey.jeppe_pc.gitasfilotakipmobil;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

public class FragmentTakipMesaj extends android.support.v4.app.Fragment {

    private boolean data_downloaded = false;
    private JSONObject last_data = new JSONObject();
    private EditText dp;
    private String oto, key_state = "gelen";
    private ProgressDialog loader;
    private ListView lw;
    private  MesajListViewAdapter lw_adapter;

    public FragmentTakipMesaj() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_fragment_takip_mesaj, container, false);

        Button tarih_data_download = (Button)layout.findViewById(R.id.btn_tarih_data_download);
        dp = (EditText)layout.findViewById(R.id.tarih_dp);
        final Button switch_btn = (Button)layout.findViewById(R.id.btn_gelen_giden);
        lw = (ListView)layout.findViewById(R.id.mesajlar_listview);

        ActivityOtobusTakip activity_ref = (ActivityOtobusTakip)getActivity();
        TextView header = (TextView)layout.findViewById(R.id.otobus_takip_fragment_header);
        oto = activity_ref.get_otobus_box_data().get_oto();
        header.setText( oto );
        lw_adapter = new MesajListViewAdapter( getActivity(), last_data );
        if( !data_downloaded ){
            data_download("AKTIF", new NoArgumentCallBack() {
                @Override
                public void action() {
                    lw_adapter.set_data( last_data );
                    lw.setAdapter( lw_adapter );
                }
            });
            data_downloaded = true;
        } else {
            lw_adapter.set_key( key_state );
            lw.setAdapter( lw_adapter );
        }
        dp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    final Calendar takvim = Calendar.getInstance();
                    int yil = takvim.get(Calendar.YEAR);
                    int ay = takvim.get(Calendar.MONTH);
                    int gun = takvim.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    // ay değeri 0 dan başladığı için (Ocak=0, Şubat=1,..,Aralık=11)
                                    // değeri 1 artırarak gösteriyoruz.
                                    month += 1;
                                    // year, month ve dayOfMonth değerleri seçilen tarihin değerleridir.
                                    // Edittextte bu değerleri gösteriyoruz.
                                    dp.setText(  dayOfMonth + "-" + month + "-" +  year );
                                }
                            }, yil, ay, gun);
                    // datepicker açıldığında set edilecek değerleri buraya yazıyoruz.
                    // şimdiki zamanı göstermesi için yukarda tanmladığımz değşkenleri kullanyoruz.
                    // dialog penceresinin button bilgilerini ayarlıyoruz ve ekranda gösteriyoruz.
                    dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", dpd);
                    dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dpd);
                    dpd.show();
                }
            }
        });

        switch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( lw_adapter.get_key().equals("gelen") ){
                    lw_adapter.set_key( "giden");
                    key_state = "giden";
                    switch_btn.setText(R.string.gelen_mesaj);
                } else {
                    lw_adapter.set_key( "gelen");
                    key_state = "gelen";
                    switch_btn.setText(R.string.giden_mesaj);
                }
                lw_adapter.notifyDataSetChanged();
            }
        });

        tarih_data_download.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String tarih_input = dp.getText().toString();
                if( tarih_input.trim().equals("") ) return;
                data_download(Common.rev_date(tarih_input.trim()), new NoArgumentCallBack() {
                    @Override
                    public void action() {
                        lw_adapter.set_key( "gelen");
                        lw_adapter.set_data( last_data  );
                        lw_adapter.notifyDataSetChanged();
                        switch_btn.setText(R.string.giden_mesaj);
                    }
                });
            }
        });
        return layout;
    }

    private void data_download( final String _tarih, final NoArgumentCallBack cb ){
        loader = ProgressDialog.show(getActivity(), "Lütfen bekleyin...", "Veri alınıyor..", true);
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                WebRequest req = new WebRequest();
                try {
                    last_data = req.req(WebRequest.MOBIL_SERVIS_URL, "req=mesaj_download&oto="+oto+"&tarih="+_tarih).getJSONObject("data").getJSONObject("mesaj_data");
                    final ActivityOtobusTakip activity_ref = (ActivityOtobusTakip)getActivity();
                    activity_ref.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cb.action();
                            loader.dismiss();
                        }
                    });

                } catch( JSONException e ){
                    e.printStackTrace();
                }
            }
        });
        th.setDaemon(true);
        th.start();

    }

}

