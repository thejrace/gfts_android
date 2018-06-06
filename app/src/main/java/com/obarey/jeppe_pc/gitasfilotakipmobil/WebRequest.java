package com.obarey.jeppe_pc.gitasfilotakipmobil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class WebRequest {

    public static String MOBIL_SERVIS_URL = "http://178.18.206.163/web_servis/servis2.php";
    private boolean kullanici_params = true;

    public JSONObject multipart_req(String urlTo, Map<String, String> parmas, ArrayList<String> files ){
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";
        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());

            for( int j = 0; j < files.size(); j++ ){

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"f1_"+j+"\"; filename=\"fn_"+j+"\"" + lineEnd);
                outputStream.writeBytes("Content-Type: image/jpg" + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                outputStream.writeBytes(lineEnd);

                File file = new File(files.get(j));
                FileInputStream fileInputStream = new FileInputStream(file);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                fileInputStream.close();
            }

            parmas.put("mobil", "true" );
            if( kullanici_params ){
                parmas.put("eposta", "test2@test2.com" );
                parmas.put("tel_hash", "test mobil hash");
                parmas.put("tel_adi", "test mobil isim" );
            }

            // FORM DATA
            Iterator<String> keys = parmas.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = parmas.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            if (200 != connection.getResponseCode()) {
                throw new Exception("İstek başarısız! " + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();
            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            System.out.println(result);
            return new JSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject req( String urlTo, String params ){

        if( kullanici_params ){
            params += "&mobil=true&eposta="+UserConfig.eposta+"&tel_hash=test mobil hash&tel_adi=test mobil isim";
        }

        String output;
        HttpURLConnection connection = null;
        System.out.println("İstek yapılıyor.. ( URL : " + urlTo );
        try {

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded; charset=ISO-8859-1");
            connection.setRequestProperty("Content-Length",
                    Integer.toString(params.getBytes().length));
            connection.setRequestProperty("Content-Language", "tr-TR");
            connection.setRequestProperty( "charset", "ISO-8859-1");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            byte[] utf8JsonString = params.getBytes("UTF8");
            wr.write(utf8JsonString, 0, utf8JsonString.length);
            wr.close();

            // donen
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // StringBuffer Java 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());
            return new JSONObject(response.toString());
        } catch (Exception e) {
            System.out.println("İstek yapılırken bir hata oluştu. Tekrar deneniyor.");
            e.printStackTrace();
            //req( urlTo, params );
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    public void kullanici_parametresi_ekleme(){
        kullanici_params = false;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


}