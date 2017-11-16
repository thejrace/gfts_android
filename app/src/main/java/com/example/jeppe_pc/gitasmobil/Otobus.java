package com.example.jeppe_pc.gitasmobil;

import android.app.Activity;
import android.content.Intent;
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

    private String cookie, ui_led;
    private OtobusBoxData box_data;
    private ArrayList<SeferData> sefer_data = new ArrayList<>();
    private ArrayList<String> suruculer = new ArrayList<>();

    private LinearLayout ui_container;
    private ImageView led;
    private TextView notf_buyuk, notf_kucuk;
    private Orer_Download orer_download;

    private Map<String, Integer> sefer_ozet = new HashMap<>();

    public Otobus( String _oto, String _ruhsat_plaka, String _aktif_plaka ){
        box_data = new OtobusBoxData( _oto, _aktif_plaka, _ruhsat_plaka );
    }

    public void filo_veri_download( final Activity context, final String cookie, final int counter ){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    Thread.sleep( counter * 2000 );
                } catch( InterruptedException e ){
                    e.printStackTrace();
                }

                orer_download = new Orer_Download(box_data.get_oto(), cookie);
                orer_download.yap();
                if( !orer_download.get_error() ){
                    try {
                        box_data.set_sefer_data(orer_download.get_seferler());
                        update( context );
                    } catch( JSONException e ){
                        e.printStackTrace();
                    }
                } else {
                    filo_veri_download( context, cookie, counter );
                }
            }
        });
        th.setDaemon(true);
        th.start();
    }

    private void update( final Activity context ) throws JSONException {

        if( box_data.get_seferler().size() == 0 ) return;

        JSONArray data = new JSONArray();
        for( SeferData sefer : box_data.get_seferler() ){
            data.put(sefer.tojson());
        }

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

        for (int j = 0; j < data.length(); j++) {
            sefer = data.getJSONObject(j);
            sefer_no = sefer.getString("no");
            sefer_hat = sefer.getString("hat");
            sefer_guzergah = sefer.getString("guzergah");
            //sefer_surucu_sicil_no = sefer.getString("surucu");
            sefer_orer = sefer.getString("orer");
            sefer_tahmin = sefer.getString("tahmin");
            sefer_durum = sefer.getString("durum");
            sefer_durum_kodu = sefer.getString("durum_kodu");
            sefer_amir = sefer.getString("amir");

            sonraki_sefer = null;
            // bir sonraki sefer varsa aliyoruz verisini
            if (!data.isNull(j + 1)) sonraki_sefer = data.getJSONObject(j + 1);

            // yarim sefer
            if (sefer_durum.equals(SeferData.DYARIM)) {
                if (sonraki_sefer != null) {
                    int k = j + 1;
                    int duzeltilmis_sefer_index = 0;
                    boolean sonraki_seferler_duzeltilmis = false;
                    while (!data.isNull(k)) {
                        JSONObject yarim_sonrasi_sefer = data.getJSONObject(k);
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
                        JSONObject iptal_sonrasi_sefer = data.getJSONObject(duzeltilmis_sefer_index);
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
                    while (!data.isNull(k)) {
                        JSONObject yarim_sonrasi_sefer = data.getJSONObject(k);
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
                        JSONObject iptal_sonrasi_sefer = data.getJSONObject(duzeltilmis_sefer_index);
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
                    System.out.println(box_data.get_oto() + " Box oluşturuluyor..");
                    ui_container = new LinearLayout( context );
                    LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                    notf_buyuk.setText(box_data.get_oto());

                    notf_kucuk = new TextView( context );
                    notf_kucuk.setTextAppearance(context, R.style.notf_kucuk_style);
                    layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); // w, h
                    notf_buyuk.setLayoutParams(layout_params);
                    notf_kucuk.setText(box_data.get_aktif_plaka());

                    alt_cont.addView( notf_buyuk, 0 );
                    alt_cont.addView( notf_kucuk, 1 );

                    ui_container.addView( led, 0 );
                    ui_container.addView( alt_cont, 1 );

                    ui_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(context, ActivityOtobusTakip.class);
                            intent.putExtra("otobus_data", box_data );
                            context.startActivity(intent);

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

class Filo_Task_Template {
    protected String oto, cookie, aktif_tarih, logprefix;
    protected boolean error = false;
    protected org.jsoup.Connection.Response istek_yap( String url ){
        try {
            return Jsoup.connect(url + oto)
                    .cookie("PHPSESSID", cookie )
                    .method(org.jsoup.Connection.Method.POST)
                    .timeout(50000)
                    .execute();
        } catch (IOException | NullPointerException e) {
            System.out.println( "["+Common.get_current_hmin() + "]  "+  oto + " " + logprefix + "veri alım hatası. Tekrar deneniyor[1].");
            e.printStackTrace();
            error = true;
        }
        return null;
    }
    protected Document parse_html(org.jsoup.Connection.Response req ){
        try {
            return req.parse();
        } catch( IOException | NullPointerException e ){
            System.out.println(  "["+Common.get_current_hmin() + "]  "+ aktif_tarih  + " " +  oto + " "+ logprefix + " parse hatası. Tekrar deneniyor.");
            error = true;
        }
        return null;
    }
    public boolean get_error(){
        return error;
    }
}

class Orer_Download extends Filo_Task_Template {

    private String aktif_sefer_verisi = "";
    private ArrayList<SeferData> seferler = new ArrayList<>();
    private boolean kaydet = false;
    public Orer_Download( String oto, String cookie ){
        this.oto = oto;
        this.cookie = cookie;
    }

    public void yap(){
        error = false;
        // veri yokken nullpointer yemeyek diye resetliyoruz başta
        System.out.println("ORER download [ " + oto + " ]");
        org.jsoup.Connection.Response sefer_verileri_req = istek_yap("http://filo5.iett.gov.tr/_FYS/000/sorgu.php?konum=ana&konu=sefer&otobus=");
        Document sefer_doc = parse_html( sefer_verileri_req );
        sefer_veri_ayikla( sefer_doc );

    }
    public void sefer_veri_ayikla( Document document ){
        if( error ){
            seferler = new ArrayList<>();
            return;
        }
        Elements table = null;
        Elements rows = null;
        Element row = null;
        Elements cols = null;

        try {
            table = document.select("table");
            rows = table.select("tr");

            if( rows.size() == 1 || rows.size() == 0 ){
                System.out.println(oto + " ORER Filo Veri Yok");
                return;
            }

            String hat = "", orer;
            SeferData tek_sefer_data;
            boolean hat_alindi = false;

            aktif_sefer_verisi = "YOK";
            for( int i = 1; i < rows.size(); i++ ){
                row = rows.get(i);
                cols = row.select("td");

                orer = Common.regex_trim(cols.get(7).getAllElements().get(2).text());

                if( !hat_alindi ){
                    hat = cols.get(1).text().trim();
                    if( cols.get(1).text().trim().contains("!")  ) hat = cols.get(1).text().trim().substring(1, cols.get(1).text().trim().length() - 1 );
                    if( cols.get(1).text().trim().contains("#") ) hat = cols.get(1).text().trim().substring(1, cols.get(1).text().trim().length() - 1 );
                    if( cols.get(1).text().trim().contains("*") ) hat = cols.get(1).text().trim().substring(1, cols.get(1).text().trim().length() - 1);
                    hat_alindi = true;
                }

                if( cols.get(12).text().replaceAll("\u00A0", "").equals("A") && cols.get(3).getAllElements().size() > 2 ){
                    aktif_sefer_verisi = Common.regex_trim(cols.get(3).getAllElements().get(2).attr("title"));
                }

                tek_sefer_data = new SeferData(
                        Common.regex_trim(cols.get(0).text()),
                        hat,
                        Common.regex_trim(cols.get(2).text()),
                        Common.regex_trim(cols.get(3).getAllElements().get(1).text()),
                        Common.regex_trim(cols.get(4).getAllElements().get(2).text()),
                        "",
                        "",
                        "",
                        Common.regex_trim(cols.get(6).text()),
                        orer,
                        "",
                        Common.regex_trim(cols.get(8).text()),
                        Common.regex_trim(cols.get(9).text()),
                        Common.regex_trim(cols.get(10).text()),
                        Common.regex_trim(cols.get(11).text()),
                        Common.regex_trim(cols.get(12).text()),
                        cols.get(13).text().substring(5),
                        "",
                        1,
                        0
                );
                seferler.add(tek_sefer_data);
                cols.clear();
            }
            rows.clear();
        } catch( NullPointerException e ){
            e.printStackTrace();
            System.out.println( "["+Common.get_current_hmin() + "]  "+  oto+ " ORER sefer veri ayıklama hatası. Tekrar deneniyor.");
            seferler = new ArrayList<>();
            error = true;
            //yap();
        }
    }
    public ArrayList<SeferData> get_seferler(){
        return seferler;
    }
    public String get_aktif_sefer_verisi(){
        return aktif_sefer_verisi;
    }

}