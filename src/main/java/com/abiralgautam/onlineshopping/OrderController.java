package com.abiralgautam.onlineshopping;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

import com.abiralgautam.onlineshopping.models.Order;
import com.abiralgautam.onlineshopping.models.OrderProduct;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class OrderController implements Initializable {

    @FXML
    private TableView<Order> orderTableView;

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    @FXML
    private TableColumn<Order, String> customerNameColumn;

    @FXML
    private TableColumn<Order, String> deliveryAddressColumn;

    @FXML
    private TableColumn<Order, String> contactNumberColumn;

    @FXML
    private TableColumn<Order, Date> orderDateColumn;

    @FXML
    private TableView<OrderProduct> orderedProductsTableView;

    @FXML
    private TableColumn<OrderProduct, Integer> productIdColumn;

    @FXML
    private TableColumn<OrderProduct, String> productNameColumn;

    @FXML
    private TableColumn<OrderProduct, String> productDescriptionColumn;

    @FXML
    private TableColumn<OrderProduct, Integer> quantityColumn;

    @FXML
    private TableColumn<OrderProduct, Double> priceColumn;

    @FXML
    private TableColumn<OrderProduct, Double> totalPriceColumn;

    @FXML
    private VBox orderDetailsPane;

    @FXML
    private Label grandTotalLabel;

    private static final String ORDER_API = "http://localhost:8080/onlineshopping/api/orders";

    private ObservableList<Order> orderList = FXCollections.observableArrayList();
    private ObservableList<OrderProduct> orderedProducts = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns for orders
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        deliveryAddressColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));
        contactNumberColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        // Initialize table columns for ordered products
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("productDescription"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        orderedProductsTableView.setItems(orderedProducts);

        // Fetch orders and populate the main table
        fetchOrders();

        // Add listener to orderTableView to show ordered products on order click
        orderTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showOrderDetails(newSelection);
            }
        });
    }

    private void fetchOrders() {
        try {
            String json = APIController.getData(ORDER_API, "GET");
            List<Order> orders = parseOrders(json);
            orderList.addAll(orders);
            orderTableView.setItems(orderList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Order> parseOrders(String json) {
        JSONArray jsonArray = new JSONArray(json);
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject orderJson = jsonArray.getJSONObject(i);
            int orderId = orderJson.getInt("id");
            String customerName = orderJson.getString("customer_name");
            String deliveryAddress = orderJson.getString("delivery_address");
            String contactNumber = orderJson.getString("contact_number");
            String orderDate = orderJson.getString("order_date");

            // Parse ordered products
            JSONArray productsJsonArray = orderJson.getJSONArray("products");
            List<OrderProduct> orderedProducts = new ArrayList<>();

            for (int j = 0; j < productsJsonArray.length(); j++) {
                JSONObject productJson = productsJsonArray.getJSONObject(j);
                int productId = productJson.getInt("product_id");
                String productName = productJson.getString("product_name");
                String productDescription = productJson.getString("product_description");
                int quantity = productJson.getInt("quantity");
                double price = productJson.getDouble("unit_price");
                double totalPrice = productJson.getDouble("total_price");

                OrderProduct orderProduct = new OrderProduct(productId, productName, productDescription, quantity, price, totalPrice);
                orderedProducts.add(orderProduct);
            }

            Order order = new Order(orderId, customerName, deliveryAddress, contactNumber, orderDate, orderedProducts);
            orders.add(order);
        }

        return orders;
    }

    private void showOrderDetails(Order order) {
        orderedProducts.clear();
        orderedProducts.addAll(order.getOrderedProducts());
        orderDetailsPane.setVisible(true);
        updateGrandTotal();
    }

    private void updateGrandTotal() {
        double grandTotal = orderedProducts.stream().mapToDouble(OrderProduct::getTotalPrice).sum();
        grandTotalLabel.setText(String.format("Grand Total: Rs. %.2f", grandTotal));
    }
}
