package com.abiralgautam.onlineshopping.servlets;

import com.abiralgautam.onlineshopping.DatabaseConnection;
import com.abiralgautam.onlineshopping.models.Product;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        try{
            connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM products");
            ResultSet resultSet = statement.executeQuery();
            JSONArray jsonArray = new JSONArray();
            while (resultSet.next()) {
                JSONObject productJson = new JSONObject();
                productJson.put("id", resultSet.getInt("id"));
                productJson.put("name", resultSet.getString("name"));
                productJson.put("price", resultSet.getDouble("price"));
                productJson.put("description", resultSet.getString("description"));
                jsonArray.put(productJson);
            }

            out.print(jsonArray.toString());
        }catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e){

            e.printStackTrace();
        }finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productName = request.getParameter("name");
        String productDescription = request.getParameter("description");
        String  productPrice = request.getParameter("price");
        PrintWriter out = response.getWriter();
        Connection connection = null;
        try{
            connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO products(name,price,description) VALUES(?,?,?)");
            statement.setString(1, productName);
            statement.setString(2, productPrice);
            statement.setString(3, productDescription);

            int insertData = statement.executeUpdate();
            if (insertData == 1) {
                out.println("product_added_successfully");
            } else {
                out.println("error_while_adding_product");
            }
        }catch (SQLException e){
            e.printStackTrace();
//            out.println(e.getMessage());

        }catch (Exception e){

            e.printStackTrace();
//            out.println(e.getMessage());

        }finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdStr = request.getParameter("id");

        int productId = Integer.parseInt(productIdStr);

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE id = ?");
            statement.setInt(1, productId);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("product_deleted_successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Product not found with ID: " + productId);
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error deleting product: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }
}
