package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.Calendar;

public class FragmentTakipOrer extends Fragment {

    private ProgressDialog loader;
    private ListView seferler_lw;
    private EditText dp;
    private String oto;
    private SeferTableListViewAdapter lv_adapter;
    private ArrayList<SeferData> aktif_gun_data = new ArrayList();
    private ArrayList<SeferData> downloaded_data = new ArrayList();
    private boolean data_downloaded = false;
    public FragmentTakipOrer() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_fragment_takip_orer, container, false);
        Button tarih_data_download = (Button)layout.findViewById(R.id.btn_tarih_data_download);
        final Button aktif_gune_don = (Button)layout.findViewById(R.id.btn_aktif_gune_don);
        dp = (EditText)layout.findViewById(R.id.tarih_dp);
        TextView header = (TextView)layout.findViewById(R.id.otobus_takip_fragment_header);
        seferler_lw = (ListView)layout.findViewById(R.id.seferler_listview);
        final ActivityOtobusTakip activity_ref = (ActivityOtobusTakip)getActivity();
        oto = activity_ref.get_otobus_box_data().get_oto();
        header.setText( oto );
        if( !data_downloaded ){
            data_download("AT", new NoArgumentCallBack() {
                @Override
                public void action() {
                    aktif_gun_data = downloaded_data;
                    lv_adapter = new SeferTableListViewAdapter( activity_ref, aktif_gun_data );
                    seferler_lw.setAdapter(lv_adapter);
                    lv_adapter.notifyDataSetChanged();
                    loader.dismiss();
                    dp.setText("");
                }
            });
            data_downloaded = true;
        } else {
            seferler_lw.setAdapter( lv_adapter );
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

                                    String month_str = String.valueOf( month );
                                    if(month < 10) month_str = "0" + month_str;
                                    String day_str = String.valueOf( dayOfMonth );
                                    if(dayOfMonth < 10) day_str  = "0" + day_str ;

                                    dp.setText(  day_str + "-" + month_str + "-" +  year );
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

        aktif_gune_don.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View v ){
                lv_adapter.set_data( aktif_gun_data );
                lv_adapter.notifyDataSetChanged();
            }
        });

        tarih_data_download.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){

                String dp_val = dp.getText().toString();

                final ActivityOtobusTakip activity_ref = (ActivityOtobusTakip)getActivity();
                if( dp_val.trim().equals("") ){
                    activity_ref.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loader.dismiss();
                            Toast.makeText(activity_ref, "Geçersiz tarih girildi.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                data_download(Common.rev_date(dp_val.trim()), new NoArgumentCallBack() {
                    @Override
                    public void action() {
                        lv_adapter.set_data( downloaded_data );
                        lv_adapter.notifyDataSetChanged();
                        loader.dismiss();
                        dp.setText("");
                    }
                });
            }
        });

        return layout;
    }

    private void data_download( final String _tarih, final NoArgumentCallBack _cb ){
        loader = ProgressDialog.show(getActivity(), "Lütfen bekleyin...", "Veri alınıyor..", true);
        final ActivityOtobusTakip activity_ref = (ActivityOtobusTakip)getActivity();
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                WebRequest req = new WebRequest();
                try {
                    JSONObject data = req.req(WebRequest.MOBIL_SERVIS_URL, "req=orer_download&oto="+oto+"&baslangic="+_tarih+"&bitis=&excel=true").getJSONObject("data");
                    JSONArray orer_data = data.getJSONArray("orer_data");
                    downloaded_data = new ArrayList<>();
                    add_thead_data();
                    JSONObject sefer;
                    if( orer_data.length() == 0 ){
                        activity_ref.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loader.dismiss();
                                Toast.makeText(activity_ref, "Bu tarihin ORER verisi yok.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    for( int k = 0; k < orer_data.length(); k++ ){
                        sefer = orer_data.getJSONObject(k);
                        downloaded_data.add( new SeferData(
                                sefer.getString("no"),
                                sefer.getString("hat"),
                                "",
                                "",
                                oto,
                                sefer.getString("surucu"),
                                "",
                                "",
                                sefer.getString("gelis"),
                                sefer.getString("orer"),
                                String.valueOf(SeferSure.hesapla(sefer.getString("gidis"), sefer.getString("bitis"))),
                                sefer.getString("amir"),
                                sefer.getString("gidis"),
                                sefer.getString("tahmin"),
                                sefer.getString("bitis"),
                                sefer.getString("durum"),
                                sefer.getString("durum_kodu"),
                                sefer.getString("plaka"),
                                1,
                                1
                        ));
                    }
                    activity_ref.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _cb.action();
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
    private void add_thead_data(){
        downloaded_data.add(new SeferData(
                "#",
                "Hat",
                "",
                "",
                "",
                "Sürücü",
                "",
                "",
                "",
                "Or",
                "Ss",
                "",
                "Gdş",
                "",
                "Btş",
                "TH",
                "Dk",
                "",
                1,
                1
        ));
    }

}
