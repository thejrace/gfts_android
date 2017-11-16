package com.example.jeppe_pc.gitasmobil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jeppe-PC on 10/27/2017.
 */

public class Common {
    public static String rev_datetime( String dt ){
        String date = dt.substring(0, 10);
        String[] exp = date.split("-");
        return exp[2]+"-"+exp[1]+"-"+exp[0]+ " " + dt.substring(11);
    }

    public static String rev_date( String dt ){
        String[] exp = dt.split("-");
        return  exp[2]+"-"+exp[1]+"-"+exp[0];
    }

    public static String iys_to_date( String iys_tarih ){
        String[] exp = iys_tarih.split("\\.");
        return "20"+exp[2]+"-"+exp[1]+"-"+exp[0];
    }

    public static boolean is_numeric( String val ){
        return val.matches("\\d+");
    }

    public static String hat_kod_sef( String hat_kodu ){
        if( hat_kodu.indexOf("Ç") > 0 ) hat_kodu = hat_kodu.replace("Ç", "C.");
        if( hat_kodu.indexOf("Ş") > 0 ) hat_kodu = hat_kodu.replace("Ş", "S.");
        if( hat_kodu.indexOf("Ü") > 0 ) hat_kodu = hat_kodu.replace("Ü", "U.");
        if( hat_kodu.indexOf("Ö") > 0 ) hat_kodu = hat_kodu.replace("Ö", "O.");
        if( hat_kodu.indexOf("İ") > 0 ) hat_kodu = hat_kodu.replace("İ", "I.");
        return hat_kodu;

    }

    public static String regex_trim( String str ){
        return str.replaceAll("\u00A0", "");
    }


    public static long get_unix() {
        return (System.currentTimeMillis() / 1000L) - 3600;
    }

    public static String get_current_datetime(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String get_current_date(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String get_current_hmin(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return dateFormat.format(date);

    }


}
