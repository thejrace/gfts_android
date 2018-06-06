package com.obarey.jeppe_pc.gitasfilotakipmobil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class SurucuListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    @Override
    public int getCount() {
        /*try {
            return data.getJSONArray(aktif_key).length();
        } catch( JSONException e ){
            e.printStackTrace();
            return 0;
        }*/
        return 0;
    }

    @Override
    public JSONObject getItem(int position) {
        /*try {
            return data.getJSONArray(aktif_key).getJSONObject(position);
        } catch( JSONException e ){
            e.printStackTrace();
            return null;
        }*/

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.surucu_row, parent, false);
        }

        TextView surucu_calisma_saati = (TextView)convertView.findViewById(R.id.surucu_calisma_saati);
        TextView surucu_isim = (TextView)convertView.findViewById(R.id.surucu_isim);


        return convertView;
    }

}
