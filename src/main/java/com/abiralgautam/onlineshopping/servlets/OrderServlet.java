package com.abiralgautam.onlineshopping.servlets;
import com.abiralgautam.onlineshopping.DatabaseConnection;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT " +
                    "    o.id AS order_id, " +
                    "    o.name AS customer_name, " +
                    "    o.address AS delivery_address, " +
                    "    o.contact AS contact_number, " +
                    "    o.ordered_on AS order_date, " +
                    "    op.id AS product_id, " +
                    "    p.name AS product_name, " +
                    "    p.description AS product_description, " +
                    "    op.quantity, " +
                    "    op.price AS unit_price, " +
                    "    op.total_price " +
                    "FROM " +
                    "    orders o " +
                    "INNER JOIN " +
                    "    order_products op ON o.id = op.order_id " +
                    "INNER JOIN " +
                    "    products p ON op.product_id = p.id " +
                    "ORDER BY " +
                    "    o.id, op.id";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            JSONArray ordersArray = new JSONArray();

            int currentOrderId = -1;
            JSONObject orderObject = null;
            JSONArray productsArray = null;

            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");

                // If it's a new order, create a new order object
                if (orderId != currentOrderId) {
                    // Save the previous order object (if exists) into the orders array
                    if (orderObject != null) {
                        orderObject.put("products", productsArray);
                        ordersArray.put(orderObject);
                    }

                    // Create a new order object
                    orderObject = new JSONObject();
                    orderObject.put("id", orderId);
                    orderObject.put("customer_name", resultSet.getString("customer_name"));
                    orderObject.put("delivery_address", resultSet.getString("delivery_address"));
                    orderObject.put("contact_number", resultSet.getString("contact_number"));
                    orderObject.put("order_date", resultSet.getDate("order_date"));

                    // Initialize products array for the new order
                    productsArray = new JSONArray();

                    // Update current order id
                    currentOrderId = orderId;
                }

                // Create product object for the current row and add it to products array
                JSONObject productObject = new JSONObject();
                productObject.put("product_id", resultSet.getInt("product_id"));
                productObject.put("product_name", resultSet.getString("product_name"));
                productObject.put("product_description", resultSet.getString("product_description"));
                productObject.put("quantity", resultSet.getInt("quantity"));
                productObject.put("unit_price", resultSet.getDouble("unit_price"));
                productObject.put("total_price", resultSet.getDouble("total_price"));

                productsArray.put(productObject);
            }

            // Add the last order object to orders array
            if (orderObject != null) {
                orderObject.put("products", productsArray);
                ordersArray.put(orderObject);
            }

            // Send JSON response
            out.print(ordersArray.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Connection connection = null;

        try{
            String name = request.getParameter("name");
            String address = request.getParameter("address");
            String  contactNumber = request.getParameter("contactNumber");
            String  itemsJson = request.getParameter("items");

            // Parse the items JSON
            JSONArray itemsArray = new JSONArray(itemsJson);

            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Insert the order into the orders table
            String orderSQL = "INSERT INTO orders(name, address, contact, ordered_on) VALUES(?, ?, ?, ?)";
            PreparedStatement orderStmt = connection.prepareStatement(orderSQL, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setString(1, name);
            orderStmt.setString(2, address);
            orderStmt.setString(3, contactNumber);
            orderStmt.setDate(4, Date.valueOf(LocalDate.now())); // Example: Using LocalDate

            int affectedRows = orderStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            // Get the generated order ID
            int orderId;
            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            // Insert each product in the order into the order_products table
            String orderProductSQL = "INSERT INTO order_products(order_id, product_id, quantity, price, total_price) VALUES(?, ?,?,?,?)";
            PreparedStatement orderProductStmt = connection.prepareStatement(orderProductSQL);

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);
                int productId = item.getInt("productId");
                int quantity = item.getInt("quantity");
                double price = item.getDouble("price");

                // Calculate total price for each item
                double totalPrice = quantity * price;

                orderProductStmt.setInt(1, orderId);
                orderProductStmt.setInt(2, productId);
                orderProductStmt.setInt(3, quantity);
                orderProductStmt.setDouble(4, price);
                orderProductStmt.setDouble(5, totalPrice);
                orderProductStmt.addBatch();
            }

            orderProductStmt.executeBatch();

            // Commit the transaction
            connection.commit();
            out.println("{\"status\":\"order_placed_successfully\"}");

        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\":\"sql_error\", \"message\":\"" + e.getMessage() + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\":\"exception\", \"message\":\"" + e.getMessage() + "\"}");

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    DatabaseConnection.closeConnection(connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
