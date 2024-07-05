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

public class OrderServlet extends HttpServlet {

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
            String orderProductSQL = "INSERT INTO order_products(order_id, product_id) VALUES(?, ?)";
            PreparedStatement orderProductStmt = connection.prepareStatement(orderProductSQL);

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);
                int productId = item.getInt("productId");
                int quantity = item.getInt("quantity");

                for (int j = 0; j < quantity; j++) {
                    orderProductStmt.setInt(1, orderId);
                    orderProductStmt.setInt(2, productId);
                    orderProductStmt.addBatch();
                }
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
