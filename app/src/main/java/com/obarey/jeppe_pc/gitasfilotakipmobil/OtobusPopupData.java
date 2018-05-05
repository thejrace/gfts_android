package com.obarey.jeppe_pc.gitasfilotakipmobil;

import java.io.Serializable;

public class OtobusPopupData implements Serializable {

    private String oto, plaka, hat, notf, main_notf, sefer_ozet, durum, tarih, ruhsat_plaka;
    public OtobusPopupData( String _oto, String _plaka, String _ruhsat_plaka, String _hat, String _main_notf, String _notf, String _sefer_ozet, String _durum, String _tarih ){
        oto = _oto;
        plaka = _plaka;
        ruhsat_plaka = _ruhsat_plaka;
        hat = _hat;
        main_notf = _main_notf;
        notf = _notf;
        sefer_ozet = _sefer_ozet;
        tarih = _tarih;
        durum = _durum;
    }

    public void set_hat( String _hat ){
        hat = _hat;
    }
    public void set_main_notf( String _main_notf ){
        main_notf = _main_notf;
    }
    public void set_notf( String _notf ){
        notf = _notf;
    }
    public void set_sefer_ozet( String _sefer_ozet ){
        sefer_ozet = _sefer_ozet;
    }
    public void set_tarih( String _tarih ){
        tarih = _tarih;
    }
    public void set_durum( String _durum ){
        durum = _durum;
    }

    public String get_oto(){
        return oto;
    }
    public String get_plaka(){
        return plaka;
    }
    public String get_ruhsat_plaka(){
        return ruhsat_plaka;
    }
    public String get_hat(){
        return hat;
    }
    public String get_main_notf(){
        return main_notf;
    }
    public String get_notf(){
        return notf;
    }
    public String get_sefer_ozet(){
        return sefer_ozet;
    }
    public String get_durum(){
        return durum;
    }
    public String get_tarih(){
        return tarih;
    }

}
