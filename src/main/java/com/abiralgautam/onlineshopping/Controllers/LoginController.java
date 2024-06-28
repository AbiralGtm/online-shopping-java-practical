package com.abiralgautam.onlineshopping.Controllers;

import com.abiralgautam.onlineshopping.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;
    @FXML
    private void login()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Username"+ username.getText() + "pas"+password.getText());
        alert.showAndWait();
    }

    @FXML
    private void returnToHome() throws IOException
    {
        App.setRoot("home");
    }
}
