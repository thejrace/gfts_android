package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MesajListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private JSONObject data;
    private String aktif_key = "gelen";


    public MesajListViewAdapter(Activity activity, JSONObject _data ){
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = _data;
    }

    public void set_data( JSONObject _data ){
        data = _data;
    }

    public void set_key( String _key ){
        aktif_key = _key;
    }

    @Override
    public int getCount() {
        try {
            return data.getJSONArray(aktif_key).length();
        } catch( JSONException e ){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return data.getJSONArray(aktif_key).getJSONObject(position);
        } catch( JSONException e ){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String get_key(){
        return aktif_key;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mesaj_row, parent, false);
        }

        TextView msj_kaynak = (TextView)convertView.findViewById(R.id.msj_kaynak);
        TextView msj_saat = (TextView)convertView.findViewById(R.id.msj_saat);
        TextView msj_mesaj = (TextView)convertView.findViewById(R.id.msj_mesaj);
        JSONObject current_data = getItem(position);
        try {
            msj_kaynak.setText( current_data.getString("kaynak") );
            msj_saat.setText( current_data.getString("saat") );
            msj_mesaj.setText( current_data.getString("mesaj") );
        } catch( JSONException e ){
            e.printStackTrace();
        }


        return convertView;
    }


}
