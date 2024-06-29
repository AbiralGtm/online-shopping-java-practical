package com.abiralgautam.onlineshopping;

import com.abiralgautam.onlineshopping.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label responseLabel;

    @FXML
    private void login()
    {
        String username = this.username.getText();
        String password = this.password.getText();

        try{
            String response = sendLoginPost("http://localhost:8080/onlineshopping/auth/login",username,password);
            if(response.equals("login_successful")){
                App.setRoot("home");
            }else{
                responseLabel.setText(response);
                responseLabel.setStyle("-fx-text-fill: red");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseLabel.setText("Error while logging in");
        }

    }

    private String sendLoginPost(String url, String username, String password) throws Exception{
        URL loginUrl = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        String postData = "username=" + username+ "&password="+password;

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

    @FXML
    private void returnToHome() throws IOException
    {
        App.setRoot("home");
    }
}
