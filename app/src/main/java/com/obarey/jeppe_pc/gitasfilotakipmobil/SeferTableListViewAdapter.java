package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jeppe-PC on 11/16/2017.
 */

public class SeferTableListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<SeferData> data;

    public SeferTableListViewAdapter(Activity activity, ArrayList<SeferData> _data ){
        data = _data;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public SeferData getItem(int position) {
        //şöyle de olabilir: public Object getItem(int position)
        return data.get(position);
    }

    public void set_data( ArrayList<SeferData> _data ){
        data = _data;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sefer_row, parent, false);
        }
        TextView no_text = (TextView)convertView.findViewById(R.id.lw_no);
        TextView orer_text = (TextView)convertView.findViewById(R.id.lw_orer);
        TextView gidis_text = (TextView)convertView.findViewById(R.id.lw_gidis);
        TextView bitis_text = (TextView)convertView.findViewById(R.id.lw_bitis);
        TextView hat_text = (TextView)convertView.findViewById(R.id.lw_hat);
        TextView surucu_text = (TextView)convertView.findViewById(R.id.lw_surucu);
        TextView tamamlanma_suresi_text = (TextView)convertView.findViewById(R.id.lw_tamamlanma_suresi);
        TextView durum_kodu_text = (TextView)convertView.findViewById(R.id.lw_durum_kodu);
        SeferData sefer = data.get(position);
        no_text.setText(sefer.get_no());
        orer_text.setText(sefer.get_orer());
        hat_text.setText(sefer.get_hat());
        gidis_text.setText(sefer.get_gidis());
        bitis_text.setText(sefer.get_bitis());
        surucu_text.setText(sefer.get_surucu());
        tamamlanma_suresi_text.setText(sefer.get_sure());
        durum_kodu_text.setText(sefer.get_durum_kodu());
        int bg;
        if( sefer.get_durum().equals(SeferData.DAKTIF) ){
            bg = parent.getResources().getColor(R.color.filo_table_aktif);
        } else if( sefer.get_durum().equals(SeferData.DBEKLEYEN)){
            bg = parent.getResources().getColor(R.color.filo_table_bekleyen);
        } else if( sefer.get_durum().equals(SeferData.DIPTAL)){
            bg = parent.getResources().getColor(R.color.filo_table_iptal);
        } else if( sefer.get_durum().equals(SeferData.DYARIM)){
            bg = parent.getResources().getColor(R.color.filo_table_yarim);
        } else if( sefer.get_durum().equals(SeferData.DTAMAM)){
            bg = parent.getResources().getColor(R.color.filo_table_tamam);
        } else {
            bg = parent.getResources().getColor(R.color.led_tamam);
        }
        convertView.setBackgroundColor(bg);
        return convertView;
    }


}

