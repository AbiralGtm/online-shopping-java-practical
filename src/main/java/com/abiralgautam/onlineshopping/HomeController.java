package com.abiralgautam.onlineshopping;

import com.abiralgautam.onlineshopping.models.Product;
import com.abiralgautam.onlineshopping.models.CartItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, Integer> productIdColumn;

    @FXML
    private TableColumn<CartItem, String> productNameColumn;

    @FXML
    private TableColumn<CartItem, Double> productPriceColumn;

    @FXML
    private TableColumn<CartItem, Integer> productQuantityColumn;

    @FXML
    private TableColumn<CartItem, Double> totalPriceColumn;

    @FXML
    private VBox rootVBox;

    @FXML
    private Label grandTotalLabel;

    @FXML
    private StackPane stackPane;
    @FXML
    private VBox checkoutFormVBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField contactNumberField;
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    private static final String PRODUCT_API_URL = "http://localhost:8080/onlineshopping/api/products";
    private static final String ORDER_API_URL = "http://localhost:8080/onlineshopping/api/orders";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VBox productVBox = new VBox(10);
        productVBox.setPadding(new Insets(10));

        List<Product> products = fetchProducts();
        if (products != null) {
            for (Product product : products) {
                VBox productBox = createProductVBox(product);
                productVBox.getChildren().add(productBox);
            }
        }

        rootVBox.getChildren().add(productVBox);

        // Initialize cart table columns
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        productQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        // Bind cart items to the table
        cartTable.setItems(cartItems);

        cartItems.addListener((ListChangeListener<CartItem>) change -> updateGrandTotal());

        setCheckoutFormVisibility(false);
    }


    private List<Product> fetchProducts() {
        List<Product> productList = new ArrayList<>();
        try {
            String response = APIController.getData(PRODUCT_API_URL, "GET");
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Product product = new Product(
                        jsonObject.getInt("id"),
                        jsonObject.getString("name"),
                        jsonObject.getDouble("price"),
                        jsonObject.getString("description")
                );
                productList.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productList;
    }

    private VBox createProductVBox(Product product) {
        VBox productBox = new VBox(10);
        productBox.setPadding(new Insets(10));
        productBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: #f0f0f0;");

        // Product name
        Label nameLabel = new Label(product.getName());
        // Product price
        Label priceLabel = new Label("Rs." + product.getPrice());
        // Product description
        Label descriptionLabel = new Label(product.getDescription());

        // Quantity controls
        HBox quantityBox = new HBox(5);
        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField("1");
        quantityField.setPrefWidth(40);
        Button increaseButton = new Button("+");
        Button decreaseButton = new Button("-");

        increaseButton.setOnAction(e -> {
            int quantity = Integer.parseInt(quantityField.getText());
            quantityField.setText(String.valueOf(quantity + 1));
        });

        decreaseButton.setOnAction(e -> {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity > 1) {
                quantityField.setText(String.valueOf(quantity - 1));
            }
        });

        quantityBox.getChildren().addAll(quantityLabel, decreaseButton, quantityField, increaseButton);

        // Add to cart button
        Button addButton = createAddToCartButton(product.getId(), quantityField);

        // Add all components to the VBox
        productBox.getChildren().addAll(nameLabel, priceLabel, descriptionLabel, quantityBox, addButton);

        return productBox;
    }

    private Button createAddToCartButton(int productId, TextField quantityField) {
        Button button = new Button("Add to Cart");
        button.setUserData(productId);
        button.setOnAction(e -> handleAddToCart(productId, Integer.parseInt(quantityField.getText())));
        return button;
    }

    private void handleAddToCart(int productId, int quantity) {
        // Find product details for the given productId
        List<Product> products = fetchProducts();
        Product selectedProduct = null;
        for (Product product : products) {
            if (product.getId() == productId) {
                selectedProduct = product;
                break;
            }
        }

        if (selectedProduct != null) {
            // Create a new CartItem and add it to the cartItems list
            CartItem cartItem = new CartItem(productId, selectedProduct.getName(), selectedProduct.getPrice(), quantity);
            cartItems.add(cartItem);
            Alert addedToCart = new Alert(Alert.AlertType.INFORMATION);
            addedToCart.setTitle("Product Added To Cart");
            addedToCart.setHeaderText(null);
            addedToCart.setContentText("Product : "+selectedProduct.getName()+"\nQuantity:"+quantity);
            addedToCart.showAndWait();
        }
    }


    @FXML
    private void removeFromCart()
    {
        CartItem selectedProduct = cartTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Delete");
            confirmationAlert.setHeaderText("Remove Product From Cart");
            confirmationAlert.setContentText("Are you sure you want to remove the product : "+ selectedProduct.getProductName()+" from cart? ");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // User confirmed deletion, remove the selected item from the TableView and underlying data list
                    cartItems.remove(selectedProduct);

            }
        } else {
            // Inform the user that no item is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Product Selected");
            alert.setContentText("Please select a product to remove from cart.");
            alert.showAndWait();
        }
    }

    private void updateGrandTotal() {
        double grandTotal = 0.0;
        for (CartItem cartItem : cartItems) {
            grandTotal += cartItem.getTotalPrice();
        }
        grandTotalLabel.setText(String.format("Grand Total: Rs. %.2f", grandTotal));
    }

    private void setCheckoutFormVisibility(boolean visible) {
        checkoutFormVBox.setVisible(visible);
        checkoutFormVBox.setManaged(visible);
    }

    @FXML
    private void handleProceedToCheckout() {
        setCheckoutFormVisibility(true);
    }

    @FXML
    private void handleSubmitOrder() {
        String name = nameField.getText();
        String address = addressField.getText();
        String contactNumber = contactNumberField.getText();

        if (name.isEmpty() || address.isEmpty() || contactNumber.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all fields", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        JSONArray orderItems = new JSONArray();
        for (CartItem cartItem : cartItems) {
            JSONObject item = new JSONObject();
            item.put("productId", cartItem.getProductId());
            item.put("quantity", cartItem.getProductQuantity()); // Assuming quantity is 1 for each product here
            item.put("price", cartItem.getProductPrice());
            item.put("total_price", cartItem.getTotalPrice()); // Assuming total price is same as price for simplicity
            orderItems.put(item);
        }

        JSONObject order = new JSONObject();
        order.put("name", name);
        order.put("address", address);
        order.put("contactNumber", contactNumber);
        order.put("items", orderItems);

        String formData = "name=" + name +
                "&address=" + address +
                "&contactNumber=" + contactNumber +
                "&items=" + orderItems.toString();
        try {
            String response = APIController.sendPost(ORDER_API_URL, formData);

            // Parse response and show appropriate alert
            JSONObject responseObject = new JSONObject(response);
            String status = responseObject.getString("status");

            if ("order_placed_successfully".equals(status)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Order placed successfully!", ButtonType.OK);
                alert.showAndWait();

                // Clear cart and reset form
                cartItems.clear();
                nameField.clear();
                addressField.clear();
                contactNumberField.clear();
                setCheckoutFormVisibility(false);
                updateGrandTotal();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to place order: " + status, ButtonType.OK);
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to place order", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void goToProducts() throws IOException
    {
        App.setRoot("products");
    }

    @FXML
    private void goToOrders() throws IOException
    {
        App.setRoot("orders");
    }
}
