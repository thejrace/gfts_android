package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

public class OtobusPopupDialog extends Dialog {

    private Activity context;
    private OtobusPopupData data;
    private TextView oto_lbl, plaka_lbl, hat_lbl, ozet_aktif_lbl, ozet_tamam_lbl, ozet_bekleyen_lbl, ozet_yarim_lbl, ozet_iptal_lbl, main_notf_lbl, notf_lbl, tarih_lbl;
    private ImageView led;

    public OtobusPopupDialog( Activity _context, OtobusPopupData _data ){
        super( _context );
        context = _context;
        data = _data;
    }

    public void set_data( OtobusPopupData _data ){
        data = _data;
        update_ui();
    }
    private void update_ui(){
        oto_lbl.setText(data.get_oto());
        plaka_lbl.setText(data.get_plaka());
        hat_lbl.setText(data.get_hat());

        main_notf_lbl.setText(data.get_main_notf());
        notf_lbl.setText(data.get_notf());
        tarih_lbl.setText(Common.rev_datetime(data.get_tarih()));
        if( data.get_durum().equals(SeferData.DAKTIF) ){
            led.setImageResource(R.drawable.led_aktif);
        } else if( data.get_durum().equals(SeferData.DBEKLEYEN) ){
            led.setImageResource(R.drawable.led_bekleyen);
        } else if( data.get_durum().equals(SeferData.DIPTAL) ){
            led.setImageResource(R.drawable.led_iptal);
        } else if( data.get_durum().equals(SeferData.DTAMAM) ){
            led.setImageResource(R.drawable.led_tamam);
        } else if( data.get_durum().equals(SeferData.DYARIM) ){
            led.setImageResource(R.drawable.led_yarim);
        }

        ozet_aktif_lbl.setText(data.get_aktif_seferler());
        ozet_tamam_lbl.setText(data.get_tamam_seferler());
        ozet_bekleyen_lbl.setText(data.get_bekleyen_seferler());
        ozet_yarim_lbl.setText(data.get_yarim_seferler());
        ozet_iptal_lbl.setText(data.get_iptal_seferler());

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.otobus_popup);
        getWindow().setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));
        LinearLayout container = (LinearLayout)findViewById(R.id.oto_popup_container);

        Map<String, Integer> screen_wh = Common.get_screen_res( context );
        LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(screen_wh.get("width")*70/100, LinearLayout.LayoutParams.WRAP_CONTENT );
        container.setLayoutParams(layout_params);

        led = (ImageView)findViewById(R.id.oto_popup_led);
        oto_lbl = (TextView)findViewById(R.id.oto_popup_kod);
        plaka_lbl = (TextView)findViewById(R.id.oto_popup_plaka);
        hat_lbl = (TextView)findViewById(R.id.oto_popup_hat);
        ozet_aktif_lbl = (TextView)findViewById(R.id.oto_popup_ozet_aktif);
        ozet_tamam_lbl = (TextView)findViewById(R.id.oto_popup_ozet_tamam);
        ozet_bekleyen_lbl = (TextView)findViewById(R.id.oto_popup_ozet_bekleyen);
        ozet_yarim_lbl = (TextView)findViewById(R.id.oto_popup_ozet_yarim);
        ozet_iptal_lbl = (TextView)findViewById(R.id.oto_popup_ozet_iptal);
        main_notf_lbl = (TextView)findViewById(R.id.oto_popup_ui_main_notf);
        notf_lbl = (TextView)findViewById(R.id.oto_popup_ui_notf);
        tarih_lbl = (TextView)findViewById(R.id.oto_popup_tarih);
        Button detay_btn = (Button)findViewById(R.id.oto_popup_btn_detay);
        detay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityOtobusTakip.class);
                intent.putExtra("otobus_data", data );
                context.startActivity(intent);
            }
        });
        update_ui();
    }
}
