package com.abiralgautam.onlineshopping;

import com.abiralgautam.onlineshopping.models.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProductController {
    @FXML
    private TableView<Product> tableView;
    @FXML
    private TableColumn<Product, Integer> id;

    @FXML
    private TableColumn<Product, String> name;

    @FXML
    private TableColumn<Product, Double> price;
    @FXML
    private TableColumn<Product, String> description;

    @FXML
    private TextField productName;

    @FXML
    private TextArea productDescription;

    @FXML
    private TextField productPrice;

    @FXML
    private Label responseLabel;

    private static final String API_URL = "http://localhost:8080/onlineshopping/api/products";

    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        //only allow adding number in price field
        productPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                productPrice.setText(oldValue);
            }
        });

        id.setCellValueFactory(new PropertyValueFactory<Product,Integer>("id"));
        name.setCellValueFactory(new PropertyValueFactory<Product,String>("name"));
        price.setCellValueFactory(new PropertyValueFactory<Product,Double>("price"));
        description.setCellValueFactory(new PropertyValueFactory<Product,String>("description"));

        tableView.setItems(productList);

        // Fetch products and populate the table
        try {
            String json = APIController.getData(API_URL,"GET");
            List<Product> products = parseProducts(json);
            productList.addAll(products);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private List<Product> parseProducts(String json) {
        JSONArray jsonArray = new JSONArray(json);
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Product product = new Product(jsonObject.getInt("id"),jsonObject.getString("name"),jsonObject.getDouble("price"),jsonObject.getString("description"));
            products.add(product);
        }

        return products;
    }
    @FXML
    private void addProduct() {
        String productName = this.productName.getText();
        String productDescription = this.productDescription.getText();
        String productPrice = this.productPrice.getText();
        try{
            String postData = "name=" + productName+ "&price="+productPrice + "&description="+ productDescription;

            String response = APIController.sendPost(API_URL,postData);
            if(response.equals("product_added_successfully")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Create Product");
                alert.setHeaderText(null);
                alert.setContentText("Product added successfully");
                alert.showAndWait();
                this.productName.clear();
                this.productPrice.clear();
                this.productDescription.clear();
                refreshTable();
            }else{
                responseLabel.setText(response);
                responseLabel.setStyle("-fx-text-fill: red");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseLabel.setText("Error while adding product");
        }

    }

    private void refreshTable() {
        try {
            String json = APIController.getData(API_URL,"GET");
            List<Product> products = parseProducts(json);

            // Clear old data and add new data
            productList.clear();
            productList.addAll(products);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteProduct()
    {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Delete");
            confirmationAlert.setHeaderText("Delete Product");
            confirmationAlert.setContentText("Are you sure you want to delete the product with ID " + selectedProduct.getId() + "?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // User confirmed deletion, remove the selected item from the TableView and underlying data list
                try {
                    String url = API_URL+selectedProduct.getId();
                    String response = APIController.getData(url,"DELETE");
                    if(response.equals("product_deleted_successfully")) {
                        productList.remove(selectedProduct);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else {
            // Inform the user that no item is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Product Selected");
            alert.setContentText("Please select a product to delete.");
            alert.showAndWait();
        }
    }

    @FXML
    private void goToHome() throws IOException
    {
        App.setRoot("home");
    }

    @FXML
    private void goToOrders() throws IOException
    {
        App.setRoot("orders");
    }
}
