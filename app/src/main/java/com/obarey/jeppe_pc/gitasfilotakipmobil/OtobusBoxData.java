package com.obarey.jeppe_pc.gitasfilotakipmobil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jeppe-PC on 10/25/2017.
 * Listeview icin kullanilacak adapter data tipi
 */

public class OtobusBoxData implements Serializable{

    private String oto, aktif_plaka, ruhsat_plaka, aktif_sefer_data;
    private ArrayList<SeferData> seferler = new ArrayList<>();

    public OtobusBoxData( String _oto, String _aktif_plaka, String _ruhsat_plaka ){
        oto = _oto;
        aktif_plaka = _aktif_plaka;
        ruhsat_plaka = _ruhsat_plaka;
        // listview thead
        seferler.add(new SeferData(
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
    public void reset_sefer_data(){
        seferler = new ArrayList<SeferData>();
    }
    public void set_aktif_sefer_data(String _aktif_sefer_data){
        aktif_sefer_data = _aktif_sefer_data;
    }
    public void add_sefer_data( SeferData _seferdata ){
        seferler.add( _seferdata );
    }

    public void set_sefer_data( ArrayList<SeferData> sefer_data ){
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
    public ArrayList<SeferData> get_seferler(){
        return seferler;
    }

}

