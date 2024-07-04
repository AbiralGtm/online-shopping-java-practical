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
            String postData = "username=" + username+ "&password="+password;

            String response = APIController.sendPost("http://localhost:8080/onlineshopping/auth/login",postData);
            if(response.equals("login_successful")){
                App.setRoot("products");
            }else{
                responseLabel.setText(response);
                responseLabel.setStyle("-fx-text-fill: red");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseLabel.setText("Error while logging in");
        }

    }

    @FXML
    private void returnToHome() throws IOException
    {
        App.setRoot("home");
    }
}
