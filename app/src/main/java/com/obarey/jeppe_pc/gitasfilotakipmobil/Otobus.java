package com.obarey.jeppe_pc.gitasfilotakipmobil;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeppe-PC on 10/25/2017.
 */

public class Otobus {
    private String sira, ui_led, oto;
    private OtobusBoxData box_data;
    private ArrayList<SeferData> sefer_data = new ArrayList<>();
    private ArrayList<String> suruculer = new ArrayList<>();
    private LinearLayout ui_container;
    private ImageView led;
    private TextView notf_buyuk, notf_kucuk;
    private String ui_main_notf, ui_notf, ui_tarih;
    private JSONArray orer_data = new JSONArray();
    private JSONObject data = new JSONObject();
    private Map<String, Integer> sefer_ozet = new HashMap<>();
    private OtobusPopupDialog dialog;
    private OtobusPopupData otobus_popup_data;
    public Otobus( String _oto, String _ruhsat_plaka, String _aktif_plaka, String _sira ){
        oto = _oto;
        //box_data = new OtobusBoxData( _oto, _aktif_plaka, _ruhsat_plaka );
        otobus_popup_data = new OtobusPopupData( _oto, _aktif_plaka, "", "","", "", "00000", "", "" );
        sira = _sira;
    }

    public void set_data( JSONObject _data, final Activity context ){
        try {
            data = _data;
            update( context );
        } catch( JSONException e ){
            e.printStackTrace();
        }
    }

    private void update( final Activity context ) throws JSONException {
        ui_led = data.getString("durum");
        //ui_main_notf = data.getString("main_notf");
        //ui_notf = data.getString("notf");
        //ui_tarih = data.getString("tarih");

        otobus_popup_data.set_durum( data.getString("durum") );
        otobus_popup_data.set_main_notf( data.getString("main_notf") );
        otobus_popup_data.set_notf( data.getString("notf") );
        otobus_popup_data.set_tarih( data.getString("tarih") );
        otobus_popup_data.set_sefer_ozet( data.getString("sefer_ozet") );
        otobus_popup_data.set_hat( data.getString("hat") );

        update_ui( context );
    }

    @Deprecated
    private void update_old( final Activity context ) throws JSONException {
        if( orer_data.length() == 0 ) return;
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String SIMDI = dateFormat.format(date);

        // sefer özeti hesaplamalari init
        sefer_ozet.put(SeferData.DTAMAM, 0);
        sefer_ozet.put(SeferData.DAKTIF, 0);
        sefer_ozet.put(SeferData.DBEKLEYEN, 0);
        sefer_ozet.put(SeferData.DIPTAL, 0);
        sefer_ozet.put(SeferData.DYARIM, 0);

        // durum degiskenleri
        boolean tum_seferler_tamam = false,
                hat_verisi_alindi = false,
                tum_seferler_bekleyen = false;


        JSONObject sefer, onceki_sefer, sonraki_sefer;
        String sefer_no,
                sefer_hat,
                sefer_guzergah,
                sefer_surucu_sicil_no,
                sefer_orer,
                sefer_tahmin,
                sefer_durum,
                sefer_amir,
                sefer_durum_kodu;

        box_data.reset_sefer_data();
        for (int j = 0; j < orer_data.length(); j++) {
            sefer = orer_data.getJSONObject(j);
            //sefer_no = sefer.getString("no");
            sefer_hat = sefer.getString("hat");
            //sefer_guzergah = sefer.getString("guzergah");
            //sefer_surucu_sicil_no = sefer.getString("surucu");
            sefer_orer = sefer.getString("orer");
            sefer_tahmin = sefer.getString("tahmin");
            sefer_durum = sefer.getString("durum");
            sefer_durum_kodu = sefer.getString("durum_kodu");
            sefer_amir = sefer.getString("amir");


            box_data.add_sefer_data( new SeferData(
                String.valueOf(j+1),
                sefer_hat,
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

            sonraki_sefer = null;
            // bir sonraki sefer varsa aliyoruz verisini
            if (!orer_data.isNull(j + 1)) sonraki_sefer = orer_data.getJSONObject(j + 1);

            // yarim sefer
            if (sefer_durum.equals(SeferData.DYARIM)) {
                if (sonraki_sefer != null) {
                    int k = j + 1;
                    int duzeltilmis_sefer_index = 0;
                    boolean sonraki_seferler_duzeltilmis = false;
                    while (!orer_data.isNull(k)) {
                        JSONObject yarim_sonrasi_sefer = orer_data.getJSONObject(k);
                        // yarim kalan seferden sonra bekleyen veya tamamlanan sefer yoksa yarim kaldi durumuna getiriyoruz
                        // varsa dokunmuyoruz zaten yukarıda sonraki seferleri geçerken bekleyen veya tamamlandi olarak degisecek durum
                        if (yarim_sonrasi_sefer.getString("durum").equals("B") || yarim_sonrasi_sefer.getString("durum").equals("T")) {
                            duzeltilmis_sefer_index = k;
                            sonraki_seferler_duzeltilmis = true;
                            break;
                        }
                        k++;
                    }
                    // seferler duzeltilmemişse yarim kaldi diyoruz
                    // bu muhtemelen son sefer yarim kaldiginda olur cunku genelde yarim kalan sefer sonrasi iptal oluyor sonrakiler
                    if (!sonraki_seferler_duzeltilmis) {
                        //System.out.println("[ " + kod + " ALARM ]" + "SEFER YARIM KALDIIIII");
                        ui_led = SeferData.DYARIM;
                    } else {
                        // bekleyen sefer ( durum led icin )
                        JSONObject iptal_sonrasi_sefer = orer_data.getJSONObject(duzeltilmis_sefer_index);
                        if (iptal_sonrasi_sefer.getString("durum").equals(SeferData.DBEKLEYEN)) {
                            ui_led = SeferData.DBEKLEYEN;
                        }
                    }
                } else {
                    // son sefer yarım kalmis
                    ui_led = SeferData.DYARIM;
                }
            }

            // sefer iptalse
            if (sefer_durum.equals(SeferData.DIPTAL)) {
                if (sonraki_sefer != null) {
                    int k = j + 1;
                    boolean sonraki_seferler_duzeltilmis = false;
                    int duzeltilmis_sefer_index = 0;
                    while (!orer_data.isNull(k)) {
                        JSONObject yarim_sonrasi_sefer = orer_data.getJSONObject(k);
                        // iptal seferden sonra bekleyen veya tamamlanan sefer yoksa yarim kaldi durumuna getiriyoruz
                        // varsa dokunmuyoruz zaten yukarıda sonraki seferleri geçerken bekleyen veya tamamlandi olarak degisecek durum
                        if (yarim_sonrasi_sefer.getString("durum").equals("B") || yarim_sonrasi_sefer.getString("durum").equals("T")) {
                            duzeltilmis_sefer_index = k;
                            sonraki_seferler_duzeltilmis = true;
                            break;
                        }
                        k++;
                    }
                    // seferler duzeltilmemişse durum iptal diyoruz
                    if (!sonraki_seferler_duzeltilmis) {
                        ui_led = SeferData.DIPTAL;
                    } else {
                        JSONObject iptal_sonrasi_sefer = orer_data.getJSONObject(duzeltilmis_sefer_index);
                        if (iptal_sonrasi_sefer.getString("durum").equals(SeferData.DBEKLEYEN)) {
                            ui_led = SeferData.DBEKLEYEN;
                        }
                    }
                } else {
                    ui_led = SeferData.DIPTAL;
                }
            }

            // aktif sefer
            if (sefer_durum.equals(SeferData.DAKTIF)) {
                ui_led = SeferData.DAKTIF;
            }

            // tamamlanmis sefer
            if (sefer_durum.equals(SeferData.DTAMAM)) {
                if (sonraki_sefer != null) {
                    if (sonraki_sefer.getString("durum").equals(SeferData.DBEKLEYEN)) {
                        // sonraki seferi var ve durumu bekleyense, bekleyen seferin saatini aliyoruz
                        ui_led = SeferData.DBEKLEYEN;
                    }
                } else {
                    ui_led = SeferData.DTAMAM;
                }
            }
        } // for

       /* if (sefer_ozet.get(Sefer_Data.DIPTAL) > 0 || sefer_ozet.get(Sefer_Data.DYARIM) > 0) {
            filtre_data.put(Otobus_Box_Filtre.FD_ZAYI, true);
        } else {
            filtre_data.put(Otobus_Box_Filtre.FD_ZAYI, false);
        }*/

        update_ui( context );
        //otobus_plaka_kontrol();
    }

    private void update_ui( Activity context ){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if( ui_led.equals(SeferData.DAKTIF) ){
                    led.setImageResource(R.drawable.led_aktif);
                } else if( ui_led.equals(SeferData.DBEKLEYEN) ){
                    led.setImageResource(R.drawable.led_bekleyen);
                } else if( ui_led.equals(SeferData.DIPTAL) ){
                    led.setImageResource(R.drawable.led_iptal);
                } else if( ui_led.equals(SeferData.DTAMAM) ){
                    led.setImageResource(R.drawable.led_tamam);
                } else if( ui_led.equals(SeferData.DYARIM) ){
                    led.setImageResource(R.drawable.led_yarim);
                }
            }
        });
    }

    public void box_layout_olustur(final Activity context, final Otobus.UI_Listener listener ){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ui_container = new LinearLayout( context );
                    //LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    Map<String, Integer> screen_wh = Common.get_screen_res( context );
                    LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams((screen_wh.get("width") / 3)-40, LinearLayout.LayoutParams.WRAP_CONTENT );

                    layout_params.setMargins(5, 5, 5, 5);
                    ui_container.setPadding(10, 10, 10, 10);
                    ui_container.setLayoutParams( layout_params );
                    ui_container.setGravity(Gravity.CENTER);
                    ui_container.setBackgroundResource(R.drawable.otobus_box_background);
                    ui_container.setOrientation(LinearLayout.HORIZONTAL);

                    led = new ImageView(context);
                    led.setImageResource(R.drawable.led_default);

                    LinearLayout alt_cont = new LinearLayout(context);
                    layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layout_params.setMargins(10, 0, 0, 0);
                    alt_cont.setLayoutParams(layout_params);
                    alt_cont.setOrientation(LinearLayout.VERTICAL);

                    notf_buyuk = new TextView( context );
                    notf_buyuk.setTextAppearance(context, R.style.notf_buyuk_style);
                    layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); // w, h
                    notf_buyuk.setLayoutParams(layout_params);
                    notf_buyuk.setText(otobus_popup_data.get_oto());

                    notf_kucuk = new TextView( context );
                    notf_kucuk.setTextAppearance(context, R.style.notf_kucuk_style);
                    layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); // w, h
                    notf_buyuk.setLayoutParams(layout_params);
                    notf_kucuk.setText(otobus_popup_data.get_plaka());

                    alt_cont.addView( notf_buyuk, 0 );
                    alt_cont.addView( notf_kucuk, 1 );

                    ui_container.addView( led, 0 );
                    ui_container.addView( alt_cont, 1 );

                    ui_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                // zaten olusturduysak sadece verileri guncelle
                                dialog.set_data( otobus_popup_data );
                                System.out.println("data guncelle zaten init edilmis");
                            } catch( NullPointerException e ){
                                dialog = new OtobusPopupDialog( context, otobus_popup_data );
                                System.out.println("ilk init");
                            }
                            dialog.show();
                        }
                    });

                    listener.ui_finished();
                } catch (Exception e ){
                    e.printStackTrace();
                }
            }
        });
        th.setDaemon(true);
        th.start();
    }

    public LinearLayout get_box(){
        return ui_container;
    }

    public interface UI_Listener {
        void ui_finished();
    }

}