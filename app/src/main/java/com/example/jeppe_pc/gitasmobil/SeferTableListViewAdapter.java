package com.example.jeppe_pc.gitasmobil;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    // todo https://stackoverflow.com/questions/19289812/findviewbyid-vs-view-holder-pattern-in-listview-adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View satirView;
        satirView = inflater.inflate(R.layout.sefer_row, null);

        TextView no_text = (TextView)satirView.findViewById(R.id.lw_no);
        TextView orer_text = (TextView)satirView.findViewById(R.id.lw_orer);
        TextView gidis_text = (TextView)satirView.findViewById(R.id.lw_gidis);
        TextView bitis_text = (TextView)satirView.findViewById(R.id.lw_bitis);
        TextView bekleme_suresi_text = (TextView)satirView.findViewById(R.id.lw_bekleme_suresi);
        TextView surucu_text = (TextView)satirView.findViewById(R.id.lw_surucu);
        TextView tamamlanma_suresi_text = (TextView)satirView.findViewById(R.id.lw_tamamlanma_suresi);
        TextView durum_kodu_text = (TextView)satirView.findViewById(R.id.lw_durum_kodu);
        SeferData sefer = data.get(position);
        no_text.setText(sefer.get_no());
        orer_text.setText(sefer.get_orer());
        gidis_text.setText(sefer.get_gidis());
        bitis_text.setText(sefer.get_bitis());
        bekleme_suresi_text.setText("10");
        surucu_text.setText(sefer.get_surucu());
        tamamlanma_suresi_text.setText("104");
        durum_kodu_text.setText(sefer.get_durum_kodu());

        return satirView;
    }

}
