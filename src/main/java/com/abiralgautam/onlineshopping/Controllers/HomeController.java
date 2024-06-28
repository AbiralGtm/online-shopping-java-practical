package com.abiralgautam.onlineshopping.Controllers;

import com.abiralgautam.onlineshopping.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.io.IOException;

public class HomeController {

    @FXML
    private ListView<String> listView;

    @FXML
    public void initialize() {
        listView.getItems().add("Item 1");
        listView.getItems().add("Item 2");
        listView.getItems().add("Item 3");
    }

    @FXML
    private void addToCart()
    {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Added to Cart");
        alert.setHeaderText(null);
        alert.setContentText(selectedItem != null ? "Item : "+ selectedItem : "No item selected");
        alert.showAndWait();
    }

    @FXML
    private  void goToLoginPage() throws IOException
    {
        App.setRoot("login");
    }
}
