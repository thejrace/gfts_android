package com.example.jeppe_pc.gitasmobil;

import org.json.JSONArray;

import java.io.Serializable;

/**
 * Created by Jeppe-PC on 10/25/2017.
 * Listeview icin kullanilacak adapter data tipi
 */

public class OtobusBoxData implements Serializable{

    private String oto, aktif_plaka, ruhsat_plaka, aktif_sefer_data;
    private JSONArray seferler = new JSONArray();

    public OtobusBoxData( String _oto, String _aktif_plaka, String _ruhsat_plaka ){
        oto = _oto;
        aktif_plaka = _aktif_plaka;
        ruhsat_plaka = _ruhsat_plaka;
    }

    public void set_aktif_sefer_data(String _aktif_sefer_data){
        aktif_sefer_data = _aktif_sefer_data;
    }
    public void set_sefer_data( JSONArray sefer_data ){
        seferler = sefer_data;
    }

    public String get_oto(){
        return oto;
    }
    public String get_aktif_plaka(){
        return aktif_plaka;
    }
    public String get_ruhsat_plaka(){
        return ruhsat_plaka;
    }
    public String get_aktif_sefer_data(){
        return aktif_sefer_data;
    }
    public JSONArray get_seferler(){
        return seferler;
    }

}
