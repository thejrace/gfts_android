package com.obarey.jeppe_pc.gitasfilotakipmobil;

import java.io.Serializable;

public class OtobusPopupData implements Serializable {

    private String oto, plaka, hat, notf, main_notf, durum, tarih, ruhsat_plaka;

    private String tamam_seferler, bekleyen_seferler, iptal_seferler, yarim_seferler, aktif_seferler;
    public OtobusPopupData(
            String _oto,
            String _plaka,
            String _ruhsat_plaka,
            String _hat,
            String _main_notf,
            String _notf,
            String _bekleyen_seferler,
            String _tamam_seferler,
            String _aktif_seferler,
            String _iptal_seferler,
            String _yarim_seferler,
            String _durum,
            String _tarih ){
        oto = _oto;
        plaka = _plaka;
        ruhsat_plaka = _ruhsat_plaka;
        hat = _hat;
        main_notf = _main_notf;
        notf = _notf;
        tamam_seferler = _tamam_seferler;
        bekleyen_seferler = _bekleyen_seferler;
        iptal_seferler = _iptal_seferler;
        yarim_seferler = _yarim_seferler;
        aktif_seferler = _aktif_seferler;
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
    public void set_tamam_seferler( String _d ){
        tamam_seferler = _d;
    }
    public void set_iptal_seferler( String _d ){
        iptal_seferler = _d;
    }
    public void set_yarim_seferler( String _d ){
        yarim_seferler = _d;
    }
    public void set_bekleyen_seferler( String _d ){
        bekleyen_seferler = _d;
    }
    public void set_aktif_seferler( String _d ){
        aktif_seferler = _d;
    }
    public String get_tamam_seferler( ){
        return tamam_seferler;
    }
    public String get_iptal_seferler( ){
        return iptal_seferler;
    }
    public String get_yarim_seferler(){
        return yarim_seferler;
    }
    public String get_bekleyen_seferler(  ){
        return bekleyen_seferler;
    }
    public String get_aktif_seferler(  ){
        return aktif_seferler;
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
    public String get_durum(){
        return durum;
    }
    public String get_tarih(){
        return tarih;
    }

}
