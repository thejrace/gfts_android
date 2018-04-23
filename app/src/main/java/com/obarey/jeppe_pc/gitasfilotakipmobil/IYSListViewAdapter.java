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

public class IYSListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private JSONArray data;

    public IYSListViewAdapter(Activity activity ){
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void set_data( JSONArray _data ) {
        data = _data;
    }

    public void add_data( JSONArray _data ){
        for( int k = 0; k < _data.length(); k++ ){
            try {
                data.put( _data.getJSONObject(k) );
            } catch( JSONException e ){
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return data.getJSONObject(position);
        } catch( JSONException e ){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
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
            msj_saat.setText( Common.rev_date(current_data.getString("tarih")) );
            msj_mesaj.setText( current_data.getString("ozet") );
        } catch( JSONException e ){
            e.printStackTrace();
        }

        return convertView;
    }

}
