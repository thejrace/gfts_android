package com.example.jeppe_pc.gitasmobil;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeppe-PC on 10/25/2017.
 */

public class FiloLogin {

    private Map<String, String> cookies = new HashMap<>();
    private int counter;
    public FiloLogin( final JSONObject login_data, final FiloLoginListener listener ){

        cookies.put("A", "INIT");
        cookies.put("B", "INIT");
        cookies.put("C", "INIT");

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject bolge;
                try {
                    bolge =  login_data.getJSONObject("A");
                    login_thread( "A", bolge.getString("login"), bolge.getString("pass") );

                    bolge =  login_data.getJSONObject("B");
                    login_thread( "B", bolge.getString("login"), bolge.getString("pass") );

                    bolge =  login_data.getJSONObject("C");
                    login_thread( "C", bolge.getString("login"), bolge.getString("pass") );

                    while( counter < 3 ){
                        System.out.println("Login threads bekleniyor.");
                        try {
                            Thread.sleep(3000);
                        } catch( InterruptedException e ){
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Login threads tamamlandı.");
                    listener.on_finish( cookies );
                } catch( JSONException e ){
                    e.printStackTrace();
                }
            }
        });
        th.setDaemon(true);
        th.start();

    }

    private void login_thread( final String bolge, final String login, final String pass ){
        System.out.println(bolge + " login thread başladı.");
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                org.jsoup.Connection.Response res;
                try {
                    res = Jsoup.connect("http://filo5.iett.gov.tr/login.php?sayfa=/_FYS.php&aday=x")
                            .data("login", login, "password", pass )
                            .method(org.jsoup.Connection.Method.POST)
                            .timeout(0)
                            .execute();
                    cookies.put(bolge, res.cookies().get("PHPSESSID"));
                    counter++;
                    System.out.println(bolge + " login thread tamamlandı.");
                } catch( IOException e ){
                    e.printStackTrace();
                    login_thread( bolge, login, pass );
                    System.out.println(bolge + " login thread hatası. Tekrar deneniyor.");
                }


                counter++;
            }
        });
        th.setDaemon(true);
        th.start();
    }

}
