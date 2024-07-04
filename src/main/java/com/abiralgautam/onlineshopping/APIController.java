package com.abiralgautam.onlineshopping;

import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class APIController {
    public static String sendPost(String url, String postData) throws Exception{
        URL productUrl = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) productUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        try(OutputStream os = connection.getOutputStream()){
            os.write(postData.getBytes());
            os.flush();
        }

        int responseCode = connection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            }
        }else{
            return "Failed: HTTP error code:" + responseCode;
        }
    }


    public static String getData(String getDataUrl, String method) throws Exception {
        URL getUrl = new URL(getDataUrl);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod(method);

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            }
        } else {
            throw new RuntimeException("Failed: HTTP error code: " + responseCode);
        }
    }

}
